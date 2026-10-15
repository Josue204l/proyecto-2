import Interfaz.login.ControllerLogin;
import Interfaz.login.LoginView;
import Interfaz.login.ModelLogin;
import Interfaz.main.MainFrame;
import frontend.red.ServiceProxy;
import frontend.red.ServicioActual;
import logic.Sesion;

import javax.swing.*;

public final class Application {
    private Application() { }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                ServiceProxy servicio = new ServiceProxy(ServiceProxy.hostConfigurado(), ServiceProxy.puertoConfigurado());
                ServicioActual.establecer(servicio);
                Runtime.getRuntime().addShutdownHook(new Thread(servicio::cerrar, "cerrar-cliente-socket"));
                if (!Sesion.isLoggedIn()) iniciarLogin(servicio);
                if (Sesion.isLoggedIn()) new MainFrame(Sesion.getUsuario(), servicio);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "No se pudo conectar con el Backend: " + e.getMessage(),
                        "Error de conexión", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static void iniciarLogin(ServiceProxy servicio) {
        LoginView loginView = new LoginView(null);
        new ControllerLogin(new ModelLogin(), loginView, servicio);
        loginView.setVisible(true);
    }
}
