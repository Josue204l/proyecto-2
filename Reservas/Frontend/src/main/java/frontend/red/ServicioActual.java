package frontend.red;

import protocolo.IService;

/** Proveedor de la única conexión de servicio usada por la capa Swing. */
public final class ServicioActual {
    private static volatile IService servicio;

    private ServicioActual() { }

    public static void establecer(IService nuevoServicio) {
        servicio = nuevoServicio;
    }

    public static IService obtener() {
        IService actual = servicio;
        if (actual == null) throw new IllegalStateException("El servicio remoto no está conectado.");
        return actual;
    }
}
