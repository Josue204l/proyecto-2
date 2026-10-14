package Interfaz.cambiarclave;

import javax.swing.*;
import java.awt.*;

public class cambiarclaveView extends JDialog {
    private JPanel mainPanel;
    private JPasswordField txtClaveActual;
    private JPasswordField txtClaveNueva;
    private JPasswordField txtConfirmacion;
    private JButton btnGuardar;
    private JButton btnCancelar;

    public cambiarclaveView(Frame parent) {
        super(parent, "Cambiar Contraseña", true); // modal
        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(parent);

        if (btnCancelar != null) {
            btnCancelar.addActionListener(e -> dispose());
        }
    }

    public JPanel getMainPanel() { return mainPanel; }
    public JPasswordField getClaveActual() { return txtClaveActual; }
    public JPasswordField getClaveNueva() { return txtClaveNueva; }
    public JPasswordField getClaveConfirmar() { return txtConfirmacion; }
    public JButton getAceptarButton() { return btnGuardar; }
    public JButton getCancelarButton() { return btnCancelar; }
}