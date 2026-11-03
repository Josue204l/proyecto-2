package reservas.presentation.funcionarios;

import reservas.logic.Funcionario;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Model {
    private final TableModel tabla = new TableModel();
    private List<Funcionario> todos = new ArrayList<>();

    public TableModel getTabla() { return tabla; }

    public void setTodos(List<Funcionario> funcionarios) {
        this.todos = funcionarios == null ? new ArrayList<>() : new ArrayList<>(funcionarios);
        tabla.setFilas(this.todos);
    }

    public void buscar(String id, String nombre) {
        String idFiltro = id == null ? "" : id.trim().toLowerCase(Locale.ROOT);
        String nombreFiltro = nombre == null ? "" : nombre.trim().toLowerCase(Locale.ROOT);
        List<Funcionario> filtrados = new ArrayList<>();
        for (Funcionario funcionario : todos) {
            boolean coincideId = idFiltro.isEmpty() || (funcionario.getId() != null
                    && funcionario.getId().toLowerCase(Locale.ROOT).contains(idFiltro));
            boolean coincideNombre = nombreFiltro.isEmpty() || (funcionario.getNombre() != null
                    && funcionario.getNombre().toLowerCase(Locale.ROOT).contains(nombreFiltro));
            if (coincideId && coincideNombre) filtrados.add(funcionario);
        }
        tabla.setFilas(filtrados);
    }
}
