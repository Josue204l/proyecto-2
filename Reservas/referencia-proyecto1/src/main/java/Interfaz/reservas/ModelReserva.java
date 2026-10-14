package Interfaz.reservas;

import logic.Categoria;
import logic.Recurso;
import logic.Reserva;
import logic.Service;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModelReserva {

    public static final String SELECCIONADO = "seleccionado";
    public static final String LISTA = "lista";

    private Reserva seleccionado;
    private TableModelReserva tableModel;
    private final PropertyChangeSupport propertyChangeSupport;
    private String funcionarioId;

    public ModelReserva() {
        this.seleccionado = new Reserva();
        this.tableModel = new TableModelReserva(new ArrayList<>());
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void setFuncionarioId(String funcionarioId) {
        this.funcionarioId = funcionarioId;
        refrescarTabla();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public Reserva getSeleccionado() { return seleccionado; }

    public void setSeleccionado(Reserva seleccionado) {
        Reserva old = this.seleccionado;
        this.seleccionado = seleccionado;
        propertyChangeSupport.firePropertyChange(SELECCIONADO, old, seleccionado);
    }

    public TableModelReserva getTableModel() { return tableModel; }

    public List<Reserva> getReservas() {
        return Service.instance().getReservas();
    }

    public List<Reserva> getReservasDelFuncionario() {
        if (funcionarioId == null) return new ArrayList<>(getReservas());
        return getReservas().stream()
                .filter(r -> r.getSolicitante() != null && funcionarioId.equals(r.getSolicitante().getId()))
                .collect(Collectors.toList());
    }

    public void refrescarTabla() {
        this.tableModel.setFilas(getReservasDelFuncionario());
        propertyChangeSupport.firePropertyChange(LISTA, null, getReservasDelFuncionario());
    }

    public List<Recurso> asignarRecursosDisponibles(List<Categoria> categorias, LocalDate fecha,
                                                      LocalTime inicio, LocalTime fin) throws Exception {
        return logic.Service.instance().asignarRecursosDisponibles(categorias, fecha, inicio, fin);
    }

    public void guardar(Reserva reserva) throws Exception {
        logic.Service.instance().guardarReserva(reserva);
        refrescarTabla();
    }

    public String generarId() {
        return "RES-" + System.currentTimeMillis();
    }

    public Reserva buscarPorId(String id) {
        return getReservas().stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public boolean cancelar(String id) throws Exception {
        boolean resultado = logic.Service.instance().cancelarReserva(id);
        refrescarTabla();
        return resultado;
    }

    public boolean eliminar(String id) {
        try {
            return cancelar(id);
        } catch (Exception e) {
            return false;
        }
    }
}
