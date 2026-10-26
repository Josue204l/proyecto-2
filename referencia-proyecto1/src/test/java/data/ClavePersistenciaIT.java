package data;

import logic.Funcionario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ClavePersistenciaIT {

    @TempDir
    Path tempDir;

    @Test
    void cambiarClave_guardar_recargar_claveActualizada() throws Exception {
        String archivo = tempDir.resolve("clave-test.xml").toString();
        XmlPersister persister = new XmlPersister(archivo);

        // Crear y guardar data con un funcionario
        Data data = new Data();
        Funcionario f = new Funcionario("emp1", "claveInicial", "FUNCIONARIO", "Pedro Ruiz", "4444-0000");
        data.getFuncionarios().add(f);
        persister.store(data);

        // Simular cambio de clave (lógica de Service.cambiarClave)
        f.setClave("claveNueva");
        persister.store(data);

        // Recargar y verificar
        Data recargada = persister.load();
        assertNotNull(recargada);
        assertEquals(1, recargada.getFuncionarios().size());
        assertEquals("claveNueva", recargada.getFuncionarios().get(0).getClave());
    }

    @Test
    void claveInicial_igualAlId_segunReglaNegocio() {
        Funcionario f = new Funcionario("emp2", "emp2", "FUNCIONARIO", "Sofia Vega", "3333-0000");
        assertEquals(f.getId(), f.getClave());
    }
}
