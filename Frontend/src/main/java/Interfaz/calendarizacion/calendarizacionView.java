package Interfaz.calendarizacion;

import com.github.lgooddatepicker.components.DatePicker;
import logic.Categoria;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class calendarizacionView implements PropertyChangeListener {

    private JPanel panel;
    private JTable table1;
    private JTextField textField1;
    private JButton button1;
    private JComboBox<Categoria> comboBox1;
    private JButton btnCargar;
    private JButton btnImprimir;

    private DatePicker datePicker;
    private ControllerCalendarizacion controller;
    private ModelCalendarizacion model;

    public calendarizacionView() {
        this.datePicker = new DatePicker();
        configurarListeners();
    }

    private void configurarListeners() {
        if (btnCargar != null) {
            btnCargar.addActionListener(e -> {
                if (controller != null) controller.filtrar();
            });
        }
        if (btnImprimir != null) {
            btnImprimir.addActionListener(e -> {
                if (controller != null) controller.print();
            });
        }
        if (button1 != null) {
            button1.addActionListener(e -> seleccionarFechaDialogo());
        }
    }

    private void seleccionarFechaDialogo() {
        SpinnerDateModel modelDate = new SpinnerDateModel();
        JSpinner spinner = new JSpinner(modelDate);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy");
        spinner.setEditor(editor);

        int option = JOptionPane.showConfirmDialog(panel, spinner, "Seleccionar Fecha", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
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
        }
    }

    public void setController(ControllerCalendarizacion controller) {
        this.controller = controller;
    }

    public void setModel(ModelCalendarizacion model) {
        this.model = model;
        if (this.model != null) {
            this.model.addPropertyChangeListener(this);
        }
    }

    public void cargarCategorias(List<Categoria> categorias) {
        DefaultComboBoxModel<Categoria> comboModel = new DefaultComboBoxModel<>();
        if (categorias != null) {
            for (Categoria cat : categorias) {
                comboModel.addElement(cat);
            }
        }
        if (comboBox1 != null) {
            comboBox1.setModel(comboModel);
        }
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

    // --- Getters de componentes ---
    public JPanel getMainPanel() { return panel; }
    public JTextField getTxtFecha() { return textField1; }
    public JComboBox<Categoria> getCmbCategoria() { return comboBox1; }
    public JButton getBtnCargar() { return btnCargar; }
    public JButton getBtnImprimir() { return btnImprimir; }
    public JTable getTblCalendarizacion() { return table1; }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (ModelCalendarizacion.CATEGORIAS.equals(evt.getPropertyName())) {
            if (evt.getNewValue() instanceof List) {
                @SuppressWarnings("unchecked")
                List<Categoria> lista = (List<Categoria>) evt.getNewValue();
                cargarCategorias(lista);
            }
        }

        if (panel != null) {
            panel.revalidate();
            panel.repaint();
        }
    }
}