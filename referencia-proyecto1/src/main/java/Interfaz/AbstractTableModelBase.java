package Interfaz;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public abstract class AbstractTableModelBase<E> extends AbstractTableModel {

    protected int[] cols;
    protected List<E> rows;
    protected String[] colNames;

    public AbstractTableModelBase(int[] cols, List<E> rows) {
        this.cols = cols;
        this.rows = rows;
        initColNames();
    }

    protected abstract void initColNames();

    protected abstract Object getPropetyAt(E e, int col);

    @Override
    public int getRowCount() {
        return rows != null ? rows.size() : 0;
    }

    @Override
    public int getColumnCount() {
        return cols != null ? cols.length : 0;
    }

    @Override
    public String getColumnName(int col) {
        return colNames[cols[col]];
    }

    @Override
    public Object getValueAt(int row, int col) {
        E e = rows.get(row);
        return getPropetyAt(e, col);
    }

    public E getRowAt(int row) {
        if (rows != null && row >= 0 && row < rows.size()) {
            return rows.get(row);
        }
        return null;
    }

    public List<E> getRows() {
        return rows;
    }

    public void setRows(List<E> rows) {
        this.rows = rows;
        fireTableDataChanged();
    }
}