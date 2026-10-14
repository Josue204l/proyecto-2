package Interfaz.funcionarios;

import data.Data;
import data.XmlPersister;
import logic.Funcionario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioAltaIT {

    @TempDir
    Path tempDir;

    @Test
    void altaFuncionario_claveIgualAlId() throws Exception {
        String archivo = tempDir.resolve("func-alta.xml").toString();
        XmlPersister persister = new XmlPersister(archivo);

        Data data = new Data();

        // Regla de negocio: al agregar funcionario, clave = id
        String id = "emp99";
        Funcionario nuevo = new Funcionario(id, id, "FUNCIONARIO", "Rosa Campos", "2222-5555");
        data.getFuncionarios().add(nuevo);
        persister.store(data);

        Data recargada = persister.load();
        Funcionario cargado = recargada.getFuncionarios().get(0);

        assertEquals(id, cargado.getId());
        assertEquals(id, cargado.getClave(), "La clave inicial debe ser igual al id");
        assertEquals("Rosa Campos", cargado.getNombre());
        assertEquals("2222-5555", cargado.getTelefono());
    }

    @Test
    void altaFuncionario_rolEsFuncionario() {
        Funcionario f = new Funcionario("emp100", "emp100", "FUNCIONARIO", "Juan Torres", "1111-9999");
        assertEquals("FUNCIONARIO", f.getRol());
    }

    @Test
    void buscarFuncionario_porId_retornaCorrectamente() throws Exception {
        String archivo = tempDir.resolve("func-buscar.xml").toString();
        XmlPersister persister = new XmlPersister(archivo);

        Data data = new Data();
        data.getFuncionarios().add(new Funcionario("emp10", "emp10", "FUNCIONARIO", "Laura Diaz", "9999-1111"));
        data.getFuncionarios().add(new Funcionario("emp11", "emp11", "FUNCIONARIO", "Marco Rios", "8888-2222"));
        persister.store(data);

        Data recargada = persister.load();
        Funcionario encontrado = recargada.buscarFuncionario("emp11");

        assertNotNull(encontrado);
        assertEquals("Marco Rios", encontrado.getNombre());
    }
}
