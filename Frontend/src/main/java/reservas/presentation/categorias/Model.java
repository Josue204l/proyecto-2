package reservas.presentation.categorias;

import reservas.logic.Categoria;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Model {
    private final TableModel tabla = new TableModel();
    private List<Categoria> todas = new ArrayList<>();

    public TableModel getTabla() { return tabla; }

    public void setTodas(List<Categoria> categorias) {
        this.todas = categorias == null ? new ArrayList<>() : new ArrayList<>(categorias);
        tabla.setFilas(this.todas);
    }

    public void buscar(String descripcion) {
        String filtro = descripcion == null ? "" : descripcion.trim().toLowerCase(Locale.ROOT);
        List<Categoria> filtradas = new ArrayList<>();
        for (Categoria categoria : todas) {
            if (filtro.isEmpty() || categoria.getEtiqueta().toLowerCase(Locale.ROOT).contains(filtro)) {
                filtradas.add(categoria);
            }
        }
        tabla.setFilas(filtradas);
    }
}
