package protocolo;

import java.io.Serializable;

public class Notificacion implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String NUEVA_RESERVA = "NUEVA_RESERVA";

    private String tipo;
    private Object datos;

    public Notificacion(String tipo, Object datos) {
        this.tipo  = tipo;
        this.datos = datos;
    }

    public String getTipo()  { return tipo; }
    public Object getDatos() { return datos; }
}
