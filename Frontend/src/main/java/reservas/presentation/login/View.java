package reservas.presentation.login;

import reservas.client.Service;
import reservas.logic.Funcionario;
import reservas.presentation.Ui;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class View extends JDialog {
    private final JTextField id = new JTextField(16);
    private final JPasswordField clave = new JPasswordField(16);
    private Controller controller;

    public View() {
        super((Frame) null, "Inicio de sesión", true);
        JButton ingresar = new JButton("Ingresar");
        JButton cancelar = new JButton("Cancelar");
        Ui.icono(ingresar, "login.png");
        Ui.icono(cancelar, "cancel.png");
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0;
        g.gridy = 0;
        panel.add(new JLabel("Id"), g);
        g.gridx = 1;
        panel.add(id, g);
        g.gridx = 0;
        g.gridy = 1;
        panel.add(new JLabel("Clave"), g);
        g.gridx = 1;
        panel.add(clave, g);
        g.gridx = 0;
        g.gridy = 2;
        panel.add(ingresar, g);
        g.gridx = 1;
        panel.add(cancelar, g);
        setContentPane(panel);
        getRootPane().setDefaultButton(ingresar);
        ingresar.addActionListener(e -> { if (controller != null) controller.login(); });
        cancelar.addActionListener(e -> dispose());
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    public void setController(Controller controller) { this.controller = controller; }
    public String getId() { return id.getText().trim(); }
    public String getClave() { return new String(clave.getPassword()); }
}
