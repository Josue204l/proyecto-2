package frontend.red;

import backend.red.ServidorTCP;
import protocolo.Notificacion;
import protocolo.Respuesta;
import protocolo.Solicitud;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SocketIntegrationIT {
    @Test
    void dosClientesIntercambianRespuestaYNotificacionAsincrona() throws Exception {
        ServidorTCP servidor = new ServidorTCP(0);
        servidor.setManejador((solicitud, emisor) -> {
            servidor.publicarATodos(new Notificacion(Notificacion.NUEVA_RESERVA, "reserva-demo"), emisor);
            return Respuesta.exito(solicitud.getId(), "recibido");
        });
        Thread hiloServidor = new Thread(() -> {
            try { servidor.iniciar(); } catch (Exception ignored) { }
        }, "servidor-prueba-integracion");
        hiloServidor.setDaemon(true);
        hiloServidor.start();

        ClienteSocket primero = null;
        ClienteSocket segundo = null;
        try {
            int puerto = esperarPuerto(servidor);
            primero = new ClienteSocket("127.0.0.1", puerto);
            segundo = new ClienteSocket("127.0.0.1", puerto);
            ArrayBlockingQueue<Notificacion> notificaciones = new ArrayBlockingQueue<>(1);
            segundo.setReceptorNotificaciones(notificaciones::offer);

            Respuesta respuesta = primero.enviar(new Solicitud("PING"));
            Notificacion notificacion = notificaciones.poll(3, TimeUnit.SECONDS);

            assertEquals("recibido", respuesta.getDatos());
            assertNotNull(notificacion);
            assertEquals(Notificacion.NUEVA_RESERVA, notificacion.getTipo());
            assertEquals("reserva-demo", notificacion.getDatos());
        } finally {
            if (primero != null) primero.cerrar();
            if (segundo != null) segundo.cerrar();
            servidor.detener();
        }
    }

    private static int esperarPuerto(ServidorTCP servidor) throws InterruptedException {
        long limite = System.nanoTime() + Duration.ofSeconds(5).toNanos();
        while (servidor.getPuertoActual() == 0 && System.nanoTime() < limite) Thread.sleep(10);
        return servidor.getPuertoActual();
    }
}
