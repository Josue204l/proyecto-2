package reservas.presentation.recursos;

import reservas.logic.Categoria;
import reservas.logic.Recurso;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private final TableModel tabla = new TableModel();
    private List<Recurso> todos = new ArrayList<>();

    public TableModel getTabla() { return tabla; }

    public void setTodos(List<Recurso> recursos) {
        this.todos = recursos == null ? new ArrayList<>() : new ArrayList<>(recursos);
        tabla.setFilas(this.todos);
    }

    public void filtrar(Categoria categoria) {
        if (categoria == null || categoria.getId() == null || categoria.getId().isBlank()) {
            tabla.setFilas(todos);
            return;
        }
        List<Recurso> filtrados = new ArrayList<>();
        for (Recurso recurso : todos) {
            if (recurso.getCategoria() != null && categoria.getId().equals(recurso.getCategoria().getId())) {
                filtrados.add(recurso);
            }
        }
        tabla.setFilas(filtrados);
    }
}
