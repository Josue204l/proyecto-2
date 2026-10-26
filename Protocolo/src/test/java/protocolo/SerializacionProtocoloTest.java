package protocolo;

import logic.Funcionario;
import logic.Reserva;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class SerializacionProtocoloTest {
    @Test
    void solicitudYRespuestaSeSerializanConObjetosDelDominio() throws Exception {
        Funcionario funcionario = new Funcionario("f01", null, "FUNCIONARIO", "Ana");
        Reserva reserva = new Reserva("r01", "Reunión", LocalDate.of(2026, 11, 2),
                LocalTime.of(9, 0), LocalTime.of(10, 0), List.of(), List.of(), funcionario);
        Solicitud original = new Solicitud("CREAR_RESERVA", reserva, List.of("CAT-01"));

        Solicitud recibida = roundTrip(original, Solicitud.class);
        Respuesta respuesta = Respuesta.exito(recibida.getId(), reserva);
        Respuesta recibidaRespuesta = roundTrip(respuesta, Respuesta.class);

        assertEquals(original.getId(), recibida.getId());
        assertEquals("CREAR_RESERVA", recibida.getOperacion());
        assertInstanceOf(Reserva.class, recibidaRespuesta.getDatos());
        assertEquals("r01", ((Reserva) recibidaRespuesta.getDatos()).getId());
    }

    private static <T> T roundTrip(T value, Class<T> type) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) { out.writeObject(value); }
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            return type.cast(in.readObject());
        }
    }
}
