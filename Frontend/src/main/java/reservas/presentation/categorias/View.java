package reservas.presentation.categorias;

import reservas.logic.Categoria;
import reservas.presentation.Ui;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class View extends JPanel {
    private final JTextField buscar = new JTextField(18);
    private final JTextField id = new JTextField(10);
    private final JTextField descripcion = new JTextField(24);
    private final JTable tabla = new JTable();
    private Controller controller;

    public View() {
        super(new BorderLayout(8, 8));
        id.setEditable(false);
        JButton botonBuscar = new JButton("Buscar");
        JButton guardar = new JButton("Guardar");
        JButton eliminar = new JButton("Eliminar");
        JButton limpiar = new JButton("Limpiar");
        JButton pdf = new JButton("PDF");
        Ui.icono(botonBuscar, "search.png");
        Ui.icono(guardar, "save.png");
        Ui.icono(eliminar, "delete.png");
        Ui.icono(limpiar, "clear.png");
        Ui.icono(pdf, "pdf.png");

        JPanel norte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        norte.add(new JLabel("Descripción"));
        norte.add(buscar);
        norte.add(botonBuscar);
        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sur.add(new JLabel("Id"));
        sur.add(id);
        sur.add(new JLabel("Descripción"));
        sur.add(descripcion);
        sur.add(guardar);
        sur.add(eliminar);
        sur.add(limpiar);
        sur.add(pdf);
        add(sur, BorderLayout.SOUTH);

        botonBuscar.addActionListener(e -> { if (controller != null) controller.buscar(); });
        guardar.addActionListener(e -> { if (controller != null) controller.guardar(); });
        eliminar.addActionListener(e -> { if (controller != null) controller.eliminar(); });
        limpiar.addActionListener(e -> limpiar());
        pdf.addActionListener(e -> { if (controller != null) controller.pdf(); });
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && controller != null) controller.seleccionar(tabla.getSelectedRow());
        });
    }

    public void setController(Controller controller) { this.controller = controller; }
    public JTable getTabla() { return tabla; }
    public String getBuscar() { return buscar.getText(); }
    public String getId() { return id.getText().trim(); }
    public String getDescripcion() { return descripcion.getText().trim(); }

    public void mostrar(Categoria categoria) {
        id.setText(categoria.getId());
        descripcion.setText(categoria.getDescripcion());
    }

    public void limpiar() {
        id.setText("");
        descripcion.setText("");
        tabla.clearSelection();
    }
}
