package reservas.presentation.actividades;

import reservas.logic.Reserva;
import reservas.presentation.Horarios;

import javax.swing.table.AbstractTableModel;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

public class TableModel extends AbstractTableModel {
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM");
    private static final Locale ES = Locale.forLanguageTag("es-CR");
    private List<LocalTime> horas = Horarios.horasDelDia();
    private LocalDate lunes;
    private String[][] celdas = new String[0][7];

    public void setDatos(LocalDate fechaReferencia, List<Reserva> reservas) {
        this.horas = Horarios.horasDelDia();
        this.lunes = fechaReferencia.with(DayOfWeek.MONDAY);
        this.celdas = new String[horas.size()][7];
        if (reservas != null) {
            for (Reserva reserva : reservas) {
                if (reserva == null || !reserva.isActiva() || reserva.getFecha() == null) continue;
                long dia = ChronoUnit.DAYS.between(lunes, reserva.getFecha());
                if (dia < 0 || dia > 6) continue;
                for (int fila = 0; fila < horas.size(); fila++) {
                    if (!reserva.cubreHora(horas.get(fila))) continue;
                    String actual = celdas[fila][(int) dia];
                    String etiqueta = reserva.etiquetaCelda();
                    celdas[fila][(int) dia] = actual == null || actual.isBlank() ? etiqueta : actual + " | " + etiqueta;
                }
            }
        }
        fireTableStructureChanged();
    }

    public String etiquetaSemana() {
        if (lunes == null) return "Semana";
        return "Semana " + lunes.format(FECHA) + " - " + lunes.plusDays(6).format(FECHA);
    }

    @Override public int getRowCount() { return horas.size(); }
    @Override public int getColumnCount() { return 8; }

    @Override
    public String getColumnName(int column) {
        if (column == 0) return "Hora";
        if (lunes == null) return "";
        LocalDate dia = lunes.plusDays(column - 1);
        return dia.getDayOfWeek().getDisplayName(TextStyle.SHORT, ES) + " " + dia.format(FECHA);
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (column == 0) return horas.get(row).format(HORA);
        String valor = celdas[row][column - 1];
        return valor == null ? "" : valor;
    }

    @Override public boolean isCellEditable(int row, int column) { return false; }
}
