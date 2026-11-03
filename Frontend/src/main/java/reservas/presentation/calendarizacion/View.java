package reservas.presentation.calendarizacion;

import reservas.logic.Categoria;
import reservas.presentation.Ui;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

public class View extends JPanel {
    private final JSpinner fecha = Ui.fecha();
    private final JComboBox<Categoria> categoria = new JComboBox<>();
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
        norte.add(new JLabel("Fecha"));
        norte.add(fecha);
        norte.add(new JLabel("Categoría"));
        norte.add(categoria);
        norte.add(mostrar);
        norte.add(pdf);
        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        mostrar.addActionListener(e -> { if (controller != null) controller.mostrar(); });
        pdf.addActionListener(e -> { if (controller != null) controller.pdf(); });
    }

    public void setController(Controller controller) { this.controller = controller; }
    public JTable getTabla() { return tabla; }
    public JSpinner getFecha() { return fecha; }
    public Categoria getCategoria() { return (Categoria) categoria.getSelectedItem(); }

    public void setCategorias(List<Categoria> categorias) {
        categoria.removeAllItems();
        for (Categoria item : categorias) categoria.addItem(item);
    }
}
