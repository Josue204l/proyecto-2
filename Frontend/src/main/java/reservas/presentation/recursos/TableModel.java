package reservas.presentation.recursos;

import reservas.logic.Recurso;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TableModel extends AbstractTableModel {
    private List<Recurso> filas = new ArrayList<>();

    public void setFilas(List<Recurso> filas) {
        this.filas = filas == null ? new ArrayList<>() : new ArrayList<>(filas);
        fireTableDataChanged();
    }

    public Recurso fila(int indice) {
        if (indice < 0 || indice >= filas.size()) return null;
        return filas.get(indice);
    }

    public List<Recurso> getFilas() { return filas; }

    @Override public int getRowCount() { return filas.size(); }
    @Override public int getColumnCount() { return 3; }
    @Override public String getColumnName(int column) {
        return switch (column) { case 0 -> "Id"; case 1 -> "Descripción"; default -> "Categoría"; };
    }

    @Override
    public Object getValueAt(int row, int column) {
        Recurso recurso = filas.get(row);
        return switch (column) {
            case 0 -> recurso.getId();
            case 1 -> recurso.getDescripcion();
            default -> recurso.getCategoria() == null ? "" : recurso.getCategoria().getEtiqueta();
        };
    }
}
