package reservas.logic;

import java.io.Serializable;

public class Recurso implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String descripcion;
    private Categoria categoria;

    public Recurso() { }

    public Recurso(String id, String descripcion, Categoria categoria) {
        this.id = id;
        this.descripcion = descripcion;
        this.categoria = categoria;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    @Override
    public String toString() {
        return descripcion == null || descripcion.isBlank() ? id : descripcion;
    }
}
