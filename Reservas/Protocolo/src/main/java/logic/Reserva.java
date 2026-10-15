package logic;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Reserva implements Serializable {

    private static final long serialVersionUID = 1L;

    private String         id;
    private String         titulo;
    private LocalDate      fecha;
    private LocalTime      horaInicio;
    private LocalTime      horaFin;
    private List<Recurso>  recursosAsignados;
    private List<Categoria> categoriasRequeridas;
    private Funcionario    solicitante;
    private String         estado;

    public Reserva() {
        this.recursosAsignados   = new ArrayList<>();
        this.categoriasRequeridas = new ArrayList<>();
        this.estado = "ACTIVA";
    }

    public Reserva(String id, String titulo, LocalDate fecha, LocalTime horaInicio,
                   LocalTime horaFin, List<Recurso> recursos, List<Categoria> categorias,
                   Funcionario solicitante) {
        this.id                   = id;
        this.titulo               = titulo;
        this.fecha                = fecha;
        this.horaInicio           = horaInicio;
        this.horaFin              = horaFin;
        this.recursosAsignados    = recursos   != null ? new ArrayList<>(recursos)   : new ArrayList<>();
        this.categoriasRequeridas = categorias != null ? new ArrayList<>(categorias) : new ArrayList<>();
        this.solicitante          = solicitante;
        this.estado               = "ACTIVA";
    }

    // Constructor compatibilidad proyecto 1 (un solo recurso)
    public Reserva(String id, String titulo, LocalDate fecha, LocalTime horaInicio,
                   LocalTime horaFin, List<Recurso> recursos, Funcionario solicitante) {
        this(id, titulo, fecha, horaInicio, horaFin, recursos, new ArrayList<>(), solicitante);
    }

    // Constructor compatibilidad proyecto 1 (recurso + categoria únicos)
    public Reserva(String id, String titulo, LocalDate fecha, LocalTime horaInicio,
                   LocalTime horaFin, Recurso recurso, Categoria categoria, Funcionario solicitante) {
        this();
        this.id          = id;
        this.titulo      = titulo;
        this.fecha       = fecha;
        this.horaInicio  = horaInicio;
        this.horaFin     = horaFin;
        this.solicitante = solicitante;
        if (recurso   != null) this.recursosAsignados.add(recurso);
        if (categoria != null) this.categoriasRequeridas.add(categoria);
    }

    public String getId()          { return id; }
    public void   setId(String id) { this.id = id; }

    public String getTitulo()              { return titulo; }
    public void   setTitulo(String titulo) { this.titulo = titulo; }

    public LocalDate getFecha()              { return fecha; }
    public void      setFecha(LocalDate f)   { this.fecha = f; }

    public LocalTime getHoraInicio()             { return horaInicio; }
    public void      setHoraInicio(LocalTime t)  { this.horaInicio = t; }

    public LocalTime getHoraFin()            { return horaFin; }
    public void      setHoraFin(LocalTime t) { this.horaFin = t; }

    public List<Recurso>   getRecursosAsignados()                        { return recursosAsignados; }
    public void            setRecursosAsignados(List<Recurso> lista)     { this.recursosAsignados = lista != null ? new ArrayList<>(lista) : new ArrayList<>(); }

    public List<Categoria> getCategoriasRequeridas()                     { return categoriasRequeridas; }
    public void            setCategoriasRequeridas(List<Categoria> lista){ this.categoriasRequeridas = lista != null ? new ArrayList<>(lista) : new ArrayList<>(); }

    public Funcionario getSolicitante()                    { return solicitante; }
    public void        setSolicitante(Funcionario f)       { this.solicitante = f; }

    public String getEstado()              { return estado; }
    public void   setEstado(String estado) { this.estado = estado; }

    // Helpers compatibilidad
    public Recurso   getRecurso()   { return (recursosAsignados   != null && !recursosAsignados.isEmpty())   ? recursosAsignados.get(0)   : null; }
    public Categoria getCategoria() { return (categoriasRequeridas != null && !categoriasRequeridas.isEmpty()) ? categoriasRequeridas.get(0) : null; }

    public boolean isActiva() {
        return estado == null || estado.isBlank() || "ACTIVA".equalsIgnoreCase(estado);
    }

    public boolean esFutura() {
        if (fecha == null || horaInicio == null) return false;
        return java.time.LocalDateTime.of(fecha, horaInicio).isAfter(java.time.LocalDateTime.now());
    }

    public boolean cubreHora(LocalTime hora) {
        if (hora == null || horaInicio == null || horaFin == null) return false;
        return !hora.isBefore(horaInicio) && hora.isBefore(horaFin);
    }

    public boolean usaRecurso(String recursoId) {
        if (recursoId == null || recursosAsignados == null) return false;
        for (Recurso r : recursosAsignados) {
            if (r != null && recursoId.equals(r.getId())) return true;
        }
        return false;
    }

    public boolean seSolapaCon(Reserva otra) {
        if (otra == null || this.fecha == null || !this.fecha.equals(otra.getFecha())) return false;
        if (this.horaInicio == null || this.horaFin == null) return false;
        return this.horaInicio.isBefore(otra.getHoraFin()) && otra.getHoraInicio().isBefore(this.horaFin);
    }

    public String etiquetaCelda() {
        String act    = titulo      != null ? titulo                  : "";
        String nombre = solicitante != null ? solicitante.getNombre() : "";
        if (act.isEmpty())    return nombre;
        if (nombre.isEmpty()) return act;
        return act + " / " + nombre;
    }
}
