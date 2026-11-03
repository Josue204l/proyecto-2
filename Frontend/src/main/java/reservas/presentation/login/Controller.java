package reservas.presentation.login;

import reservas.client.Service;
import reservas.logic.Funcionario;
import reservas.presentation.Ui;

public class Controller {
    private final View view;
    private final Service service;
    private Funcionario usuario;

    public Controller(View view, Service service) {
        this.view = view;
        this.service = service;
        view.setController(this);
    }

    public void login() {
        if (view.getId().isBlank() || view.getClave().isBlank()) {
            Ui.aviso(view, "Debe ingresar el id y la clave.");
            return;
        }
        try {
            usuario = service.login(view.getId(), view.getClave());
            view.dispose();
        } catch (Exception ex) {
            Ui.error(view, ex);
        }
    }

    public Funcionario getUsuario() { return usuario; }
}
