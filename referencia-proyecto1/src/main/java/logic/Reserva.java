package logic;

import data.LocalDateAdapter;
import data.LocalTimeAdapter;
import data.XmlIdAdapter;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Reserva {

    @XmlID
    @XmlJavaTypeAdapter(XmlIdAdapter.class)
    private String id;
    private String titulo;
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fecha;
    @XmlJavaTypeAdapter(LocalTimeAdapter.class)
    private LocalTime horaInicio;
    @XmlJavaTypeAdapter(LocalTimeAdapter.class)
    private LocalTime horaFin;
    @XmlElementWrapper(name = "recursosAsignados")
    @XmlElement(name = "recurso")
    @XmlIDREF
    private List<Recurso> recursosAsignados;
    @XmlElementWrapper(name = "categoriasRequeridas")
    @XmlElement(name = "categoria")
    @XmlIDREF
    private List<Categoria> categoriasRequeridas;
    @XmlIDREF
    private Funcionario solicitante;
    private String estado; // "ACTIVA", "CANCELADA", etc.

    // Constructor por defecto para XML / Beans
    public Reserva() {
        this.recursosAsignados = new ArrayList<>();
        this.categoriasRequeridas = new ArrayList<>();
        this.estado = "ACTIVA";
    }

    // Constructor Principal (Listas de recursos y categorías)
    public Reserva(String id, String titulo, LocalDate fecha, LocalTime horaInicio,
                   LocalTime horaFin, List<Recurso> recursosAsignados,
                   List<Categoria> categoriasRequeridas, Funcionario solicitante) {
        this.id = id;
        this.titulo = titulo;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.recursosAsignados = copiarRecursos(recursosAsignados);
        this.categoriasRequeridas = copiarCategorias(categoriasRequeridas);
        this.solicitante = solicitante;
        this.estado = "ACTIVA";
    }

    // Constructor Sobrecargado (Solo lista de recursos)
    public Reserva(String id, String titulo, LocalDate fecha, LocalTime horaInicio,
                   LocalTime horaFin, List<Recurso> recursosAsignados, Funcionario solicitante) {
        this(id, titulo, fecha, horaInicio, horaFin, recursosAsignados, new ArrayList<>(), solicitante);
    }

    // Constructor Legacy / Compatibilidad (Un solo recurso y una sola categoría)
    public Reserva(String id, String titulo, LocalDate fecha, LocalTime horaInicio,
                   LocalTime horaFin, Recurso recurso, Categoria categoria, Funcionario solicitante) {
        this();
        this.id = id;
        this.titulo = titulo;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.solicitante = solicitante;
        if (recurso != null) this.recursosAsignados.add(recurso);
        if (categoria != null) this.categoriasRequeridas.add(categoria);
    }

    // Constructor Legacy / Compatibilidad (Un solo recurso sin categoría)
    public Reserva(String id, String titulo, LocalDate fecha, LocalTime horaInicio,
                   LocalTime horaFin, Recurso recurso, Funcionario solicitante) {
        this(id, titulo, fecha, horaInicio, horaFin, recurso, null, solicitante);
    }

    // Getters y Setters Básicos
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public List<Recurso> getRecursosAsignados() { return recursosAsignados; }
    public void setRecursosAsignados(List<Recurso> recursosAsignados) {
        this.recursosAsignados = copiarRecursos(recursosAsignados);
    }

    public List<Categoria> getCategoriasRequeridas() { return categoriasRequeridas; }
    public void setCategoriasRequeridas(List<Categoria> categoriasRequeridas) {
        this.categoriasRequeridas = copiarCategorias(categoriasRequeridas);
    }

    private static List<Recurso> copiarRecursos(List<Recurso> origen) {
        return origen != null ? new ArrayList<>(origen) : new ArrayList<>();
    }

    private static List<Categoria> copiarCategorias(List<Categoria> origen) {
        return origen != null ? new ArrayList<>(origen) : new ArrayList<>();
    }

    public Funcionario getSolicitante() { return solicitante; }
    public void setSolicitante(Funcionario solicitante) { this.solicitante = solicitante; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    // Helpers de compatibilidad
    public Recurso getRecurso() {
        return (recursosAsignados != null && !recursosAsignados.isEmpty()) ? recursosAsignados.get(0) : null;
    }

    public Categoria getCategoria() {
        return (categoriasRequeridas != null && !categoriasRequeridas.isEmpty()) ? categoriasRequeridas.get(0) : null;
    }

    public boolean seSolapaCon(Reserva otra) {
        if (otra == null || this.fecha == null || !this.fecha.equals(otra.getFecha())) {
            return false;
        }
        if (this.horaInicio == null || this.horaFin == null || otra.getHoraInicio() == null || otra.getHoraFin() == null) {
            return false;
        }
        return this.horaInicio.isBefore(otra.getHoraFin()) && otra.getHoraInicio().isBefore(this.horaFin);
    }

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
        return recursosAsignados.stream().anyMatch(r -> r != null && recursoId.equals(r.getId()));
    }

    public String etiquetaCelda() {
        String actividad = titulo != null ? titulo : "";
        String nombre = solicitante != null ? solicitante.getNombre() : "";
        if (actividad.isEmpty()) return nombre;
        if (nombre == null || nombre.isEmpty()) return actividad;
        return actividad + " / " + nombre;
    }
}
