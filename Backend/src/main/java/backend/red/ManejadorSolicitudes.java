package backend.red;

import protocolo.Respuesta;
import protocolo.Solicitud;

@FunctionalInterface
public interface ManejadorSolicitudes {
    Respuesta procesar(Solicitud solicitud, WorkerCliente emisor) throws Exception;
}
