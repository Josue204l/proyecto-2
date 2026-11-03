package reservas.presentation.clave;

import reservas.client.Service;
import reservas.presentation.Ui;

public class Controller {
    private final View view;
    private final Service service;

    public Controller(View view, Service service) {
        this.view = view;
        this.service = service;
        view.setController(this);
    }

    public void guardar() {
        try {
            service.cambiarClave(view.getActual(), view.getNueva(), view.getConfirmar());
            Ui.aviso(view, "Clave actualizada.");
            view.dispose();
        } catch (Exception ex) {
            Ui.error(view, ex);
        }
    }
}
