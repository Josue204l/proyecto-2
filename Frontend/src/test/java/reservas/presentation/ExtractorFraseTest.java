package reservas.presentation;

import org.junit.jupiter.api.Test;
import reservas.logic.Categoria;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtractorFraseTest {
    @Test
    void extraeFechaHoraYCategoria() throws Exception {
        ExtractorFrase extractor = new ExtractorFrase(LocalDate.of(2026, 10, 29));
        ExtractorFrase.Datos datos = extractor.extraer(
                "Necesito la sala para 10 personas mañana de 10 a 12 para coordinacion",
                List.of(new Categoria("CAT-001", "Sala para 10 personas"),
                        new Categoria("CAT-002", "Laptop windows 11")));
        assertEquals(LocalDate.of(2026, 10, 30), datos.fecha);
        assertEquals(LocalTime.of(10, 0), datos.inicio);
        assertEquals(LocalTime.of(12, 0), datos.fin);
        assertEquals(1, datos.categorias.size());
        assertEquals("CAT-001", datos.categorias.get(0).getId());
        assertTrue(datos.titulo.toLowerCase().contains("coordinacion"));
    }
}
