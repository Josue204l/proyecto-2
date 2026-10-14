package protocolo;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;

/** Mensaje de petición enviado por un Frontend al Backend. */
public record MensajeSolicitud(String id, String operacion, JsonNode datos) {
    public MensajeSolicitud(String operacion, JsonNode datos) {
        this(UUID.randomUUID().toString(), operacion, datos);
    }
}
