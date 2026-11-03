package reservas.presentation.estadisticas;

import reservas.client.Service;
import reservas.logic.Reserva;
import reservas.presentation.ReportePdf;
import reservas.presentation.Ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private final View view;
    private final Model model;
    private final Service service;
    private final TableModel tablaRecursos = new TableModel("Cantidad");
    private final TableModel tablaActividades = new TableModel("Actividades");

    public Controller(View view, Model model, Service service) {
        this.view = view;
        this.model = model;
        this.service = service;
        view.setController(this);
        view.getTablaRecursos().setModel(tablaRecursos);
        view.getTablaActividades().setModel(tablaActividades);
    }

    public void calcularRecursos() {
        try {
            LocalDate desde = Ui.leerFecha(view.getDesdeRecursos());
            LocalDate hasta = Ui.leerFecha(view.getHastaRecursos());
            if (hasta.isBefore(desde)) throw new Exception("La fecha hasta debe ser posterior o igual a la fecha desde.");
            List<Model.Fila> filas = model.recursos(service.listarReservas(), desde, hasta);
            tablaRecursos.setFilas(filas);
            view.getGraficoRecursos().setDatos(etiquetas(filas), valores(filas));
        } catch (Exception ex) {
            Ui.error(view, ex);
        }
    }

    public void calcularActividades() {
        try {
            LocalDate desde = Ui.leerFecha(view.getDesdeActividades());
            LocalDate hasta = Ui.leerFecha(view.getHastaActividades());
            if (hasta.isBefore(desde)) throw new Exception("La fecha hasta debe ser posterior o igual a la fecha desde.");
            List<Model.Fila> filas = model.actividades(service.listarReservas(), desde, hasta);
            tablaActividades.setFilas(filas);
            view.getGraficoActividades().setDatos(etiquetas(filas), valores(filas));
        } catch (Exception ex) {
            Ui.error(view, ex);
        }
    }

    public void pdfRecursos() { pdf("recursos.pdf", "Recursos reservados", tablaRecursos); }
    public void pdfActividades() { pdf("actividades-estadistica.pdf", "Actividades por semana", tablaActividades); }

    private void pdf(String nombre, String titulo, TableModel tabla) {
        List<String> lineas = new ArrayList<>();
        lineas.add("Detalle | Cantidad");
        for (Model.Fila fila : tabla.getFilas()) lineas.add(fila.etiqueta + " | " + fila.cantidad);
        ReportePdf.guardarYAbrir(view, nombre, titulo, lineas);
    }

    private static List<String> etiquetas(List<Model.Fila> filas) {
        List<String> etiquetas = new ArrayList<>();
        for (Model.Fila fila : filas) etiquetas.add(fila.etiqueta);
        return etiquetas;
    }

    private static List<Integer> valores(List<Model.Fila> filas) {
        List<Integer> valores = new ArrayList<>();
        for (Model.Fila fila : filas) valores.add(fila.cantidad);
        return valores;
    }
}
