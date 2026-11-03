package reservas.presentation.estadisticas;

import reservas.logic.Reserva;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TableModel extends AbstractTableModel {
    private final String tituloCantidad;
    private List<Model.Fila> filas = new ArrayList<>();

    public TableModel(String tituloCantidad) {
        this.tituloCantidad = tituloCantidad;
    }

    public void setFilas(List<Model.Fila> filas) {
        this.filas = filas == null ? new ArrayList<>() : new ArrayList<>(filas);
        fireTableDataChanged();
    }

    public List<Model.Fila> getFilas() { return filas; }

    @Override public int getRowCount() { return filas.size(); }
    @Override public int getColumnCount() { return 2; }
    @Override public String getColumnName(int column) { return column == 0 ? "Detalle" : tituloCantidad; }

    @Override
    public Object getValueAt(int row, int column) {
        Model.Fila fila = filas.get(row);
        return column == 0 ? fila.etiqueta : fila.cantidad;
    }
}
