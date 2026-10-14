package protocolo;

import com.fasterxml.jackson.databind.JsonNode;

/** Evento no solicitado, por ejemplo la notificación de una reserva recién creada. */
public record EventoServidor(String tipo, JsonNode datos) {
    public static final String NUEVA_RESERVA = "NUEVA_RESERVA";
}
