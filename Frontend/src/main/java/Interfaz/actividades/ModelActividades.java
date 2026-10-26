package Interfaz.actividades;

import frontend.red.ServicioActual;
import logic.Reserva;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ModelActividades {

    public static final String LISTA = "lista";

    private final TableModelActividades tableModel;
    private final PropertyChangeSupport propertyChangeSupport;
    private LocalDate fechaReferencia;

    public ModelActividades() {
        this.tableModel = new TableModelActividades();
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.fechaReferencia = LocalDate.now();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public TableModelActividades getTableModel() {
        return tableModel;
    }

    public List<Reserva> getActividades() {
        try { return ServicioActual.obtener().getReservas(); }
        catch (Exception e) { throw new IllegalStateException("No se pudieron cargar las actividades: " + e.getMessage(), e); }
    }

    public void cargarSemana(LocalDate fechaReferencia) {
        this.fechaReferencia = fechaReferencia;
        tableModel.setDatos(fechaReferencia, getActividades());
        propertyChangeSupport.firePropertyChange(LISTA, null, tableModel);
    }

    public String etiquetaSemana() {
        if (fechaReferencia == null) return "Semana";
        LocalDate lunes = fechaReferencia.with(DayOfWeek.MONDAY);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return "Semana " + lunes.format(fmt) + " - " + lunes.plusDays(6).format(fmt);
    }

    public LocalDate getFechaReferencia() {
        return fechaReferencia;
    }
}
