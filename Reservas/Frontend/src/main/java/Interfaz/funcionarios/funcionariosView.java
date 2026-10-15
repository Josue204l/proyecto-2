package Interfaz.funcionarios;

import javax.swing.*;

public class funcionariosView {

    // --- Vinculación con el Form de IntelliJ UI Designer ---
    private JPanel panel1;         // Panel principal

    // Sección Búsqueda
    private JTextField textField1; // Buscar por ID
    private JTextField textField2; // Buscar por Nombre
    private JButton button1;       // Botón Buscar
    private JButton button2;       // Botón Imprimir PDF

    // Sección Formulario Funcionario
    private JTextField textField3; // ID Funcionario
    private JTextField textField4; // Nombre Funcionario
    private JTextField textField5; // Teléfono Funcionario

    // Botones de Acción
    private JButton guardarButton;
    private JButton borrarButton;
    private JButton limpiarButton;

    // Sección Listado
    private JTable table1;         // Tabla de registros

    private ControllerFuncionario controller;

    public funcionariosView() {
        if (button1 != null) button1.setText("Buscar");
        if (button2 != null) button2.setText("Imprimir");
    }

    public void setController(ControllerFuncionario controller) {
        this.controller = controller;
        configurarListeners();
    }

    private void configurarListeners() {
        if (guardarButton != null) guardarButton.addActionListener(e -> controller.guardar());
        if (borrarButton != null) borrarButton.addActionListener(e -> controller.eliminar());
        if (limpiarButton != null) limpiarButton.addActionListener(e -> controller.limpiar());
        if (button1 != null) button1.addActionListener(e -> controller.buscar());
        if (button2 != null) button2.addActionListener(e -> controller.print());

        if (table1 != null) {
            table1.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && table1.getSelectedRow() >= 0) {
                    controller.cargarSeleccionado();
                }
            });
        }
    }

    // --- Contenedor Principal ---
    public JPanel getMainPanel() {
        return panel1 != null ? panel1 : new JPanel();
    }

    // --- Getters Adaptados para ControllerFuncionario ---
    public JTable getTable() { return table1; }
    public JTable getTablaFuncionarios() { return table1; }

    public JButton getBuscarButton() { return button1; }
    public JButton getImprimirButton() { return button2; }
    public JButton getGuardarButton() { return guardarButton; }
    public JButton getBorrarButton() { return borrarButton; }
    public JButton getLimpiarButton() { return limpiarButton; }

    // Campos de Búsqueda
    public JTextField getTxtBuscarId() { return textField1; }
    public JTextField getTxtBuscarNombre() { return textField2; }

    // Campos de Formulario
    public JTextField getTxtId() { return textField3; }
    public JTextField getTxtNombre() { return textField4; }
    public JTextField getTxtTelefono() { return textField5; }
}