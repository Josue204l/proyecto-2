package backend.red;

import protocolo.MensajeRespuesta;
import protocolo.MensajeSolicitud;
import protocolo.EventoServidor;
import java.util.function.Consumer;

@FunctionalInterface
public interface ManejadorSolicitudes {
    MensajeRespuesta procesar(MensajeSolicitud solicitud, Consumer<EventoServidor> emisorEventos) throws Exception;
}
