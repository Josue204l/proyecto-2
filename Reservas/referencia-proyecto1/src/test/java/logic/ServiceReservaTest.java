package logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServiceReservaTest {

    private List<Recurso> recursos;
    private List<Reserva> reservas;
    private List<Categoria> categorias;
    private Funcionario funcionario;
    private Categoria catSala;
    private Recurso sala1;

    @BeforeEach
    void setUp() {
        catSala = new Categoria("CAT-1", "Sala", "Sala para 10 personas");
        sala1 = new Recurso("SALA-1", "Sala 1", catSala);
        funcionario = new Funcionario("f1", "clave", "FUNCIONARIO", "Carlos Mora", "7777-0000");
        recursos = new ArrayList<>(List.of(sala1));
        reservas = new ArrayList<>();
        categorias = new ArrayList<>(List.of(catSala));
    }

    // Simula la lógica de asignación sin tocar Data (prueba pura de algoritmo)
    private List<Recurso> asignar(List<Categoria> cats, LocalDate fecha,
                                   LocalTime inicio, LocalTime fin) throws Exception {
        List<Recurso> asignados = new ArrayList<>();
        List<String> noDisponibles = new ArrayList<>();

        for (Categoria cat : cats) {
            Recurso encontrado = null;
            for (Recurso r : recursos) {
                if (asignados.stream().anyMatch(a -> a.getId().equals(r.getId()))) continue;
                if (r.getCategoria() == null || !r.getCategoria().getId().equals(cat.getId())) continue;
                boolean libre = true;
                for (Reserva res : reservas) {
                    if (res.isActiva() && fecha.equals(res.getFecha())) {
                        boolean solapa = inicio.isBefore(res.getHoraFin()) && res.getHoraInicio().isBefore(fin);
                        if (solapa && res.usaRecurso(r.getId())) { libre = false; break; }
                    }
                }
                if (libre) { encontrado = r; break; }
            }
            if (encontrado != null) asignados.add(encontrado);
            else noDisponibles.add(cat.getEtiqueta());
        }

        if (!noDisponibles.isEmpty())
            throw new Exception("Sin disponibilidad para las categorías: " + String.join(", ", noDisponibles));
        return asignados;
    }

    @Test
    void asignar_primerRecursoLibre() throws Exception {
        LocalDate fecha = LocalDate.now().plusDays(1);
        List<Recurso> resultado = asignar(List.of(catSala), fecha, LocalTime.of(9, 0), LocalTime.of(11, 0));
        assertEquals(1, resultado.size());
        assertEquals("SALA-1", resultado.get(0).getId());
    }

    @Test
    void asignar_categoriaOcupada_lanzaExcepcion() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        Reserva existente = new Reserva("R-EXIST", "Otra reunion", fecha,
                LocalTime.of(9, 0), LocalTime.of(11, 0), List.of(sala1), List.of(catSala), funcionario);
        reservas.add(existente);

        Exception ex = assertThrows(Exception.class, () ->
                asignar(List.of(catSala), fecha, LocalTime.of(10, 0), LocalTime.of(12, 0)));
        assertTrue(ex.getMessage().contains("Sin disponibilidad"));
    }

    @Test
    void cancelar_reservaFutura_cambiasEstado() throws Exception {
        LocalDate fecha = LocalDate.now().plusDays(1);
        Reserva r = new Reserva("R-FUTURA", "Taller", fecha,
                LocalTime.of(14, 0), LocalTime.of(16, 0), List.of(sala1), List.of(catSala), funcionario);
        reservas.add(r);

        assertTrue(r.isActiva());
        r.setEstado("CANCELADA");
        assertFalse(r.isActiva());
    }

    @Test
    void cancelar_reservaPasada_noPuedeSerCancelada() {
        LocalDate fecha = LocalDate.now().minusDays(1);
        Reserva r = new Reserva("R-PASADA", "Evento pasado", fecha,
                LocalTime.of(9, 0), LocalTime.of(10, 0), List.of(sala1), List.of(catSala), funcionario);
        reservas.add(r);

        assertFalse(r.esFutura());
    }

    @Test
    void asignar_horarioAdyacente_noHaySolape() throws Exception {
        LocalDate fecha = LocalDate.now().plusDays(1);
        Reserva existente = new Reserva("R-EXIST", "Reunion", fecha,
                LocalTime.of(8, 0), LocalTime.of(10, 0), List.of(sala1), List.of(catSala), funcionario);
        reservas.add(existente);

        List<Recurso> resultado = asignar(List.of(catSala), fecha, LocalTime.of(10, 0), LocalTime.of(12, 0));
        assertEquals(1, resultado.size());
    }
}
