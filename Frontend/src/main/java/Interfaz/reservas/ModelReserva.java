package Interfaz.reservas;

import logic.Categoria;
import logic.Recurso;
import logic.Reserva;
import frontend.red.ServicioActual;

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
        try { return ServicioActual.obtener().getReservas(); }
        catch (Exception e) { throw new IllegalStateException("No se pudieron cargar las reservas: " + e.getMessage(), e); }
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
        List<Reserva> existentes = getReservas();
        List<Recurso> recursos = new ArrayList<>();
        for (Categoria categoria : categorias) {
            Recurso libre = ServicioActual.obtener().getRecursos().stream()
                    .filter(r -> r.getCategoria() != null && categoria.getId().equals(r.getCategoria().getId()))
                    .filter(r -> recursos.stream().noneMatch(a -> a.getId().equals(r.getId())))
                    .filter(r -> existentes.stream().filter(Reserva::isActiva)
                            .filter(res -> fecha.equals(res.getFecha()))
                            .noneMatch(res -> inicio.isBefore(res.getHoraFin())
                                    && res.getHoraInicio().isBefore(fin) && res.usaRecurso(r.getId())))
                    .findFirst().orElse(null);
            if (libre == null) throw new Exception("No hay recursos disponibles para " + categoria.getEtiqueta());
            recursos.add(libre);
        }
        return recursos;
    }

    public void guardar(Reserva reserva) throws Exception {
        List<String> idsCategorias = reserva.getCategoriasRequeridas().stream().map(Categoria::getId).toList();
        ServicioActual.obtener().crearReserva(reserva.getTitulo(), reserva.getFecha(), reserva.getHoraInicio(),
                reserva.getHoraFin(), idsCategorias, reserva.getSolicitante().getId());
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
        ServicioActual.obtener().cancelarReserva(id);
        refrescarTabla();
        return true;
    }

    public boolean eliminar(String id) {
        try {
            return cancelar(id);
        } catch (Exception e) {
            return false;
        }
    }
}
