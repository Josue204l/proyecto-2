package protocolo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/** Codificación JSON por línea para permitir respuestas y eventos en el mismo socket. */
public final class JsonProtocolo {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private JsonProtocolo() { }

    public static ObjectMapper mapper() { return MAPPER; }

    public static String codificar(Object mensaje) {
        try {
            return MAPPER.writeValueAsString(mensaje);
        } catch (Exception e) {
            throw new IllegalArgumentException("No se pudo codificar el mensaje del protocolo.", e);
        }
    }
}
