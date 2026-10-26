package Interfaz.calendarizacion;

import utils.Horarios;
import logic.Recurso;
import logic.Reserva;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TableModelCalendarizacion extends AbstractTableModel {

    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private List<LocalTime> horas = Horarios.horasDelDia();
    private List<Recurso> recursos = new ArrayList<>();
    private String[][] celdas = new String[0][0];

    public void setDatos(List<Recurso> recursosCategoria, List<Reserva> reservas, LocalDate fecha) {
        this.recursos = recursosCategoria != null ? recursosCategoria : new ArrayList<>();
        this.horas = Horarios.horasDelDia();
        this.celdas = new String[horas.size()][this.recursos.size()];

        if (fecha != null) {
            for (Reserva reserva : reservas) {
                if (reserva == null || !reserva.isActiva()) continue;
                if (reserva.getFecha() == null || !fecha.equals(reserva.getFecha())) continue;
                for (int c = 0; c < this.recursos.size(); c++) {
                    Recurso recurso = this.recursos.get(c);
                    if (recurso == null || !reserva.usaRecurso(recurso.getId())) continue;
                    for (int f = 0; f < horas.size(); f++) {
                        if (reserva.cubreHora(horas.get(f))) {
                            String actual = celdas[f][c];
                            String etiqueta = reserva.etiquetaCelda();
                            celdas[f][c] = (actual == null || actual.isBlank()) ? etiqueta : actual + " | " + etiqueta;
                        }
                    }
                }
            }
        }
        fireTableStructureChanged();
    }

    @Override
    public int getRowCount() {
        return horas.size();
    }

    @Override
    public int getColumnCount() {
        return 1 + recursos.size();
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) return "Hora";
        Recurso r = recursos.get(column - 1);
        return r.getDescripcion() != null ? r.getDescripcion() : r.getId();
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
