package Interfaz.estadisticas;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TableModelEstadisticas extends AbstractTableModel {

    private List<String[]> filas;
    private String[] columnas;

    // Constructor sin parámetros
    public TableModelEstadisticas() {
        this.filas = new ArrayList<>();
        this.columnas = new String[]{"Etiqueta", "Cantidad"};
    }

    // Constructor que acepta la lista de filas
    public TableModelEstadisticas(List<String[]> filas) {
        this.filas = filas != null ? filas : new ArrayList<>();
        this.columnas = new String[]{"Etiqueta", "Cantidad"};
    }

    // Constructor completo con columnas personalizadas
    public TableModelEstadisticas(List<String[]> filas, String[] columnas) {
        this.filas = filas != null ? filas : new ArrayList<>();
        this.columnas = columnas != null ? columnas : new String[]{"Etiqueta", "Cantidad"};
    }

    /**
     * Permite actualizar dinámicamente las filas y los nombres de las columnas
     */
    public void setFilasGenericas(List<String[]> nuevasFilas, String[] nuevasColumnas) {
        this.filas = nuevasFilas != null ? nuevasFilas : new ArrayList<>();
        if (nuevasColumnas != null) {
            this.columnas = nuevasColumnas;
        }
        fireTableStructureChanged(); // Notifica que cambiaron columnas y datos
    }

    @Override
    public int getRowCount() {
        return filas.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length;
    }

    @Override
    public String getColumnName(int column) {
        if (column >= 0 && column < columnas.length) {
            return columnas[column];
        }
        return super.getColumnName(column);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex >= 0 && rowIndex < filas.size()) {
            String[] fila = filas.get(rowIndex);
            if (columnIndex >= 0 && columnIndex < fila.length) {
                return fila[columnIndex];
            }
        }
        return "";
    }
}