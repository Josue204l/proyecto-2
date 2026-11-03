package reservas.presentation.recursos;

import reservas.client.Service;
import reservas.logic.Categoria;
import reservas.logic.Recurso;
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
            view.setCategorias(service.listarCategorias());
            model.setTodos(service.listarRecursos());
            filtrar();
        } catch (Exception ex) {
            Ui.error(view, ex);
        }
    }

    public void filtrar() { model.filtrar(view.getFiltro()); }

    public void guardar() {
        try {
            service.guardarRecurso(new Recurso(view.getId(), view.getDescripcion(), view.getCategoria()));
            refrescar();
            view.limpiar();
            Ui.aviso(view, "Recurso guardado.");
        } catch (Exception ex) {
            Ui.error(view, ex);
        }
    }

    public void eliminar() {
        if (view.getId().isBlank()) {
            Ui.aviso(view, "Seleccione un recurso.");
            return;
        }
        if (JOptionPane.showConfirmDialog(view, "¿Borrar el recurso " + view.getId() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            service.eliminarRecurso(view.getId());
            refrescar();
            view.limpiar();
        } catch (Exception ex) {
            Ui.error(view, ex);
        }
    }

    public void seleccionar(int fila) {
        Recurso recurso = model.getTabla().fila(fila);
        if (recurso != null) view.mostrar(recurso);
    }

    public void pdf() {
        List<String> lineas = new ArrayList<>();
        lineas.add("Id | Descripción | Categoría");
        for (Recurso recurso : model.getTabla().getFilas()) {
            String categoria = recurso.getCategoria() == null ? "" : recurso.getCategoria().getEtiqueta();
            lineas.add(recurso.getId() + " | " + recurso.getDescripcion() + " | " + categoria);
        }
        ReportePdf.guardarYAbrir(view, "recursos.pdf", "Listado de recursos", lineas);
    }
}
