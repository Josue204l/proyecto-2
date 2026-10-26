package Interfaz.funcionarios;

import data.Data;
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
        return Data.getInstancia().getFuncionarios();
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
        List<Funcionario> funcionarios = Data.getInstancia().getFuncionarios();
        int index = -1;
        for (int i = 0; i < funcionarios.size(); i++) {
            if (funcionarios.get(i).getId().equals(funcionario.getId())) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            Funcionario existente = funcionarios.get(index);
            funcionario.setClave(existente.getClave());
            funcionario.setRol(existente.getRol());
            funcionarios.set(index, funcionario);
        } else {
            funcionario.setClave(funcionario.getId());
            if (funcionario.getRol() == null || funcionario.getRol().isBlank()) {
                funcionario.setRol("FUNCIONARIO");
            }
            funcionarios.add(funcionario);
        }

        Data.getInstancia().guardarFuncionarios();
        this.tableModel.setFilas(new ArrayList<>(funcionarios));
        propertyChangeSupport.firePropertyChange(LISTA, null, funcionarios);
    }

    public boolean eliminar(String id) throws Exception {
        Funcionario objetivo = Data.getInstancia().buscarFuncionario(id);
        if (objetivo == null) return false;
        if ("ADMIN".equalsIgnoreCase(objetivo.getRol())) {
            throw new Exception("No se puede borrar el usuario administrador.");
        }
        boolean tieneReservas = Data.getInstancia().getReservas().stream()
                .anyMatch(r -> r.isActiva()
                        && r.getSolicitante() != null
                        && id.equals(r.getSolicitante().getId()));
        if (tieneReservas) {
            throw new Exception("No se puede borrar un funcionario con reservas activas.");
        }
        boolean eliminado = Data.getInstancia().getFuncionarios().removeIf(f -> f.getId().equals(id));
        if (eliminado) {
            Data.getInstancia().guardarFuncionarios();
            this.tableModel.setFilas(new ArrayList<>(getFuncionarios()));
            propertyChangeSupport.firePropertyChange(LISTA, null, getFuncionarios());
        }
        return eliminado;
    }
}
