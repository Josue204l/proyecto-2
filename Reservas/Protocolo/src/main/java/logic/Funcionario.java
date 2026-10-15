package logic;

import java.io.Serializable;

public class Funcionario extends Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private String telefono;
    private String puesto;

    public Funcionario() {}

    public Funcionario(String id, String clave, String rol, String nombre) {
        super(id, clave, rol);
        this.nombre   = nombre;
        this.telefono = "";
        this.puesto   = "";
    }

    public Funcionario(String id, String clave, String rol, String nombre, String telefono) {
        super(id, clave, rol);
        this.nombre   = nombre;
        this.telefono = telefono;
        this.puesto   = "";
    }

    public Funcionario(String id, String clave, String rol, String nombre, String telefono, String puesto) {
        super(id, clave, rol);
        this.nombre   = nombre;
        this.telefono = telefono;
        this.puesto   = puesto;
    }

    public String getNombre()              { return nombre; }
    public void   setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono()                { return telefono; }
    public void   setTelefono(String telefono) { this.telefono = telefono; }

    public String getPuesto()              { return puesto != null ? puesto : ""; }
    public void   setPuesto(String puesto) { this.puesto = puesto; }
}
