package Interfaz.cambiarclave;

import logic.Service;
import logic.Usuario;

public class ModelCambiarClave {

    private final Usuario usuario;

    public ModelCambiarClave(Usuario usuario) {
        this.usuario = usuario;
    }

    public void cambiar(String claveActual, String claveNueva, String claveConfirmar) throws Exception {
        Service.instance().cambiarClave(usuario, claveActual, claveNueva, claveConfirmar);
    }
}
