package frontend.red;

import protocolo.Notificacion;
import protocolo.Respuesta;
import protocolo.Solicitud;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ClienteSocket {

    private Socket              socket;
    private ObjectOutputStream  salida;
    private ObjectInputStream   entrada;
    private volatile ReceptorNotificaciones receptor;
    private volatile boolean conectado;
    private final Object candadoSalida = new Object();
    private final Map<String, CompletableFuture<Respuesta>> respuestasPendientes = new ConcurrentHashMap<>();

    public ClienteSocket(String host, int puerto) throws IOException {
        socket    = new Socket(host, puerto);
        salida    = new ObjectOutputStream(socket.getOutputStream());
        salida.flush();
        entrada   = new ObjectInputStream(socket.getInputStream());
        conectado = true;
        iniciarHiloLector();
    }

    /** Registra el objeto que recibirá las notificaciones asíncronas. */
    public void setReceptorNotificaciones(ReceptorNotificaciones receptor) {
        this.receptor = receptor;
    }

    /**
     * Envía una solicitud y espera la respuesta de forma síncrona.
     * Las notificaciones que lleguen mientras se espera se despachan al receptor.
     */
    public Respuesta enviar(Solicitud solicitud) throws Exception {
        if (!conectado) throw new IOException("Sin conexión con el Backend.");
        CompletableFuture<Respuesta> pendiente = new CompletableFuture<>();
        respuestasPendientes.put(solicitud.getId(), pendiente);
        try {
            synchronized (candadoSalida) {
                salida.writeObject(solicitud);
                salida.flush();
                salida.reset();
            }
            return pendiente.get(60, TimeUnit.SECONDS);
        } catch (Exception e) {
            respuestasPendientes.remove(solicitud.getId());
            throw e;
        } finally {
            respuestasPendientes.remove(solicitud.getId());
        }
    }

    private void iniciarHiloLector() {
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (conectado) {
                        Object obj = entrada.readObject();
                        if (obj instanceof Notificacion) {
                            despacharNotificacion((Notificacion) obj);
                        } else if (obj instanceof Respuesta) {
                            Respuesta respuesta = (Respuesta) obj;
                            CompletableFuture<Respuesta> pendiente = respuestasPendientes.get(respuesta.getIdSolicitud());
                            if (pendiente != null) pendiente.complete(respuesta);
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    conectado = false;
                    respuestasPendientes.values().forEach(f -> f.completeExceptionally(e));
                }
            }
        });
        hilo.setDaemon(true);
        hilo.setName("frontend-lector-notificaciones");
        hilo.start();
    }

    private void despacharNotificacion(Notificacion n) {
        if (receptor != null) receptor.recibirNotificacion(n);
    }

    public void cerrar() {
        conectado = false;
        respuestasPendientes.values().forEach(f -> f.completeExceptionally(new IOException("Conexión cerrada.")));
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
    }
}
