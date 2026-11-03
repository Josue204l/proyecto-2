package reservas.presentation.categorias;

import reservas.logic.Categoria;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TableModel extends AbstractTableModel {
    private List<Categoria> filas = new ArrayList<>();

    public void setFilas(List<Categoria> filas) {
        this.filas = filas == null ? new ArrayList<>() : new ArrayList<>(filas);
        fireTableDataChanged();
    }

    public Categoria fila(int indice) {
        if (indice < 0 || indice >= filas.size()) return null;
        return filas.get(indice);
    }

    public List<Categoria> getFilas() { return filas; }

    @Override public int getRowCount() { return filas.size(); }
    @Override public int getColumnCount() { return 2; }
    @Override public String getColumnName(int column) { return column == 0 ? "Id" : "Descripción"; }

    @Override
    public Object getValueAt(int row, int column) {
        Categoria categoria = filas.get(row);
        return column == 0 ? categoria.getId() : categoria.getDescripcion();
    }
}
