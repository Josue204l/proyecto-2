package Interfaz.calendarizacion;

import data.Data;
import logic.Categoria;
import logic.Recurso;
import logic.Reserva;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModelCalendarizacion {

    public static final String MATRIZ = "matriz";
    public static final String CATEGORIAS = "categorias";
    public static final String RESERVAS = "reservas";

    private final PropertyChangeSupport propertyChangeSupport;
    private final TableModelCalendarizacion tableModel;
    private LocalDate fecha;
    private Categoria categoria;

    public ModelCalendarizacion() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.tableModel = new TableModelCalendarizacion();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public TableModelCalendarizacion getTableModel() {
        return tableModel;
    }

    public List<Categoria> getCategorias() {
        return Data.getInstancia().getCategorias();
    }

    public List<Recurso> getRecursosDeCategoria(Categoria categoria) {
        if (categoria == null) return new ArrayList<>();
        return Data.getInstancia().getRecursos().stream()
                .filter(r -> r.getCategoria() != null && categoria.getId().equals(r.getCategoria().getId()))
                .collect(Collectors.toList());
    }

    public void cargarMatriz(LocalDate fecha, Categoria categoria) {
        this.fecha = fecha;
        this.categoria = categoria;
        List<Recurso> recursos = getRecursosDeCategoria(categoria);
        tableModel.setDatos(recursos, Data.getInstancia().getReservas(), fecha);
        propertyChangeSupport.firePropertyChange(MATRIZ, null, tableModel);
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Categoria getCategoria() {
        return categoria;
    }
}