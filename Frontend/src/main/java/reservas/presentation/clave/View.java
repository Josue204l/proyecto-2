package reservas.presentation.clave;

import reservas.presentation.Ui;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class View extends JDialog {
    private final JPasswordField actual = new JPasswordField(16);
    private final JPasswordField nueva = new JPasswordField(16);
    private final JPasswordField confirmar = new JPasswordField(16);
    private Controller controller;

    public View(Frame padre) {
        super(padre, "Cambiar clave", true);
        JButton guardar = new JButton("Guardar");
        JButton cancelar = new JButton("Cancelar");
        Ui.icono(guardar, "save.png");
        Ui.icono(cancelar, "cancel.png");
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.anchor = GridBagConstraints.WEST;
        fila(panel, g, 0, "Clave actual", actual);
        fila(panel, g, 1, "Clave nueva", nueva);
        fila(panel, g, 2, "Confirmar", confirmar);
        g.gridx = 0;
        g.gridy = 3;
        panel.add(guardar, g);
        g.gridx = 1;
        panel.add(cancelar, g);
        setContentPane(panel);
        guardar.addActionListener(e -> { if (controller != null) controller.guardar(); });
        cancelar.addActionListener(e -> dispose());
        pack();
        setLocationRelativeTo(padre);
        setResizable(false);
    }

    private static void fila(JPanel panel, GridBagConstraints g, int y, String etiqueta, JPasswordField campo) {
        g.gridx = 0;
        g.gridy = y;
        panel.add(new JLabel(etiqueta), g);
        g.gridx = 1;
        panel.add(campo, g);
    }

    public void setController(Controller controller) { this.controller = controller; }
    public String getActual() { return new String(actual.getPassword()); }
    public String getNueva() { return new String(nueva.getPassword()); }
    public String getConfirmar() { return new String(confirmar.getPassword()); }
}
