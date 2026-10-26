package protocolo;

import java.io.Serializable;
import java.util.UUID;

public class Solicitud implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String operacion;
    private Object[] parametros;

    public Solicitud(String operacion, Object... parametros) {
        this.id          = UUID.randomUUID().toString();
        this.operacion   = operacion;
        this.parametros  = parametros;
    }

    public String   getId()          { return id; }
    public String   getOperacion()   { return operacion; }
    public Object[] getParametros()  { return parametros; }
}
