package reservas.server;

import reservas.logic.Protocol;
import reservas.logic.Reserva;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Server {
    private final ServerSocket srv;
    private final Service service;
    private final List<Worker> workers = new ArrayList<>();
    private volatile boolean activo = true;

    public Server() throws Exception {
        this(Protocol.PORT, Path.of("datos", "reservas.xml"));
    }

    public Server(int puerto, Path archivo) throws Exception {
        this.srv = new ServerSocket(puerto);
        this.service = new Service(archivo);
        if (service.estaVacio()) service.sembrarDemo();
        System.out.println("Servidor iniciado en el puerto " + srv.getLocalPort());
    }

    public int getPuerto() {
        return srv.getLocalPort();
    }

    public Service getService() {
        return service;
    }

    public void run() {
        while (activo) {
            try {
                Socket s = srv.accept();
                ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
                os.flush();
                ObjectInputStream is = new ObjectInputStream(s.getInputStream());
                int tipo = is.readInt();
                if (tipo == Protocol.SYNC) {
                    String sid = UUID.randomUUID().toString();
                    Worker worker = new Worker(this, s, os, is, sid, service);
                    add(worker);
                    worker.start();
                    os.writeObject(sid);
                    os.flush();
                } else if (tipo == Protocol.ASYNC) {
                    Object sid = is.readObject();
                    if (sid instanceof String id && !id.isBlank()) join(s, os, is, id);
                    else cerrar(s);
                } else {
                    cerrar(s);
                }
            } catch (Exception ex) {
                if (activo) System.out.println(ex.getMessage());
            }
        }
    }

    public void add(Worker worker) {
        synchronized (workers) {
            workers.add(worker);
        }
    }

    public void remove(Worker worker) {
        synchronized (workers) {
            workers.remove(worker);
        }
    }

    public void join(Socket as, ObjectOutputStream aos, ObjectInputStream ais, String sid) {
        synchronized (workers) {
            for (Worker worker : workers) {
                if (worker.sid.equals(sid)) {
                    worker.setAs(as, aos, ais);
                    return;
                }
            }
        }
        cerrar(as);
    }

    public void deliver_reserva(Worker from, Reserva reserva) {
        synchronized (workers) {
            for (Worker worker : workers) {
                if (worker != from) worker.deliver_reserva(reserva);
            }
        }
    }

    public void detener() {
        activo = false;
        synchronized (workers) {
            for (Worker worker : new ArrayList<>(workers)) worker.detener();
            workers.clear();
        }
        try {
            srv.close();
        } catch (IOException ignored) { }
    }

    private static void cerrar(Socket socket) {
        try {
            socket.close();
        } catch (IOException ignored) { }
    }
}
