package Interfaz.login;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JDialog {

    // Paneles principales según el .form
    private JPanel contentPane;
    private JPanel icono;
    private JPanel panel;

    // Campos de texto y etiquetas
    private JTextField txtUsuario;
    private JPasswordField txtClave;
    private JLabel ID;
    private JLabel Clave;

    // Botones según los bindings del .form
    private JButton ingresarButton;
    private JButton btnCancelar;
    private JButton cambiarButton;

    private ControllerLogin controller;

    public LoginView(Frame parent) {
        super(parent, "Inicio de Sesión", true);

        if (contentPane == null) {
            contentPane = new JPanel();
        }

        setContentPane(contentPane);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        configurarEventosBasicos();
        pack();
        if (parent != null) {
            setLocationRelativeTo(parent);
        } else {
            setLocationRelativeTo(null);
        }
    }

    private void configurarEventosBasicos() {
        if (btnCancelar != null) {
            btnCancelar.addActionListener(e -> {
                if (controller != null) {
                    controller.cancel();
                } else {
                    dispose();
                }
            });
        }
    }

    public void setController(ControllerLogin controller) {
        this.controller = controller;

        if (ingresarButton != null) {
            ingresarButton.addActionListener(e -> controller.login());
        }

        if (cambiarButton != null) {
            cambiarButton.addActionListener(e -> controller.cambiarClave());
        }
    }

    // --- Getters de Componentes ---
    public JTextField getTxtUsuario() { return txtUsuario; }
    public JPasswordField getTxtClave() { return txtClave; }
    public JButton getIngresarButton() { return ingresarButton; }
    public JButton getBtnCancelar() { return btnCancelar; }
    public JButton getCambiarButton() { return cambiarButton; }

    @Override
    public JPanel getContentPane() { return contentPane; }
}