package backend.red;

import protocolo.Notificacion;
import protocolo.Respuesta;
import protocolo.Solicitud;
import logic.Funcionario;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class WorkerCliente implements Runnable {

    private final Socket           socket;
    private final ServidorTCP      servidor;
    private final ManejadorSolicitudes manejador;
    private ObjectOutputStream     salida;
    private volatile String usuarioId;
    private volatile String rol;

    public WorkerCliente(Socket socket, ServidorTCP servidor) {
        this.socket    = socket;
        this.servidor  = servidor;
        this.manejador = servidor.getManejador();
    }

    @Override
    public void run() {
        try {
            // OOS primero para evitar deadlock al abrir ambos streams
            salida = new ObjectOutputStream(socket.getOutputStream());
            salida.flush();
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());

            Object objeto;
            while ((objeto = entrada.readObject()) != null) {
                if (objeto instanceof Solicitud) {
                    Solicitud solicitud = (Solicitud) objeto;
                    Respuesta respuesta;
                    try {
                        respuesta = manejador.procesar(solicitud, this);
                    } catch (Exception e) {
                        respuesta = Respuesta.error(solicitud.getId(), e.getMessage());
                    }
                    enviarObjeto(respuesta);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            // Cliente desconectado — no es un error fatal
        } finally {
            servidor.removerCliente(this);
            try { socket.close(); } catch (IOException ignored) {}
            System.out.println("[Servidor] Cliente desconectado.");
        }
    }

    /** Envía una notificación asíncrona al cliente de este worker. */
    public synchronized void enviarNotificacion(Notificacion notificacion) {
        enviarObjeto(notificacion);
    }

    public void autenticar(Funcionario funcionario) {
        this.usuarioId = funcionario.getId();
        this.rol = funcionario.getRol();
    }

    public String getUsuarioId() { return usuarioId; }
    public String getRol() { return rol; }
    public boolean estaAutenticado() { return usuarioId != null; }
    public boolean esAdministrador() { return "ADMIN".equalsIgnoreCase(rol); }

    private synchronized void enviarObjeto(Object obj) {
        try {
            if (salida != null) {
                salida.writeObject(obj);
                salida.flush();
                salida.reset(); // evita que ObjectOutputStream cachee referencias
            }
        } catch (IOException e) {
            servidor.removerCliente(this);
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}
