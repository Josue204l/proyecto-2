package reservas.presentation.calendarizacion;

import reservas.client.Service;
import reservas.logic.Categoria;
import reservas.logic.Recurso;
import reservas.logic.Reserva;
import reservas.presentation.ReportePdf;
import reservas.presentation.Ui;

import javax.swing.JTable;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private final View view;
    private final TableModel tabla = new TableModel();
    private final Service service;

    public Controller(View view, Service service) {
        this.view = view;
        this.service = service;
        view.setController(this);
        view.getTabla().setModel(tabla);
        try {
            view.setCategorias(service.listarCategorias());
        } catch (Exception ex) {
            Ui.error(view, ex);
        }
    }

    public void mostrar() {
        try {
            Categoria categoria = view.getCategoria();
            if (categoria == null || categoria.getId() == null || categoria.getId().isBlank()) {
                Ui.aviso(view, "Seleccione una categoría.");
                return;
            }
            List<Recurso> recursos = new ArrayList<>();
            for (Recurso recurso : service.listarRecursos()) {
                if (recurso.getCategoria() != null && categoria.getId().equals(recurso.getCategoria().getId())) {
                    recursos.add(recurso);
                }
            }
            tabla.setDatos(recursos, service.listarReservas(), Ui.leerFecha(view.getFecha()));
        } catch (Exception ex) {
            Ui.error(view, ex);
        }
    }

    public void refrescar() {
        Categoria categoria = view.getCategoria();
        if (categoria == null || categoria.getId() == null || categoria.getId().isBlank()) return;
        mostrar();
    }

    public void pdf() {
        JTable grilla = view.getTabla();
        List<String> lineas = new ArrayList<>();
        StringBuilder encabezado = new StringBuilder();
        for (int c = 0; c < grilla.getColumnCount(); c++) {
            if (c > 0) encabezado.append(" | ");
            encabezado.append(grilla.getColumnName(c));
        }
        lineas.add(encabezado.toString());
        for (int f = 0; f < grilla.getRowCount(); f++) {
            StringBuilder fila = new StringBuilder();
            for (int c = 0; c < grilla.getColumnCount(); c++) {
                if (c > 0) fila.append(" | ");
                Object valor = grilla.getValueAt(f, c);
                fila.append(valor == null ? "" : valor);
            }
            lineas.add(fila.toString());
        }
        ReportePdf.guardarYAbrir(view, "calendarizacion.pdf", "Calendarización de recursos", lineas);
    }
}
