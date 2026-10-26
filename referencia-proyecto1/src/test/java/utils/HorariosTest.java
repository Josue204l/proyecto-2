package utils;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HorariosTest {

    @Test
    void horasDelDia_iniciaEn07() {
        List<LocalTime> horas = Horarios.horasDelDia();
        assertFalse(horas.isEmpty());
        assertEquals(LocalTime.of(7, 0), horas.get(0));
    }

    @Test
    void horasDelDia_terminaEn21() {
        List<LocalTime> horas = Horarios.horasDelDia();
        assertEquals(LocalTime.of(21, 0), horas.get(horas.size() - 1));
    }

    @Test
    void horasDelDia_cantidadCorrecta() {
        // De 07:00 a 21:00 inclusive = 15 horas
        List<LocalTime> horas = Horarios.horasDelDia();
        assertEquals(15, horas.size());
    }

    @Test
    void horasDelDia_todasEnPuntoExacto() {
        for (LocalTime h : Horarios.horasDelDia()) {
            assertEquals(0, h.getMinute());
        }
    }

    @Test
    void constantes_correctas() {
        assertEquals(7, Horarios.HORA_INICIO);
        assertEquals(21, Horarios.HORA_FIN);
    }
}
