package reservas.presentation.recursos;

import reservas.logic.Categoria;
import reservas.logic.Recurso;
import reservas.presentation.Ui;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

public class View extends JPanel {
    private final JComboBox<Categoria> filtro = new JComboBox<>();
    private final JTextField id = new JTextField(10);
    private final JTextField descripcion = new JTextField(20);
    private final JComboBox<Categoria> categoria = new JComboBox<>();
    private final JTable tabla = new JTable();
    private Controller controller;

    public View() {
        super(new BorderLayout(8, 8));
        JButton buscar = new JButton("Filtrar");
        JButton guardar = new JButton("Guardar");
        JButton eliminar = new JButton("Eliminar");
        JButton limpiar = new JButton("Limpiar");
        JButton pdf = new JButton("PDF");
        Ui.icono(buscar, "search.png");
        Ui.icono(guardar, "save.png");
        Ui.icono(eliminar, "delete.png");
        Ui.icono(limpiar, "clear.png");
        Ui.icono(pdf, "pdf.png");

        JPanel norte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        norte.add(new JLabel("Categoría"));
        norte.add(filtro);
        norte.add(buscar);
        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sur.add(new JLabel("Número de activo"));
        sur.add(id);
        sur.add(new JLabel("Descripción"));
        sur.add(descripcion);
        sur.add(new JLabel("Categoría"));
        sur.add(categoria);
        sur.add(guardar);
        sur.add(eliminar);
        sur.add(limpiar);
        sur.add(pdf);
        add(sur, BorderLayout.SOUTH);

        buscar.addActionListener(e -> { if (controller != null) controller.filtrar(); });
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
    public String getId() { return id.getText().trim(); }
    public String getDescripcion() { return descripcion.getText().trim(); }
    public Categoria getCategoria() { return (Categoria) categoria.getSelectedItem(); }
    public Categoria getFiltro() { return (Categoria) filtro.getSelectedItem(); }

    public void setCategorias(List<Categoria> categorias) {
        Categoria seleccion = getCategoria();
        Categoria filtroActual = getFiltro();
        DefaultComboBoxModel<Categoria> modeloFiltro = new DefaultComboBoxModel<>();
        modeloFiltro.addElement(new Categoria("", "Todas"));
        DefaultComboBoxModel<Categoria> modelo = new DefaultComboBoxModel<>();
        for (Categoria item : categorias) {
            modeloFiltro.addElement(item);
            modelo.addElement(item);
        }
        filtro.setModel(modeloFiltro);
        categoria.setModel(modelo);
        seleccionar(filtro, filtroActual);
        seleccionar(categoria, seleccion);
    }

    private static void seleccionar(JComboBox<Categoria> combo, Categoria buscada) {
        if (buscada == null || buscada.getId() == null) return;
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (buscada.getId().equals(combo.getItemAt(i).getId())) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    public void mostrar(Recurso recurso) {
        id.setText(recurso.getId());
        descripcion.setText(recurso.getDescripcion());
        seleccionar(categoria, recurso.getCategoria());
    }

    public void limpiar() {
        id.setText("");
        descripcion.setText("");
        tabla.clearSelection();
    }
}
