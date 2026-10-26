package Interfaz.recursos;

import data.Data;
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
        return Data.getInstancia().getRecursos();
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
        return Data.getInstancia().getCategorias();
    }

    public void guardar(Recurso recurso) throws Exception {
        List<Recurso> recursos = Data.getInstancia().getRecursos();
        int index = -1;
        for (int i = 0; i < recursos.size(); i++) {
            if (recursos.get(i).getId().equals(recurso.getId())) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            recursos.set(index, recurso);
        } else {
            recursos.add(recurso);
        }
        Data.getInstancia().guardarRecursos();
        this.tableModel.setFilas(new ArrayList<>(recursos));
        propertyChangeSupport.firePropertyChange(LISTA, null, recursos);
    }

    public boolean eliminar(String id) throws Exception {
        boolean enUso = Data.getInstancia().getReservas().stream()
                .anyMatch(r -> r.isActiva() && r.usaRecurso(id));
        if (enUso) {
            throw new Exception("No se puede borrar un recurso con reservas activas.");
        }
        boolean eliminado = Data.getInstancia().getRecursos().removeIf(r -> r.getId().equals(id));
        if (eliminado) {
            Data.getInstancia().guardarRecursos();
            this.tableModel.setFilas(new ArrayList<>(getRecursos()));
            propertyChangeSupport.firePropertyChange(LISTA, null, getRecursos());
        }
        return eliminado;
    }
}
