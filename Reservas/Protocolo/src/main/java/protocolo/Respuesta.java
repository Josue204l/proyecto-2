package protocolo;

import java.io.Serializable;

public class Respuesta implements Serializable {

    private static final long serialVersionUID = 1L;

    private String  idSolicitud;
    private boolean exitosa;
    private Object  datos;
    private String  error;

    // Respuesta exitosa con datos
    public static Respuesta exito(String idSolicitud, Object datos) {
        Respuesta r = new Respuesta();
        r.idSolicitud = idSolicitud;
        r.exitosa     = true;
        r.datos       = datos;
        return r;
    }

    // Respuesta de error
    public static Respuesta error(String idSolicitud, String mensaje) {
        Respuesta r = new Respuesta();
        r.idSolicitud = idSolicitud;
        r.exitosa     = false;
        r.error       = mensaje;
        return r;
    }

    public String  getIdSolicitud() { return idSolicitud; }
    public boolean isExitosa()      { return exitosa; }
    public Object  getDatos()       { return datos; }
    public String  getError()       { return error; }
}
