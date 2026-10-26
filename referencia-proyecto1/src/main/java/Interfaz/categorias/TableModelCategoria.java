package Interfaz.categorias;

import logic.Categoria;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class TableModelCategoria extends AbstractTableModel {

    public static final int ID = 0;
    public static final int DESCRIPCION = 1;

    private final String[] cols = {"ID", "Descripción"};
    private List<Categoria> filas;

    public TableModelCategoria(List<Categoria> filas) {
        this.filas = filas;
    }

    public List<Categoria> getFilas() {
        return filas;
    }

    public void setFilas(List<Categoria> filas) {
        this.filas = filas;
        fireTableDataChanged();
    }

    public Categoria getRowAt(int row) {
        if (filas != null && row >= 0 && row < filas.size()) {
            return filas.get(row);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return filas != null ? filas.size() : 0;
    }

    @Override
    public int getColumnCount() {
        return cols.length;
    }

    @Override
    public String getColumnName(int col) {
        return cols[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        Categoria c = filas.get(row);
        switch (col) {
            case ID:
                return c.getId();
            case DESCRIPCION:
                return c.getEtiqueta();
            default:
                return "";
        }
    }
}
