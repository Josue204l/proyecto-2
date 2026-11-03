package reservas.presentation.actividades;

import reservas.presentation.Ui;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class View extends JPanel {
    private final JSpinner fecha = Ui.fecha();
    private final JLabel titulo = new JLabel("Semana");
    private final JTable tabla = new JTable();
    private Controller controller;

    public View() {
        super(new BorderLayout(8, 8));
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JButton mostrar = new JButton("Mostrar");
        JButton pdf = new JButton("PDF");
        Ui.icono(mostrar, "search.png");
        Ui.icono(pdf, "pdf.png");
        JPanel norte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        norte.add(new JLabel("Cualquier día de la semana"));
        norte.add(fecha);
        norte.add(mostrar);
        norte.add(pdf);
        norte.add(titulo);
        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        mostrar.addActionListener(e -> { if (controller != null) controller.mostrar(); });
        pdf.addActionListener(e -> { if (controller != null) controller.pdf(); });
    }

    public void setController(Controller controller) { this.controller = controller; }
    public JTable getTabla() { return tabla; }
    public JSpinner getFecha() { return fecha; }
    public void setTitulo(String texto) { titulo.setText(texto); }
}
