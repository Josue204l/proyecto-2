package reservas.presentation.funcionarios;

import reservas.client.Service;
import reservas.logic.Funcionario;
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
            model.setTodos(service.listarFuncionarios());
        } catch (Exception ex) {
            Ui.error(view, ex);
        }
    }

    public void buscar() {
        model.buscar(view.getBuscarId(), view.getBuscarNombre());
    }

    public void guardar() {
        try {
            service.guardarFuncionario(new Funcionario(view.getId(), null, null, view.getNombre(), view.getTelefono()));
            refrescar();
            view.limpiar();
            Ui.aviso(view, "Funcionario guardado. Si es nuevo, la clave inicial es el id.");
        } catch (Exception ex) {
            Ui.error(view, ex);
        }
    }

    public void eliminar() {
        String id = view.getId();
        if (id.isBlank()) {
            Ui.aviso(view, "Seleccione un funcionario.");
            return;
        }
        if (JOptionPane.showConfirmDialog(view, "¿Borrar el funcionario " + id + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            service.eliminarFuncionario(id);
            refrescar();
            view.limpiar();
        } catch (Exception ex) {
            Ui.error(view, ex);
        }
    }

    public void seleccionar(int fila) {
        Funcionario funcionario = model.getTabla().fila(fila);
        if (funcionario != null) view.mostrar(funcionario);
    }

    public void pdf() {
        List<String> lineas = new ArrayList<>();
        lineas.add("Id | Nombre | Teléfono | Rol");
        for (Funcionario funcionario : model.getTabla().getFilas()) {
            lineas.add(funcionario.getId() + " | " + funcionario.getNombre() + " | "
                    + funcionario.getTelefono() + " | " + funcionario.getRol());
        }
        ReportePdf.guardarYAbrir(view, "funcionarios.pdf", "Listado de funcionarios", lineas);
    }
}
