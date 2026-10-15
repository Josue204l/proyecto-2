package Interfaz.recursos;

import logic.Recurso;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class TableModelRecurso extends AbstractTableModel {
    private static final String[] COLUMNAS = {"ID / Activo", "Categoría", "Descripción"};
    private List<Recurso> filas;

    public TableModelRecurso(List<Recurso> filas) {
        this.filas = filas;
    }

    public void setFilas(List<Recurso> filas) {
        this.filas = filas;
        fireTableDataChanged();
    }

    public Recurso getRowAt(int row) {
        if (filas == null || row < 0 || row >= filas.size()) return null;
        return filas.get(row);
    }

    @Override
    public int getRowCount() {
        return filas != null ? filas.size() : 0;
    }

    @Override
    public int getColumnCount() {
        return COLUMNAS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNAS[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Recurso recurso = filas.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return recurso.getId();
            case 1:
                return recurso.getCategoria() != null ? recurso.getCategoria().getEtiqueta() : "";
            case 2:
                return recurso.getDescripcion();
            default:
                return "";
        }
    }
}