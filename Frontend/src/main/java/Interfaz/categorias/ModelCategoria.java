package Interfaz.categorias;

import frontend.red.ServicioActual;
import logic.Categoria;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModelCategoria {

    public static final String SELECCIONADO = "seleccionado";
    public static final String LISTA = "lista";

    private Categoria seleccionado;
    private TableModelCategoria tableModel;
    private final PropertyChangeSupport propertyChangeSupport;

    public ModelCategoria() {
        this.seleccionado = new Categoria();
        this.tableModel = new TableModelCategoria(new ArrayList<>(getCategorias()));
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public Categoria getSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(Categoria seleccionado) {
        Categoria old = this.seleccionado;
        this.seleccionado = seleccionado;
        propertyChangeSupport.firePropertyChange(SELECCIONADO, old, seleccionado);
    }

    public TableModelCategoria getTableModel() {
        return tableModel;
    }

    public List<Categoria> getCategorias() {
        try { return ServicioActual.obtener().getCategorias(); }
        catch (Exception e) { throw new IllegalStateException("No se pudieron cargar las categorías: " + e.getMessage(), e); }
    }

    public List<Categoria> buscar(String descripcion) {
        String filtro = descripcion == null ? "" : descripcion.trim().toLowerCase();
        if (filtro.isEmpty()) return new ArrayList<>(getCategorias());
        return getCategorias().stream()
                .filter(c -> c.getEtiqueta() != null && c.getEtiqueta().toLowerCase().contains(filtro))
                .collect(Collectors.toList());
    }

    public void mostrar(List<Categoria> lista) {
        this.tableModel.setFilas(lista);
        propertyChangeSupport.firePropertyChange(LISTA, null, lista);
    }

    public String generarId() {
        int max = 0;
        for (Categoria c : getCategorias()) {
            if (c.getId() != null && c.getId().toUpperCase().startsWith("CAT-")) {
                try {
                    max = Math.max(max, Integer.parseInt(c.getId().substring(4)));
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("CAT-%03d", max + 1);
    }

    public void guardar(Categoria categoria) throws Exception {
        if (categoria.getDescripcion() == null || categoria.getDescripcion().trim().isEmpty()) {
            throw new Exception("La descripción de la categoría es obligatoria.");
        }
        categoria.setDescripcion(categoria.getDescripcion().trim());
        categoria.setNombre(categoria.getDescripcion());

        if (categoria.getId() == null || categoria.getId().isBlank()) categoria.setId(generarId());
        ServicioActual.obtener().guardarCategoria(categoria);
        mostrar(getCategorias());
    }

    public boolean eliminar(String id) throws Exception {
        boolean enUso = ServicioActual.obtener().getRecursos().stream()
                .anyMatch(r -> r.getCategoria() != null && id.equals(r.getCategoria().getId()));
        if (enUso) {
            throw new Exception("No se puede borrar una categoría que tiene recursos asociados.");
        }
        boolean existe = getCategorias().stream().anyMatch(c -> c.getId().equals(id));
        if (!existe) return false;
        ServicioActual.obtener().eliminarCategoria(id);
        mostrar(getCategorias());
        return true;
    }
}
