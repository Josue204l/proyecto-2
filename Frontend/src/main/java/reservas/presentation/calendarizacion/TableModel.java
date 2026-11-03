package reservas.presentation.calendarizacion;

import reservas.logic.Recurso;
import reservas.logic.Reserva;
import reservas.presentation.Horarios;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TableModel extends AbstractTableModel {
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private List<LocalTime> horas = Horarios.horasDelDia();
    private List<Recurso> recursos = new ArrayList<>();
    private String[][] celdas = new String[0][0];

    public void setDatos(List<Recurso> recursosCategoria, List<Reserva> reservas, LocalDate fecha) {
        this.recursos = recursosCategoria == null ? new ArrayList<>() : new ArrayList<>(recursosCategoria);
        this.horas = Horarios.horasDelDia();
        this.celdas = new String[horas.size()][this.recursos.size()];
        if (fecha != null && reservas != null) {
            for (Reserva reserva : reservas) {
                if (reserva == null || !reserva.isActiva() || !fecha.equals(reserva.getFecha())) continue;
                for (int columna = 0; columna < this.recursos.size(); columna++) {
                    Recurso recurso = this.recursos.get(columna);
                    if (recurso == null || !reserva.usaRecurso(recurso.getId())) continue;
                    for (int fila = 0; fila < horas.size(); fila++) {
                        if (!reserva.cubreHora(horas.get(fila))) continue;
                        String actual = celdas[fila][columna];
                        String etiqueta = reserva.etiquetaCelda();
                        celdas[fila][columna] = actual == null || actual.isBlank() ? etiqueta : actual + " | " + etiqueta;
                    }
                }
            }
        }
        fireTableStructureChanged();
    }

    public String[][] getCeldas() { return celdas; }
    public List<Recurso> getRecursos() { return recursos; }
    public List<LocalTime> getHoras() { return horas; }

    @Override public int getRowCount() { return horas.size(); }
    @Override public int getColumnCount() { return 1 + recursos.size(); }

    @Override
    public String getColumnName(int column) {
        if (column == 0) return "Hora";
        Recurso recurso = recursos.get(column - 1);
        return recurso.getDescripcion() == null ? recurso.getId() : recurso.getDescripcion();
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (column == 0) return horas.get(row).format(HORA);
        String valor = celdas[row][column - 1];
        return valor == null ? "" : valor;
    }

    @Override public boolean isCellEditable(int row, int column) { return false; }
}
