package reservas.server;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import reservas.logic.Categoria;
import reservas.logic.Funcionario;
import reservas.logic.Reserva;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServiceTest {
    @TempDir
    Path carpeta;

    @Test
    void cambiarClaveUsaLaSesionYNoElIdDelCliente() throws Exception {
        Service service = nuevo();
        assertThrows(Exception.class, () -> service.cambiarClave("111", "otra", "otra"));
        service.enSesion("111", "FUNCIONARIO", () -> service.cambiarClave("111", "nueva", "nueva"));
        assertEquals("111", service.login("111", "nueva").getId());
        assertNull(service.login("111", "nueva").getClave());
        service.enSesion("admin", "ADMIN", () -> service.cambiarClave("222", "admin2", "admin2"));
        assertEquals("111", service.login("111", "nueva").getId());
        assertThrows(Exception.class, () -> service.login("111", "admin2"));
    }

    @Test
    void claveInicialEsElIdYNoSeListaLaClave() throws Exception {
        Service service = nuevo();
        service.enSesion("admin", "ADMIN", () -> service.guardarFuncionario(
                new Funcionario("ana", null, null, "Ana", "8888")));
        Funcionario ana = service.login("ana", "ana");
        assertEquals("FUNCIONARIO", ana.getRol());
        assertNull(ana.getClave());
        service.enSesion("admin", "ADMIN", () -> {
            for (Funcionario funcionario : service.listarFuncionarios()) assertNull(funcionario.getClave());
        });
    }

    @Test
    void reservaTomaElPrimerRecursoLibreYResuelveLasRelaciones() throws Exception {
        Service service = nuevo();
        LocalDate fecha = LocalDate.now().plusDays(2);
        service.enSesion("111", "FUNCIONARIO", () -> {
            Reserva primera = service.crearReserva("Clase", fecha, LocalTime.of(10, 0), LocalTime.of(12, 0),
                    List.of("CAT-001"));
            assertEquals("SALA-1", primera.getRecursosAsignados().get(0).getId());
            assertEquals("Sala 1 primer piso", primera.getRecursosAsignados().get(0).getDescripcion());
            assertEquals("Funcionario Test", primera.getSolicitante().getNombre());
            assertNull(primera.getSolicitante().getClave());
            Reserva segunda = service.crearReserva("Otra", fecha, LocalTime.of(10, 0), LocalTime.of(11, 0),
                    List.of("CAT-001"));
            assertEquals("SALA-2", segunda.getRecursosAsignados().get(0).getId());
        });
        service.enSesion("111", "FUNCIONARIO", () -> {
            Exception ex = assertThrows(Exception.class, () -> service.crearReserva(
                    "Tercera", fecha, LocalTime.of(10, 0), LocalTime.of(12, 0), List.of("CAT-001")));
            assertTrue(ex.getMessage().contains("Sin disponibilidad"));
            Reserva leida = service.listarReservas().stream()
                    .filter(r -> "Clase".equals(r.getTitulo())).findFirst().orElseThrow();
            assertEquals("CAT-001", leida.getCategoriasRequeridas().get(0).getId());
            assertEquals("Sala para 10 personas", leida.getCategoriasRequeridas().get(0).getDescripcion());
        });
    }

    @Test
    void funcionarioNoAdministraYLaCategoriaNuevaRecibeId() throws Exception {
        Service service = nuevo();
        assertThrows(Exception.class, () -> service.enSesion("111", "FUNCIONARIO",
                () -> service.listarFuncionarios()));
        service.enSesion("admin", "ADMIN", () -> service.guardarCategoria(new Categoria("", "Proyector")));
        service.enSesion("111", "FUNCIONARIO", () -> {
            boolean encontrada = service.listarCategorias().stream()
                    .anyMatch(c -> "CAT-003".equals(c.getId()) && "Proyector".equals(c.getDescripcion()));
            assertTrue(encontrada);
        });
    }

    private Service nuevo() throws Exception {
        Service service = new Service(carpeta.resolve("reservas.xml"));
        service.sembrarDemo();
        return service;
    }
}
