package data;

import logic.Categoria;
import logic.Funcionario;
import logic.Recurso;
import logic.Reserva;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XmlPersisterIT {

    @TempDir
    Path tempDir;

    private Data crearDataMinima() {
        Data d = new Data();
        Funcionario f = new Funcionario("u1", "pass1", "FUNCIONARIO", "Luis Perez", "5555-0000");
        d.getFuncionarios().add(f);

        Categoria cat = new Categoria("CAT-1", "Sala", "Sala para 10 personas");
        d.getCategorias().add(cat);

        Recurso rec = new Recurso("SALA-1", "Sala 1 primer piso", cat);
        d.getRecursos().add(rec);

        Reserva res = new Reserva("RES-1", "Capacitacion",
                LocalDate.of(2025, 9, 10), LocalTime.of(9, 0), LocalTime.of(11, 0),
                List.of(rec), List.of(cat), f);
        d.getReservas().add(res);

        return d;
    }

    @Test
    void guardarYCargar_conservaDatos() throws Exception {
        String archivo = tempDir.resolve("test-data.xml").toString();
        XmlPersister persister = new XmlPersister(archivo);

        Data original = crearDataMinima();
        persister.store(original);

        Data cargada = persister.load();

        assertNotNull(cargada);
        assertEquals(1, cargada.getFuncionarios().size());
        assertEquals("u1", cargada.getFuncionarios().get(0).getId());
        assertEquals(1, cargada.getCategorias().size());
        assertEquals("CAT-1", cargada.getCategorias().get(0).getId());
        assertEquals(1, cargada.getRecursos().size());
        assertEquals("SALA-1", cargada.getRecursos().get(0).getId());
        assertEquals(1, cargada.getReservas().size());
        assertEquals("RES-1", cargada.getReservas().get(0).getId());
    }

    @Test
    void cargar_archivoInexistente_lanzaExcepcion() {
        XmlPersister persister = new XmlPersister(tempDir.resolve("noexiste.xml").toString());
        assertThrows(Exception.class, persister::load);
    }

    @Test
    void guardar_dataVacia_cargaListasVacias() throws Exception {
        String archivo = tempDir.resolve("vacio.xml").toString();
        XmlPersister persister = new XmlPersister(archivo);

        persister.store(new Data());
        Data cargada = persister.load();

        assertNotNull(cargada);
        assertTrue(cargada.getFuncionarios() == null || cargada.getFuncionarios().isEmpty());
    }
}
