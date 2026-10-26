package backend.red;

import protocolo.Notificacion;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorTCP {

    private final int puerto;
    private ManejadorSolicitudes manejador;
    private final List<WorkerCliente> clientes = Collections.synchronizedList(new ArrayList<>());
    private ServerSocket serverSocket;
    private volatile boolean ejecutando;
    private final ExecutorService despachadorAlertas = Executors.newVirtualThreadPerTaskExecutor();

    public ServidorTCP(int puerto) {
        this.puerto = puerto;
    }

    public void setManejador(ManejadorSolicitudes manejador) {
        this.manejador = manejador;
    }

    public ManejadorSolicitudes getManejador() {
        return manejador;
    }

    public int getPuertoActual() {
        return serverSocket == null ? puerto : serverSocket.getLocalPort();
    }

    public void iniciar() throws IOException {
        if (manejador == null) throw new IllegalStateException("Debe asignar un ManejadorSolicitudes antes de iniciar.");
        serverSocket = new ServerSocket(puerto);
        serverSocket.setReuseAddress(true);
        ejecutando   = true;
        System.out.println("[Servidor] Escuchando en puerto " + puerto);

        while (ejecutando) {
            try {
                Socket socket = serverSocket.accept();
                WorkerCliente worker = new WorkerCliente(socket, this);
                clientes.add(worker);
                Thread hilo = new Thread(worker);
                hilo.setDaemon(true);
                hilo.start();
                System.out.println("[Servidor] Cliente conectado: " + socket.getInetAddress());
            } catch (IOException e) {
                if (ejecutando) System.err.println("[Servidor] Error aceptando cliente: " + e.getMessage());
            }
        }
    }

    /** Envía una notificación a todos los clientes excepto el emisor. */
    public void publicarATodos(Notificacion notificacion, WorkerCliente emisor) {
        synchronized (clientes) {
            for (WorkerCliente cliente : clientes) {
                if (cliente != emisor) {
                    despachadorAlertas.submit(() -> cliente.enviarNotificacion(notificacion));
                }
            }
        }
    }

    public void removerCliente(WorkerCliente worker) {
        clientes.remove(worker);
    }

    public void detener() {
        ejecutando = false;
        try { if (serverSocket != null) serverSocket.close(); } catch (IOException ignored) {}
        despachadorAlertas.shutdownNow();
    }
}
