package protocolo;

import com.fasterxml.jackson.databind.JsonNode;

/** Respuesta correlacionada con una petición. Los errores nunca contienen trazas internas. */
public record MensajeRespuesta(String id, boolean correcta, JsonNode datos, String error) {
    public static MensajeRespuesta exito(String id, JsonNode datos) {
        return new MensajeRespuesta(id, true, datos, null);
    }

    public static MensajeRespuesta error(String id, String mensaje) {
        return new MensajeRespuesta(id, false, null, mensaje);
    }
}
