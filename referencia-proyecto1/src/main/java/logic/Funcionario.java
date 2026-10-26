package logic;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Funcionario extends Usuario {
    private String nombre;
    private String telefono;
    private String puesto;

    public Funcionario() {}

    // Constructor de 4 parámetros
    public Funcionario(String id, String clave, String rol, String nombre) {
        super(id, clave, rol);
        this.nombre = nombre;
        this.telefono = "";
        this.puesto = "";
    }

    // Constructor de 5 parámetros (el que usa Data.java)
    public Funcionario(String id, String clave, String rol, String nombre, String telefono) {
        super(id, clave, rol);
        this.nombre = nombre;
        this.telefono = telefono;
        this.puesto = "";
    }

    // Constructor de 6 parámetros (completo)
    public Funcionario(String id, String clave, String rol, String nombre, String telefono, String puesto) {
        super(id, clave, rol);
        this.nombre = nombre;
        this.telefono = telefono;
        this.puesto = puesto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPuesto() {
        return puesto != null ? puesto : "";
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }
}
