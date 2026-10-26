package Interfaz.actividades;


import logic.Reserva;
import utils.Horarios;

import javax.swing.table.AbstractTableModel;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class TableModelActividades extends AbstractTableModel {

    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM");
    private static final Locale ES = Locale.forLanguageTag("es-CR");

    private List<LocalTime> horas = Horarios.horasDelDia();
    private LocalDate lunes;
    private String[][] celdas = new String[0][7];

    public void setDatos(LocalDate fechaReferencia, List<Reserva> reservas) {
        this.horas = Horarios.horasDelDia();
        this.lunes = fechaReferencia.with(DayOfWeek.MONDAY);
        this.celdas = new String[horas.size()][7];

        for (Reserva reserva : reservas) {
            if (reserva == null || !reserva.isActiva() || reserva.getFecha() == null) continue;
            int dia = (int) java.time.temporal.ChronoUnit.DAYS.between(lunes, reserva.getFecha());
            if (dia < 0 || dia > 6) continue;
            for (int f = 0; f < horas.size(); f++) {
                if (reserva.cubreHora(horas.get(f))) {
                    String actual = celdas[f][dia];
                    String etiqueta = reserva.etiquetaCelda();
                    celdas[f][dia] = (actual == null || actual.isBlank()) ? etiqueta : actual + " | " + etiqueta;
                }
            }
        }
        fireTableStructureChanged();
    }

    public LocalDate getLunes() {
        return lunes;
    }

    @Override
    public int getRowCount() {
        return horas.size();
    }

    @Override
    public int getColumnCount() {
        return 8;
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) return "Hora";
        if (lunes == null) return "";
        LocalDate dia = lunes.plusDays(column - 1);
        String nombre = dia.getDayOfWeek().getDisplayName(TextStyle.SHORT, ES);
        return nombre + " " + dia.format(FMT_FECHA);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return horas.get(rowIndex).format(FMT_HORA);
        }
        String valor = celdas[rowIndex][columnIndex - 1];
        return valor != null ? valor : "";
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
