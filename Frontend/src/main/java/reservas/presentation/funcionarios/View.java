package reservas.presentation.funcionarios;

import reservas.logic.Funcionario;
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
    private final JTextField buscarId = new JTextField(10);
    private final JTextField buscarNombre = new JTextField(16);
    private final JTextField id = new JTextField(10);
    private final JTextField nombre = new JTextField(16);
    private final JTextField telefono = new JTextField(12);
    private final JTable tabla = new JTable();
    private Controller controller;

    public View() {
        super(new BorderLayout(8, 8));
        JButton buscar = new JButton("Buscar");
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
        norte.add(new JLabel("Id"));
        norte.add(buscarId);
        norte.add(new JLabel("Nombre"));
        norte.add(buscarNombre);
        norte.add(buscar);
        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sur.add(new JLabel("Id"));
        sur.add(id);
        sur.add(new JLabel("Nombre"));
        sur.add(nombre);
        sur.add(new JLabel("Teléfono"));
        sur.add(telefono);
        sur.add(guardar);
        sur.add(eliminar);
        sur.add(limpiar);
        sur.add(pdf);
        add(sur, BorderLayout.SOUTH);

        buscar.addActionListener(e -> { if (controller != null) controller.buscar(); });
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
    public String getBuscarId() { return buscarId.getText(); }
    public String getBuscarNombre() { return buscarNombre.getText(); }
    public String getId() { return id.getText().trim(); }
    public String getNombre() { return nombre.getText().trim(); }
    public String getTelefono() { return telefono.getText().trim(); }

    public void mostrar(Funcionario funcionario) {
        id.setText(funcionario.getId());
        nombre.setText(funcionario.getNombre());
        telefono.setText(funcionario.getTelefono() == null ? "" : funcionario.getTelefono());
    }

    public void limpiar() {
        id.setText("");
        nombre.setText("");
        telefono.setText("");
        tabla.clearSelection();
    }
}
