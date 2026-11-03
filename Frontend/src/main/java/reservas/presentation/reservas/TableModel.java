package reservas.presentation.reservas;

import reservas.logic.Reserva;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TableModel extends AbstractTableModel {
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private List<Reserva> filas = new ArrayList<>();

    public void setFilas(List<Reserva> filas) {
        this.filas = filas == null ? new ArrayList<>() : new ArrayList<>(filas);
        fireTableDataChanged();
    }

    public Reserva fila(int indice) {
        if (indice < 0 || indice >= filas.size()) return null;
        return filas.get(indice);
    }

    public List<Reserva> getFilas() { return filas; }

    @Override public int getRowCount() { return filas.size(); }
    @Override public int getColumnCount() { return 5; }
    @Override public String getColumnName(int column) {
        return switch (column) {
            case 0 -> "Actividad";
            case 1 -> "Fecha";
            case 2 -> "Inicio";
            case 3 -> "Fin";
            default -> "Estado";
        };
    }

    @Override
    public Object getValueAt(int row, int column) {
        Reserva reserva = filas.get(row);
        return switch (column) {
            case 0 -> reserva.getTitulo();
            case 1 -> reserva.getFecha() == null ? "" : reserva.getFecha().toString();
            case 2 -> reserva.getHoraInicio() == null ? "" : reserva.getHoraInicio().format(HORA);
            case 3 -> reserva.getHoraFin() == null ? "" : reserva.getHoraFin().format(HORA);
            default -> reserva.getEstado();
        };
    }
}
