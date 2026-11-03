package reservas.presentation.categorias;

import reservas.client.Service;
import reservas.logic.Categoria;
import reservas.presentation.ReportePdf;
import reservas.presentation.Ui;

import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private final View view;
    private final Model model;
    private final Service service;

    public Controller(View view, Model model, Service service) {
        this.view = view;
        this.model = model;
        this.service = service;
        view.setController(this);
        view.getTabla().setModel(model.getTabla());
        refrescar();
    }

    public void refrescar() {
        try {
            model.setTodas(service.listarCategorias());
        } catch (Exception ex) {
            Ui.error(view, ex);
        }
    }

    public void buscar() { model.buscar(view.getBuscar()); }

    public void guardar() {
        try {
            service.guardarCategoria(new Categoria(view.getId(), view.getDescripcion()));
            refrescar();
            view.limpiar();
            Ui.aviso(view, "Categoría guardada.");
        } catch (Exception ex) {
            Ui.error(view, ex);
        }
    }

    public void eliminar() {
        if (view.getId().isBlank()) {
            Ui.aviso(view, "Seleccione una categoría.");
            return;
        }
        if (JOptionPane.showConfirmDialog(view, "¿Borrar la categoría " + view.getId() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            service.eliminarCategoria(view.getId());
            refrescar();
            view.limpiar();
        } catch (Exception ex) {
            Ui.error(view, ex);
        }
    }

    public void seleccionar(int fila) {
        Categoria categoria = model.getTabla().fila(fila);
        if (categoria != null) view.mostrar(categoria);
    }

    public void pdf() {
        List<String> lineas = new ArrayList<>();
        lineas.add("Id | Descripción");
        for (Categoria categoria : model.getTabla().getFilas()) {
            lineas.add(categoria.getId() + " | " + categoria.getDescripcion());
        }
        ReportePdf.guardarYAbrir(view, "categorias.pdf", "Listado de categorías", lineas);
    }
}
