package Interfaz.actividades;

import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class actividadesView {
    private JPanel panel1;
    private JTextField textField1; // Fecha de referencia
    private JButton button1;       // Botón "..."
    private JButton btnBuscar;     // Botón Cargar
    private JButton btnImprimir;   // Botón Imprimir PDF
    private JTable table1;
    private JLabel lblSemana;

    private DatePicker datePicker;
    private ControllerActividades controller;

    public actividadesView() {
        if (datePicker == null) {
            datePicker = new DatePicker();
        }
        if (lblSemana == null) {
            lblSemana = new JLabel("Semana");
        }
    }

    public void setController(ControllerActividades controller) {
        this.controller = controller;
        configurarListeners();
    }

    private void configurarListeners() {
        if (btnBuscar != null) {
            btnBuscar.addActionListener(e -> controller.cargar());
        }
        if (btnImprimir != null) {
            btnImprimir.addActionListener(e -> controller.print());
        }
        if (button1 != null) {
            button1.addActionListener(e -> abrirSelectorFecha());
        }
        if (datePicker != null) {
            datePicker.addDateChangeListener(e -> controller.cargar());
        }
    }

    private void abrirSelectorFecha() {
        SpinnerDateModel modelDate = new SpinnerDateModel();
        JSpinner spinner = new JSpinner(modelDate);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy");
        spinner.setEditor(editor);

        int option = JOptionPane.showConfirmDialog(panel1, spinner, "Seleccionar Fecha", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            Date selectedDate = (Date) spinner.getValue();
            LocalDate localDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String formatted = localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            if (textField1 != null) {
                textField1.setText(formatted);
            }
            if (datePicker != null) {
                datePicker.setDate(localDate);
            }
            if (controller != null) {
                controller.cargar();
            }
        }
    }

    // --- Getters de componentes ---
    public JPanel getMainPanel() {
        return panel1 != null ? panel1 : new JPanel();
    }

    public JTable getTable() {
        return table1;
    }

    public JTextField getTxtBuscar() {
        return textField1;
    }

    public JButton getBtnBuscar() {
        return btnBuscar;
    }

    public JButton getBtnImprimir() {
        return btnImprimir;
    }

    public JLabel getLblSemana() {
        return lblSemana;
    }

    public DatePicker getDatePicker() {
        if (datePicker == null) {
            datePicker = new DatePicker();
        }
        if (textField1 != null && !textField1.getText().isBlank()) {
            String texto = textField1.getText().trim();
            try {
                LocalDate parsed = LocalDate.parse(texto, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                datePicker.setDate(parsed);
            } catch (Exception ignored1) {
                try {
                    LocalDate parsedIso = LocalDate.parse(texto);
                    datePicker.setDate(parsedIso);
                } catch (Exception ignored2) {}
            }
        }
        return datePicker;
    }
}