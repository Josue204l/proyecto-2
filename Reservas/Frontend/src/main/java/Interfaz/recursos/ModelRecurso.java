package Interfaz.recursos;

import frontend.red.ServicioActual;
import logic.Categoria;
import logic.Recurso;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModelRecurso {

    public static final String SELECCIONADO = "seleccionado";
    public static final String LISTA = "lista";

    private Recurso seleccionado;
    private final TableModelRecurso tableModel;
    private final PropertyChangeSupport propertyChangeSupport;

    public ModelRecurso() {
        this.seleccionado = new Recurso();
        this.tableModel = new TableModelRecurso(new ArrayList<>(getRecursos()));
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public Recurso getSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(Recurso seleccionado) {
        Recurso old = this.seleccionado;
        this.seleccionado = seleccionado;
        propertyChangeSupport.firePropertyChange(SELECCIONADO, old, seleccionado);
    }

    public TableModelRecurso getTableModel() {
        return tableModel;
    }

    public List<Recurso> getRecursos() {
        try { return ServicioActual.obtener().getRecursos(); }
        catch (Exception e) { throw new IllegalStateException("No se pudieron cargar los recursos: " + e.getMessage(), e); }
    }

    public List<Recurso> getRecursosPorCategoria(Categoria categoria) {
        return filtrar(categoria, "");
    }

    public List<Recurso> filtrar(Categoria categoria, String descripcion) {
        String filtro = descripcion == null ? "" : descripcion.trim().toLowerCase();
        return getRecursos().stream()
                .filter(r -> categoria == null
                        || (r.getCategoria() != null && categoria.getId().equals(r.getCategoria().getId())))
                .filter(r -> filtro.isEmpty()
                        || (r.getDescripcion() != null && r.getDescripcion().toLowerCase().contains(filtro)))
                .collect(Collectors.toList());
    }

    public void mostrar(List<Recurso> lista) {
        this.tableModel.setFilas(lista);
        propertyChangeSupport.firePropertyChange(LISTA, null, lista);
    }

    public List<Categoria> getCategorias() {
        try { return ServicioActual.obtener().getCategorias(); }
        catch (Exception e) { throw new IllegalStateException("No se pudieron cargar las categorías: " + e.getMessage(), e); }
    }

    public void guardar(Recurso recurso) throws Exception {
        ServicioActual.obtener().guardarRecurso(recurso);
        mostrar(getRecursos());
    }

    public boolean eliminar(String id) throws Exception {
        boolean enUso = ServicioActual.obtener().getReservas().stream()
                .anyMatch(r -> r.isActiva() && r.usaRecurso(id));
        if (enUso) {
            throw new Exception("No se puede borrar un recurso con reservas activas.");
        }
        boolean existe = getRecursos().stream().anyMatch(r -> r.getId().equals(id));
        if (!existe) return false;
        ServicioActual.obtener().eliminarRecurso(id);
        mostrar(getRecursos());
        return true;
    }
}
