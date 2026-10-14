package logic;

import data.XmlIdAdapter;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Categoria {
    @XmlID
    @XmlJavaTypeAdapter(XmlIdAdapter.class)
    private String id;
    private String nombre;
    private String descripcion;

    public Categoria() {}

    public Categoria(String id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEtiqueta() {
        if (descripcion != null && !descripcion.isBlank()) return descripcion;
        if (nombre != null && !nombre.isBlank()) return nombre;
        return id != null ? id : "";
    }

    @Override
    public String toString() {
        return getEtiqueta();
    }
}
