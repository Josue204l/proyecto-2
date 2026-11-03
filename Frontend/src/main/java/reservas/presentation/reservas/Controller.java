package reservas.presentation.reservas;

import reservas.client.Service;
import reservas.logic.Categoria;
import reservas.logic.Funcionario;
import reservas.logic.Reserva;
import reservas.presentation.ExtractorFrase;
import reservas.presentation.ReportePdf;
import reservas.presentation.Ui;

import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private final View view;
    private final Model model;
    private final Service service;
    private final Funcionario funcionario;

    public Controller(View view, Model model, Service service, Funcionario funcionario) {
        this.view = view;
        this.model = model;
        this.service = service;
        this.funcionario = funcionario;
        view.setController(this);
        view.getTabla().setModel(model.getTabla());
        refrescar();
    }

    public void refrescar() {
        try {
            view.setCategorias(service.listarCategorias());
            model.mostrar(model.propias(service.listarReservas(), funcionario.getId()));
        } catch (Exception ex) {
            Ui.error(view, ex);
        }
    }

    public void guardar() {
        try {
            List<String> ids = new ArrayList<>();
            for (Categoria categoria : view.getCategoriasSeleccionadas()) ids.add(categoria.getId());
            service.crearReserva(view.getTitulo(), Ui.leerFecha(view.getFecha()),
                    view.getInicio(), view.getFin(), ids);
            refrescar();
            view.limpiar();
            Ui.aviso(view, "Reserva registrada.");
        } catch (Exception ex) {
            Ui.error(view, ex);
        }
    }

    public void cancelar() {
        Reserva reserva = model.getTabla().fila(view.getTabla().getSelectedRow());
        if (reserva == null) {
            Ui.aviso(view, "Seleccione una reserva.");
            return;
        }
        if (JOptionPane.showConfirmDialog(view, "¿Cancelar la reserva " + reserva.getTitulo() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            service.cancelarReserva(reserva.getId());
            refrescar();
        } catch (Exception ex) {
            Ui.error(view, ex);
        }
    }

    public void extraer() {
        try {
            ExtractorFrase.Datos datos = new ExtractorFrase(LocalDate.now())
                    .extraer(view.getFrase(), view.getCategorias());
            view.aplicarExtraccion(datos);
            Ui.aviso(view, "Revise los datos extraídos antes de registrar la reserva.");
        } catch (Exception ex) {
            Ui.error(view, ex);
        }
    }

    public void pdf() {
        List<String> lineas = new ArrayList<>();
        lineas.add("Actividad | Fecha | Inicio | Fin | Estado");
        for (Reserva reserva : model.getTabla().getFilas()) {
            lineas.add(reserva.getTitulo() + " | " + reserva.getFecha() + " | "
                    + reserva.getHoraInicio() + " | " + reserva.getHoraFin() + " | " + reserva.getEstado());
        }
        ReportePdf.guardarYAbrir(view, "reservas.pdf", "Mis reservas", lineas);
    }
}
