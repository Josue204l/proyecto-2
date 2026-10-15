package logic;

import java.io.Serializable;

public class Categoria implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
    private String descripcion;

    public Categoria() {}

    public Categoria(String id, String nombre, String descripcion) {
        this.id          = id;
        this.nombre      = nombre;
        this.descripcion = descripcion;
    }

    public String getId()          { return id; }
    public void   setId(String id) { this.id = id; }

    public String getNombre()              { return nombre; }
    public void   setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion()                   { return descripcion; }
    public void   setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEtiqueta() {
        if (descripcion != null && !descripcion.isBlank()) return descripcion;
        if (nombre      != null && !nombre.isBlank())      return nombre;
        return id != null ? id : "";
    }

    @Override
    public String toString() { return getEtiqueta(); }
}
