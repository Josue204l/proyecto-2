package logic;

import java.io.Serializable;

public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String id;
    protected String clave;
    protected String rol;

    public Usuario() {}

    public Usuario(String id, String clave, String rol) {
        this.id = id;
        this.clave = clave;
        this.rol = rol;
    }

    public String getId()            { return id; }
    public void   setId(String id)   { this.id = id; }

    public String getClave()             { return clave; }
    public void   setClave(String clave) { this.clave = clave; }

    public String getRol()           { return rol; }
    public void   setRol(String rol) { this.rol = rol; }
}
