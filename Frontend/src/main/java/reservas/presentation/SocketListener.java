package reservas.presentation;

import reservas.logic.Protocol;
import reservas.logic.Reserva;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketListener {
    private final ThreadListener listener;
    private final Socket as;
    private final ObjectInputStream ais;
    private volatile boolean activo = true;

    public SocketListener(ThreadListener listener, String sid, String host, int puerto) throws Exception {
        this.listener = listener;
        as = new Socket(host, puerto);
        ObjectOutputStream aos = new ObjectOutputStream(as.getOutputStream());
        aos.flush();
        ais = new ObjectInputStream(as.getInputStream());
        aos.writeInt(Protocol.ASYNC);
        aos.writeObject(sid);
        aos.flush();
    }

    public void start() {
        Thread hilo = new Thread(new Runnable() {
            public void run() {
                listen();
            }
        });
        activo = true;
        hilo.start();
    }

    public void detener() {
        activo = false;
        try {
            as.close();
        } catch (IOException ignored) { }
    }

    public void listen() {
        while (activo) {
            try {
                int metodo = ais.readInt();
                if (metodo == Protocol.DELIVER_RESERVA) {
                    Object dato = ais.readObject();
                    if (dato instanceof Reserva reserva) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                listener.reservaCreada(reserva);
                            }
                        });
                    }
                }
            } catch (IOException ex) {
                activo = false;
            } catch (ClassNotFoundException ex) {
                activo = false;
            }
        }
        try {
            as.close();
        } catch (IOException ignored) { }
    }
}
