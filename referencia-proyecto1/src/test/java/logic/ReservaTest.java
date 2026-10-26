package logic;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservaTest {

    private Funcionario funcionario() {
        return new Funcionario("f1", "clave", "FUNCIONARIO", "Ana Lopez", "8888-0000");
    }

    private Reserva reservaBase(LocalDate fecha, LocalTime inicio, LocalTime fin) {
        return new Reserva("R1", "Reunion", fecha, inicio, fin, List.of(), List.of(), funcionario());
    }

    @Test
    void reservaFutura_esActiva() {
        Reserva r = reservaBase(LocalDate.now().plusDays(1), LocalTime.of(9, 0), LocalTime.of(10, 0));
        assertTrue(r.isActiva());
        assertTrue(r.esFutura());
    }

    @Test
    void reservaCancelada_noEsActiva() {
        Reserva r = reservaBase(LocalDate.now().plusDays(1), LocalTime.of(9, 0), LocalTime.of(10, 0));
        r.setEstado("CANCELADA");
        assertFalse(r.isActiva());
    }

    @Test
    void reservaPasada_noEsFutura() {
        Reserva r = reservaBase(LocalDate.now().minusDays(1), LocalTime.of(9, 0), LocalTime.of(10, 0));
        assertFalse(r.esFutura());
    }

    @Test
    void solapamiento_mismaFechaHorasSuperpuestas() {
        LocalDate fecha = LocalDate.now().plusDays(2);
        Reserva r1 = reservaBase(fecha, LocalTime.of(9, 0), LocalTime.of(11, 0));
        Reserva r2 = reservaBase(fecha, LocalTime.of(10, 0), LocalTime.of(12, 0));
        assertTrue(r1.seSolapaCon(r2));
    }

    @Test
    void sinSolapamiento_horasConsecutivas() {
        LocalDate fecha = LocalDate.now().plusDays(2);
        Reserva r1 = reservaBase(fecha, LocalTime.of(8, 0), LocalTime.of(10, 0));
        Reserva r2 = reservaBase(fecha, LocalTime.of(10, 0), LocalTime.of(12, 0));
        assertFalse(r1.seSolapaCon(r2));
    }

    @Test
    void sinSolapamiento_fechasDiferentes() {
        Reserva r1 = reservaBase(LocalDate.now().plusDays(1), LocalTime.of(9, 0), LocalTime.of(11, 0));
        Reserva r2 = reservaBase(LocalDate.now().plusDays(2), LocalTime.of(9, 0), LocalTime.of(11, 0));
        assertFalse(r1.seSolapaCon(r2));
    }

    @Test
    void cubreHora_dentroDelRango() {
        Reserva r = reservaBase(LocalDate.now().plusDays(1), LocalTime.of(9, 0), LocalTime.of(11, 0));
        assertTrue(r.cubreHora(LocalTime.of(10, 0)));
        assertFalse(r.cubreHora(LocalTime.of(11, 0)));
        assertFalse(r.cubreHora(LocalTime.of(8, 0)));
    }

    @Test
    void etiquetaCelda_conTituloYNombre() {
        Reserva r = reservaBase(LocalDate.now().plusDays(1), LocalTime.of(9, 0), LocalTime.of(10, 0));
        assertEquals("Reunion / Ana Lopez", r.etiquetaCelda());
    }
}
