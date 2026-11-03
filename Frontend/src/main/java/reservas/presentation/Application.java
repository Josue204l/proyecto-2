package reservas.presentation;

import reservas.client.Service;
import reservas.logic.Funcionario;
import reservas.logic.Protocol;
import reservas.presentation.login.Controller;
import reservas.presentation.login.View;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Application {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                abrir();
            }
        });
    }

    public static void abrir() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) { }
        Service servicio = null;
        try {
            servicio = new Service(Protocol.SERVER, Protocol.PORT);
            View login = new View();
            Controller controller = new Controller(login, servicio);
            login.setVisible(true);
            Funcionario usuario = controller.getUsuario();
            if (usuario == null) {
                servicio.desconectar();
                return;
            }
            MainFrame frame = new MainFrame(usuario, servicio);
            SocketListener avisos = new SocketListener(frame, servicio.getSid(), Protocol.SERVER, Protocol.PORT);
            frame.setAvisos(avisos);
            avisos.start();
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    frame.cerrar();
                }
            });
            frame.setVisible(true);
        } catch (Exception ex) {
            if (servicio != null) {
                try {
                    servicio.desconectar();
                } catch (Exception ignored) { }
            }
            Ui.error(null, ex);
        }
    }
}
