package reservas.logic;

import java.io.Serializable;

public class Categoria implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String descripcion;

    public Categoria() { }

    public Categoria(String id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEtiqueta() {
        if (descripcion != null && !descripcion.isBlank()) return descripcion;
        return id == null ? "" : id;
    }

    @Override
    public String toString() { return getEtiqueta(); }
}
