package Interfaz.funcionarios;

import frontend.red.ServicioActual;
import logic.Funcionario;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModelFuncionario {

    public static final String SELECCIONADO = "seleccionado";
    public static final String LISTA = "lista";

    private Funcionario seleccionado;
    private TableModelFuncionario tableModel;
    private final PropertyChangeSupport propertyChangeSupport;

    public ModelFuncionario() {
        this.seleccionado = new Funcionario();
        this.tableModel = new TableModelFuncionario(new ArrayList<>(getFuncionarios()));
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public Funcionario getSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(Funcionario seleccionado) {
        Funcionario old = this.seleccionado;
        this.seleccionado = seleccionado;
        propertyChangeSupport.firePropertyChange(SELECCIONADO, old, seleccionado);
    }

    public TableModelFuncionario getTableModel() {
        return tableModel;
    }

    public List<Funcionario> getFuncionarios() {
        try { return ServicioActual.obtener().getFuncionarios(); }
        catch (Exception e) { throw new IllegalStateException("No se pudieron cargar los funcionarios: " + e.getMessage(), e); }
    }

    public List<Funcionario> buscar(String id, String nombre) {
        String idFiltro = id == null ? "" : id.trim().toLowerCase();
        String nombreFiltro = nombre == null ? "" : nombre.trim().toLowerCase();
        return getFuncionarios().stream()
                .filter(f -> idFiltro.isEmpty() || (f.getId() != null && f.getId().toLowerCase().contains(idFiltro)))
                .filter(f -> nombreFiltro.isEmpty() || (f.getNombre() != null && f.getNombre().toLowerCase().contains(nombreFiltro)))
                .collect(Collectors.toList());
    }

    public void mostrar(List<Funcionario> lista) {
        this.tableModel.setFilas(lista);
        propertyChangeSupport.firePropertyChange(LISTA, null, lista);
    }

    public void guardar(Funcionario funcionario) throws Exception {
        List<Funcionario> funcionarios = getFuncionarios();
        Funcionario existente = funcionarios.stream()
                .filter(f -> f.getId().equals(funcionario.getId())).findFirst().orElse(null);
        if (existente != null) {
            funcionario.setClave(existente.getClave());
            funcionario.setRol(existente.getRol());
        } else {
            funcionario.setClave(funcionario.getId());
            if (funcionario.getRol() == null || funcionario.getRol().isBlank()) {
                funcionario.setRol("FUNCIONARIO");
            }
        }
        ServicioActual.obtener().guardarFuncionario(funcionario);
        mostrar(getFuncionarios());
    }

    public boolean eliminar(String id) throws Exception {
        Funcionario objetivo = getFuncionarios().stream().filter(f -> f.getId().equals(id)).findFirst().orElse(null);
        if (objetivo == null) return false;
        if ("ADMIN".equalsIgnoreCase(objetivo.getRol())) {
            throw new Exception("No se puede borrar el usuario administrador.");
        }
        boolean tieneReservas = ServicioActual.obtener().getReservas().stream()
                .anyMatch(r -> r.isActiva()
                        && r.getSolicitante() != null
                        && id.equals(r.getSolicitante().getId()));
        if (tieneReservas) {
            throw new Exception("No se puede borrar un funcionario con reservas activas.");
        }
        ServicioActual.obtener().eliminarFuncionario(id);
        mostrar(getFuncionarios());
        return true;
    }
}
