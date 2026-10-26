package Interfaz.funcionarios;

import logic.Funcionario;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class TableModelFuncionario extends AbstractTableModel {

    public static final int ID = 0;
    public static final int NOMBRE = 1;
    public static final int TELEFONO = 2;

    private final String[] cols = {"ID", "Nombre", "Teléfono"};
    private List<Funcionario> filas;

    public TableModelFuncionario(List<Funcionario> filas) {
        this.filas = filas;
    }

    public List<Funcionario> getFilas() {
        return filas;
    }

    public void setFilas(List<Funcionario> filas) {
        this.filas = filas;
        fireTableDataChanged();
    }

    public Funcionario getRowAt(int row) {
        if (filas == null || row < 0 || row >= filas.size()) return null;
        return filas.get(row);
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
        Funcionario f = filas.get(row);
        switch (col) {
            case ID: return f.getId();
            case NOMBRE: return f.getNombre();
            case TELEFONO: return f.getTelefono();
            default: return "";
        }
    }
}
