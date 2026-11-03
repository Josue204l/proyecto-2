package reservas.presentation;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import java.awt.Component;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public final class Ui {
    private Ui() { }

    public static ImageIcon icono(String nombre) {
        URL url = Ui.class.getResource("/iconos/" + nombre);
        return url == null ? null : new ImageIcon(url);
    }

    public static void icono(AbstractButton boton, String nombre) {
        ImageIcon icono = icono(nombre);
        if (icono != null && boton != null) boton.setIcon(icono);
    }

    public static void error(Component padre, Exception ex) {
        JOptionPane.showMessageDialog(padre, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void aviso(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Aviso", JOptionPane.INFORMATION_MESSAGE);
    }

    public static JSpinner fecha() {
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        spinner.setEditor(new JSpinner.DateEditor(spinner, "yyyy-MM-dd"));
        return spinner;
    }

    public static LocalDate leerFecha(JSpinner spinner) {
        Date valor = (Date) spinner.getValue();
        return valor.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static void ponerFecha(JSpinner spinner, LocalDate fecha) {
        spinner.setValue(Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    public static JComboBox<LocalTime> horas() {
        JComboBox<LocalTime> combo = new JComboBox<>();
        for (LocalTime hora : Horarios.horasDelDia()) combo.addItem(hora);
        combo.setRenderer((lista, valor, indice, seleccionado, foco) -> {
            JLabel etiqueta = new JLabel(valor == null ? "" : valor.toString());
            if (seleccionado) {
                etiqueta.setBackground(lista.getSelectionBackground());
                etiqueta.setForeground(lista.getSelectionForeground());
                etiqueta.setOpaque(true);
            }
            return etiqueta;
        });
        return combo;
    }
}
