package reservas.presentation.funcionarios;

import reservas.logic.Funcionario;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TableModel extends AbstractTableModel {
    private final String[] columnas = {"Id", "Nombre", "Teléfono", "Rol"};
    private List<Funcionario> filas = new ArrayList<>();

    public void setFilas(List<Funcionario> filas) {
        this.filas = filas == null ? new ArrayList<>() : new ArrayList<>(filas);
        fireTableDataChanged();
    }

    public Funcionario fila(int indice) {
        if (indice < 0 || indice >= filas.size()) return null;
        return filas.get(indice);
    }

    public List<Funcionario> getFilas() { return filas; }

    @Override public int getRowCount() { return filas.size(); }
    @Override public int getColumnCount() { return columnas.length; }
    @Override public String getColumnName(int column) { return columnas[column]; }

    @Override
    public Object getValueAt(int row, int column) {
        Funcionario funcionario = filas.get(row);
        return switch (column) {
            case 0 -> funcionario.getId();
            case 1 -> funcionario.getNombre();
            case 2 -> funcionario.getTelefono();
            default -> funcionario.getRol();
        };
    }
}
