package reservas.presentation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import reservas.client.Service;
import reservas.logic.Protocol;
import reservas.logic.Reserva;
import reservas.server.Server;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RedIT {
    @TempDir
    Path carpeta;

    @Test
    void claveSinSesionFallaYLaReservaAvisaAlOtroCliente() throws Exception {
        Server server = new Server(0, carpeta.resolve("reservas.xml"));
        Thread hilo = new Thread(new Runnable() {
            public void run() {
                server.run();
            }
        });
        hilo.setDaemon(true);
        hilo.start();

        Service funcionario = null;
        Service administrador = null;
        SocketListener avisos = null;
        List<Reserva> recibidas = new ArrayList<>();
        try {
            funcionario = new Service("localhost", server.getPuerto());
            Service anonimo = new Service("localhost", server.getPuerto());
            try {
                Exception sinSesion = assertThrows(Exception.class,
                        () -> anonimo.cambiarClave("111", "otra", "otra"));
                assertTrue(sinSesion.getMessage().contains("iniciar sesión"));
            } finally {
                cerrar(anonimo);
            }

            funcionario.login("111", "111");
            administrador = new Service("localhost", server.getPuerto());
            administrador.login("admin", "222");
            avisos = new SocketListener(new ThreadListener() {
                public void reservaCreada(Reserva reserva) {
                    synchronized (recibidas) {
                        recibidas.add(reserva);
                        recibidas.notifyAll();
                    }
                }
            }, administrador.getSid(), "localhost", server.getPuerto());
            avisos.start();

            Reserva creada = funcionario.crearReserva("Integracion", LocalDate.now().plusDays(3),
                    LocalTime.of(8, 0), LocalTime.of(9, 0), List.of("CAT-002"));
            synchronized (recibidas) {
                long limite = System.currentTimeMillis() + 5000;
                while (recibidas.isEmpty() && System.currentTimeMillis() < limite) {
                    recibidas.wait(200);
                }
            }
            assertEquals(1, recibidas.size());
            assertEquals(creada.getId(), recibidas.get(0).getId());
            assertEquals("Integracion", recibidas.get(0).getTitulo());
        } finally {
            if (avisos != null) avisos.detener();
            cerrar(funcionario);
            cerrar(administrador);
            server.detener();
        }
    }

    private static void cerrar(Service servicio) {
        if (servicio == null) return;
        try {
            servicio.desconectar();
        } catch (Exception ignored) { }
    }
}
