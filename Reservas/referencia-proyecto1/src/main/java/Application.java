import Interfaz.login.ControllerLogin;
import Interfaz.login.LoginView;
import Interfaz.login.ModelLogin;
import Interfaz.main.MainFrame;
import logic.Sesion;

import javax.swing.*;

public class Application {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception ignored) {}

            if (!Sesion.isLoggedIn()) {
                doLogin();
            }

            if (Sesion.isLoggedIn()) {
                doRun();
            }
        });
    }

    private static void doLogin() {
        LoginView loginView = new LoginView(null);
        ModelLogin loginModel = new ModelLogin();
        new ControllerLogin(loginModel, loginView);

        // Al ser modal, la aplicación se detiene aquí hasta que se haga dispose()
        loginView.setVisible(true);
    }

    private static void doRun() {
        new MainFrame(Sesion.getUsuario());
    }
}