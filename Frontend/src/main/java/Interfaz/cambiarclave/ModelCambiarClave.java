package Interfaz.cambiarclave;

import frontend.red.ServicioActual;
import logic.Usuario;
import protocolo.IService;

public class ModelCambiarClave {

    private final Usuario usuario;
    private final IService servicio;

    public ModelCambiarClave(Usuario usuario, IService servicio) {
        this.usuario = usuario;
        this.servicio = servicio;
    }

    public void cambiar(String claveActual, String claveNueva, String claveConfirmar) throws Exception {
        servicio.cambiarClave(usuario.getId(), claveActual, claveNueva, claveConfirmar);
    }
}
