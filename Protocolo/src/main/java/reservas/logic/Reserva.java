package reservas.logic;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Reserva implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String titulo;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private List<Recurso> recursosAsignados = new ArrayList<>();
    private List<Categoria> categoriasRequeridas = new ArrayList<>();
    private Funcionario solicitante;
    private String estado = "ACTIVA";

    public Reserva() { }

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
    public void setRecursosAsignados(List<Recurso> recursos) {
        this.recursosAsignados = recursos == null ? new ArrayList<>() : new ArrayList<>(recursos);
    }

    public List<Categoria> getCategoriasRequeridas() { return categoriasRequeridas; }
    public void setCategoriasRequeridas(List<Categoria> categorias) {
        this.categoriasRequeridas = categorias == null ? new ArrayList<>() : new ArrayList<>(categorias);
    }

    public Funcionario getSolicitante() { return solicitante; }
    public void setSolicitante(Funcionario solicitante) { this.solicitante = solicitante; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public boolean isActiva() {
        return estado == null || estado.isBlank() || "ACTIVA".equalsIgnoreCase(estado);
    }

    public boolean esFutura() {
        if (fecha == null || horaInicio == null) return false;
        return LocalDateTime.of(fecha, horaInicio).isAfter(LocalDateTime.now());
    }

    public boolean cubreHora(LocalTime hora) {
        if (hora == null || horaInicio == null || horaFin == null) return false;
        return !hora.isBefore(horaInicio) && hora.isBefore(horaFin);
    }

    public boolean usaRecurso(String recursoId) {
        if (recursoId == null) return false;
        for (Recurso recurso : recursosAsignados) {
            if (recurso != null && recursoId.equals(recurso.getId())) return true;
        }
        return false;
    }

    public boolean seSolapaCon(Reserva otra) {
        if (otra == null || fecha == null || !fecha.equals(otra.fecha)) return false;
        if (horaInicio == null || horaFin == null || otra.horaInicio == null || otra.horaFin == null) return false;
        return horaInicio.isBefore(otra.horaFin) && otra.horaInicio.isBefore(horaFin);
    }

    public String etiquetaCelda() {
        String actividad = titulo == null ? "" : titulo;
        String nombre = solicitante == null || solicitante.getNombre() == null ? "" : solicitante.getNombre();
        if (actividad.isEmpty()) return nombre;
        if (nombre.isEmpty()) return actividad;
        return actividad + " / " + nombre;
    }
}
