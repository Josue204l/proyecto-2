package Interfaz.recursos;

import logic.Categoria;
import javax.swing.*;

public class recursosView {
    private JPanel recursos;
    private JComboBox<Categoria> category;
    private JTextField descrip;
    private JButton buscarButton;
    private JButton imprimirButton;
    private JPanel recurso;
    private JLabel id;
    private JLabel Categoria;
    private JButton Guardar;
    private JButton limpiarButton;
    private JComboBox<Categoria> categorias;
    private JTextField idtext;
    private JTextField descripcion;
    private JButton borrarButton;
    private JTable tablaRecursos;

    private ControllerRecurso controller;

    public recursosView() {}

    public void setController(ControllerRecurso controller) {
        this.controller = controller;
        configurarListeners();
    }

    private void configurarListeners() {
        if (Guardar != null) Guardar.addActionListener(e -> controller.guardar());
        if (borrarButton != null) borrarButton.addActionListener(e -> controller.eliminar());
        if (limpiarButton != null) limpiarButton.addActionListener(e -> controller.limpiar());
        if (buscarButton != null) buscarButton.addActionListener(e -> controller.buscar());
        if (imprimirButton != null) imprimirButton.addActionListener(e -> controller.print());

        if (tablaRecursos != null) {
            tablaRecursos.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && tablaRecursos.getSelectedRow() >= 0) {
                    controller.cargarSeleccionado();
                }
            });
        }
    }

    // --- Adaptación de Getters para ControllerRecurso ---
    public JPanel getPanel() { return recursos != null ? recursos : new JPanel(); }
    public JPanel getMainPanel() { return getPanel(); }

    public JComboBox<Categoria> getCmbFiltroCategoria() { return category; }
    public JComboBox<Categoria> getCmbCategoria() { return categorias; }
    public JComboBox<Categoria> getCmbCategoriaForm() { return categorias; }

    public JTextField getTxtBuscar() { return descrip; }
    public JTextField getTxtBuscarDescripcion() { return descrip; }
    public JTextField getTxtId() { return idtext; }
    public JTextField getTxtDescripcion() { return descripcion; }

    public JButton getBuscarButton() { return buscarButton; }
    public JButton getImprimirButton() { return imprimirButton; }
    public JButton getGuardarButton() { return Guardar; }
    public JButton getLimpiarButton() { return limpiarButton; }
    public JButton getBorrarButton() { return borrarButton; }

    public JTable getTable() {
        if (tablaRecursos == null) {
            tablaRecursos = new JTable();
        }
        return tablaRecursos;
    }

    public JTable getTablaRecursos() {
        return getTable();
    }
}