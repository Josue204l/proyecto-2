package reservas.presentation.reservas;

import reservas.logic.Categoria;
import reservas.presentation.ExtractorFrase;
import reservas.presentation.Ui;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class View extends JPanel {
    private final JTextArea frase = new JTextArea(3, 30);
    private final JTextField titulo = new JTextField(18);
    private final JSpinner fecha = Ui.fecha();
    private final JComboBox<LocalTime> inicio = Ui.horas();
    private final JComboBox<LocalTime> fin = Ui.horas();
    private final DefaultListModel<Categoria> modeloCategorias = new DefaultListModel<>();
    private final JList<Categoria> categorias = new JList<>(modeloCategorias);
    private final JTable tabla = new JTable();
    private Controller controller;

    public View() {
        super(new BorderLayout(8, 8));
        categorias.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        frase.setLineWrap(true);
        frase.setWrapStyleWord(true);
        JButton extraer = new JButton("Extraer");
        JButton guardar = new JButton("Registrar");
        JButton cancelar = new JButton("Cancelar reserva");
        JButton limpiar = new JButton("Limpiar");
        JButton pdf = new JButton("PDF");
        Ui.icono(extraer, "AI.png");
        Ui.icono(guardar, "save.png");
        Ui.icono(cancelar, "cancel.png");
        Ui.icono(limpiar, "clear.png");
        Ui.icono(pdf, "pdf.png");
        if (fin.getItemCount() > 1) fin.setSelectedIndex(1);

        JPanel norte = new JPanel(new BorderLayout(6, 6));
        norte.add(new JLabel("Describa la actividad, la fecha, el horario y los recursos:"), BorderLayout.NORTH);
        norte.add(new JScrollPane(frase), BorderLayout.CENTER);
        norte.add(extraer, BorderLayout.EAST);
        add(norte, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formulario.add(new JLabel("Actividad"));
        formulario.add(titulo);
        formulario.add(new JLabel("Fecha"));
        formulario.add(fecha);
        formulario.add(new JLabel("Inicio"));
        formulario.add(inicio);
        formulario.add(new JLabel("Fin"));
        formulario.add(fin);
        JScrollPane lista = new JScrollPane(categorias);
        lista.setPreferredSize(new Dimension(220, 90));
        formulario.add(new JLabel("Categorías"));
        formulario.add(lista);
        formulario.add(guardar);
        formulario.add(cancelar);
        formulario.add(limpiar);
        formulario.add(pdf);

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(formulario, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        extraer.addActionListener(e -> { if (controller != null) controller.extraer(); });
        guardar.addActionListener(e -> { if (controller != null) controller.guardar(); });
        cancelar.addActionListener(e -> { if (controller != null) controller.cancelar(); });
        limpiar.addActionListener(e -> limpiar());
        pdf.addActionListener(e -> { if (controller != null) controller.pdf(); });
    }

    public void setController(Controller controller) { this.controller = controller; }
    public JTable getTabla() { return tabla; }
    public String getFrase() { return frase.getText(); }
    public String getTitulo() { return titulo.getText().trim(); }
    public JSpinner getFecha() { return fecha; }
    public LocalTime getInicio() { return (LocalTime) inicio.getSelectedItem(); }
    public LocalTime getFin() { return (LocalTime) fin.getSelectedItem(); }

    public List<Categoria> getCategorias() {
        List<Categoria> lista = new ArrayList<>();
        for (int i = 0; i < modeloCategorias.size(); i++) lista.add(modeloCategorias.get(i));
        return lista;
    }

    public List<Categoria> getCategoriasSeleccionadas() {
        return categorias.getSelectedValuesList();
    }

    public void setCategorias(List<Categoria> lista) {
        List<String> seleccionadas = new ArrayList<>();
        for (Categoria categoria : categorias.getSelectedValuesList()) seleccionadas.add(categoria.getId());
        modeloCategorias.clear();
        for (Categoria categoria : lista) modeloCategorias.addElement(categoria);
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < modeloCategorias.size(); i++) {
            if (seleccionadas.contains(modeloCategorias.get(i).getId())) indices.add(i);
        }
        int[] arreglo = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) arreglo[i] = indices.get(i);
        categorias.setSelectedIndices(arreglo);
    }

    public void aplicarExtraccion(ExtractorFrase.Datos datos) {
        if (datos.titulo != null) titulo.setText(datos.titulo);
        if (datos.fecha != null) Ui.ponerFecha(fecha, datos.fecha);
        seleccionarHora(inicio, datos.inicio);
        seleccionarHora(fin, datos.fin);
        categorias.clearSelection();
        List<Integer> indices = new ArrayList<>();
        for (Categoria pedida : datos.categorias) {
            for (int i = 0; i < modeloCategorias.size(); i++) {
                if (pedida.getId().equals(modeloCategorias.get(i).getId())) indices.add(i);
            }
        }
        int[] arreglo = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) arreglo[i] = indices.get(i);
        categorias.setSelectedIndices(arreglo);
    }

    private static void seleccionarHora(JComboBox<LocalTime> combo, LocalTime hora) {
        if (hora == null) return;
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (hora.equals(combo.getItemAt(i))) {
                combo.setSelectedIndex(i);
                return;
            }
        }
        combo.addItem(hora);
        combo.setSelectedItem(hora);
    }

    public void limpiar() {
        frase.setText("");
        titulo.setText("");
        categorias.clearSelection();
        tabla.clearSelection();
    }
}
