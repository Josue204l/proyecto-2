package backend.red;

import protocolo.EventoServidor;
import protocolo.JsonProtocolo;
import protocolo.MensajeRespuesta;
import protocolo.MensajeSolicitud;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/** Servidor TCP concurrente. Cada cliente conserva una conexión bidireccional. */
public final class ServidorSocket implements AutoCloseable {
    private final int puerto;
    private final ManejadorSolicitudes manejador;
    private final Set<ConexionCliente> clientes = ConcurrentHashMap.newKeySet();
    private final ExecutorService trabajadores = Executors.newVirtualThreadPerTaskExecutor();
    private final AtomicBoolean ejecutando = new AtomicBoolean();
    private ServerSocket servidor;

    public ServidorSocket(int puerto, ManejadorSolicitudes manejador) {
        if (puerto < 1 || puerto > 65535) throw new IllegalArgumentException("Puerto fuera de rango.");
        this.puerto = puerto;
        this.manejador = manejador;
    }

    public void iniciar() throws IOException {
        if (!ejecutando.compareAndSet(false, true)) return;
        servidor = new ServerSocket(puerto);
        while (ejecutando.get()) {
            try {
                Socket socket = servidor.accept();
                socket.setKeepAlive(true);
                ConexionCliente cliente = new ConexionCliente(socket);
                clientes.add(cliente);
                trabajadores.submit(cliente);
            } catch (IOException e) {
                if (ejecutando.get()) throw e;
            }
        }
    }

    /** Emite una alerta a todos los clientes conectados, excepto quien hizo la reserva. */
    public void publicar(EventoServidor evento, ConexionCliente emisor) {
        clientes.stream().filter(cliente -> cliente != emisor)
                .forEach(cliente -> cliente.enviar(evento));
    }

    @Override
    public void close() {
        ejecutando.set(false);
        if (servidor != null) {
            try { servidor.close(); } catch (IOException ignored) { }
        }
        clientes.forEach(ConexionCliente::close);
        trabajadores.shutdownNow();
    }

    private final class ConexionCliente implements Runnable, AutoCloseable {
        private final Socket socket;
        private final BufferedReader entrada;
        private final BufferedWriter salida;
        private final Object candadoSalida = new Object();

        private ConexionCliente(Socket socket) throws IOException {
            this.socket = socket;
            entrada = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            salida = new BufferedWriter(new java.io.OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        }

        @Override
        public void run() {
            try (this) {
                String linea;
                while ((linea = entrada.readLine()) != null) {
                    MensajeSolicitud solicitud;
                    String idSolicitud = null;
                    try {
                        solicitud = JsonProtocolo.mapper().readValue(linea, MensajeSolicitud.class);
                        idSolicitud = solicitud.id();
                        MensajeRespuesta respuesta = manejador.procesar(solicitud, evento -> publicar(evento, this));
                        enviar(respuesta);
                    } catch (Exception e) {
                        enviar(MensajeRespuesta.error(idSolicitud, mensajeSeguro(e)));
                    }
                }
            } catch (IOException ignored) {
                // La desconexión de un cliente no debe detener al servidor.
            } finally {
                clientes.remove(this);
            }
        }

        private void enviar(Object mensaje) {
            synchronized (candadoSalida) {
                try {
                    salida.write(JsonProtocolo.codificar(mensaje));
                    salida.newLine();
                    salida.flush();
                } catch (IOException e) {
                    close();
                }
            }
        }

        private String mensajeSeguro(Exception e) {
            String mensaje = e.getMessage();
            return mensaje == null || mensaje.isBlank() ? "No se pudo procesar la solicitud." : mensaje;
        }

        @Override
        public void close() {
            clientes.remove(this);
            try { socket.close(); } catch (IOException ignored) { }
        }
    }
}
