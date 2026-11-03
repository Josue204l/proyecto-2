package reservas.server;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidadorRedTest {
    @Test
    void rechazaTipoDistintoConMensajeClaro() {
        Exception ex = assertThrows(Exception.class, () -> ValidadorRed.fecha("2026-11-01", "fecha"));
        assertTrue(ex.getMessage().contains("fecha"));
        assertTrue(ex.getMessage().contains("Dato de red inválido"));
    }

    @Test
    void aceptaListaDeIdYRechazaElementosVacios() throws Exception {
        assertEquals(List.of("CAT-001"), ValidadorRed.ids(List.of("CAT-001"), "categorías"));
        assertThrows(Exception.class, () -> ValidadorRed.ids(List.of("CAT-001", " "), "categorías"));
        assertEquals(LocalDate.of(2026, 11, 1), ValidadorRed.fecha(LocalDate.of(2026, 11, 1), "fecha"));
    }
}
