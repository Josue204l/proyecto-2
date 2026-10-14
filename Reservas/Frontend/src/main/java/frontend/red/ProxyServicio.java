package frontend.red;

import com.fasterxml.jackson.databind.JsonNode;
import protocolo.EventoServidor;
import protocolo.JsonProtocolo;
import protocolo.MensajeRespuesta;

import java.io.IOException;
import java.util.function.Consumer;

/** Proxy local que presenta el servicio remoto a la capa MVC del Frontend. */
public final class ProxyServicio implements AutoCloseable {
    private final ClienteSocket cliente;

    public ProxyServicio(String host, int puerto) throws IOException {
        cliente = new ClienteSocket(host, puerto);
    }

    public void onAlerta(Consumer<EventoServidor> receptor) {
        cliente.onEvento(receptor);
    }

    public <T> T ejecutar(String operacion, Object datos, Class<T> tipoRespuesta) throws Exception {
        JsonNode solicitud = datos == null ? null : JsonProtocolo.mapper().valueToTree(datos);
        MensajeRespuesta respuesta = cliente.solicitar(operacion, solicitud);
        if (tipoRespuesta == Void.class) return null;
        return JsonProtocolo.mapper().treeToValue(respuesta.datos(), tipoRespuesta);
    }

    public static String hostConfigurado() {
        String host = System.getenv("BACKEND_HOST");
        return host == null || host.isBlank() ? "localhost" : host;
    }

    public static int puertoConfigurado() {
        String puerto = System.getenv("BACKEND_PORT");
        return puerto == null || puerto.isBlank() ? 5050 : Integer.parseInt(puerto);
    }

    @Override
    public void close() {
        cliente.close();
    }
}
