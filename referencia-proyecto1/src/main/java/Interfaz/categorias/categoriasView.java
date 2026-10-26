package Interfaz.categorias;

import utils.UiHelper;

import javax.swing.*;

public class categoriasView {
    private JPanel panel1;
    private JTable table1;
    private JTextField textField1; // Búsqueda Descripción
    private JButton buscarButton;
    private JButton imprimirButton;
    private JTextField textField2; // Formulario Descripción
    private JTextField textField3; // Formulario ID
    private JButton guardarButton;
    private JButton borrarButton;
    private JButton limpiarButton;

    private ControllerCategoria controller;

    public categoriasView() {
        if (getTxtId() != null) {
            getTxtId().setEditable(false);
        }
    }

    public JPanel getMainPanel() {
        return panel1 != null ? panel1 : new JPanel();
    }

    public JTable getTable() {
        return table1;
    }

    public JTextField getTxtBuscar() {
        return textField1;
    }

    public JTextField getTxtId() {
        return textField3;
    }

    public JTextField getTxtDescripcion() {
        return textField2;
    }

    public JButton getBuscarButton() {
        return buscarButton;
    }

    public JButton getImprimirButton() {
        return imprimirButton;
    }

    public JButton getGuardarButton() {
        return guardarButton;
    }

    public JButton getBorrarButton() {
        return borrarButton;
    }

    public JButton getLimpiarButton() {
        return limpiarButton;
    }

    public void setController(ControllerCategoria controller) {
        this.controller = controller;
        addListeners();
    }

    public void addListeners() {
        if (controller == null) return;
        UiHelper.bind(guardarButton, e -> controller.guardar());
        UiHelper.bind(borrarButton, e -> controller.eliminar());
        UiHelper.bind(limpiarButton, e -> controller.limpiar());
        UiHelper.bind(buscarButton, e -> controller.buscar());
        UiHelper.bind(imprimirButton, e -> controller.print());
        if (table1 != null) {
            table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table1.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) controller.cargarSeleccionado();
            });
        }
    }
}