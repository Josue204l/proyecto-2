package Interfaz.login;

import Interfaz.cambiarclave.ControllerCambiarClave;
import Interfaz.cambiarclave.cambiarclaveView;
import frontend.red.ServiceProxy;
import logic.Sesion;
import logic.Usuario;
import logic.Funcionario;

import javax.swing.*;
import java.awt.*;

public class ControllerLogin {

    private final ModelLogin model;
    private final LoginView view;
    private final ServiceProxy service;

    public ControllerLogin(ModelLogin model, LoginView view, ServiceProxy service) {
        this.model = model;
        this.view = view;
        this.service = service;
        this.view.setController(this);
    }

    public void login() {
        String id = view.getTxtUsuario() != null ? view.getTxtUsuario().getText().trim() : "";
        String clave = view.getTxtClave() != null ? new String(view.getTxtClave().getPassword()).trim() : "";

        if (id.isEmpty() || clave.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Debe ingresar usuario y contraseña.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Usuario usuario = service.login(id, clave);

            if (usuario == null) {
                JOptionPane.showMessageDialog(view, "Usuario o contraseña incorrectos.", "Error de autenticación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Sesion.setUsuario(usuario);
            model.setCurrentUser(usuario);
            view.dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Error de autenticación", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cambiarClave() {
        String id = view.getTxtUsuario() != null ? view.getTxtUsuario().getText().trim() : "";
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Ingrese el ID del usuario cuya clave desea cambiar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // La clave actual se valida en el Backend; no se consulta el catálogo de funcionarios aquí.
        Usuario usuario = new Funcionario(id, "", "FUNCIONARIO", id);

        Window owner = SwingUtilities.getWindowAncestor(view);
        cambiarclaveView dialogo = (owner instanceof Frame) ? new cambiarclaveView((Frame) owner) : new cambiarclaveView(null);
        new ControllerCambiarClave(dialogo, usuario, service);
        dialogo.setVisible(true);
    }

    public void cancel() {
        Sesion.logout();
        view.dispose();
    }

    public void clear() {
        if (view.getTxtUsuario() != null) view.getTxtUsuario().setText("");
        if (view.getTxtClave() != null) view.getTxtClave().setText("");
    }
}
