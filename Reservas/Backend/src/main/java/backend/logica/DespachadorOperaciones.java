package backend.logica;

import backend.red.ManejadorSolicitudes;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import logic.*;
import protocolo.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DespachadorOperaciones implements ManejadorSolicitudes {

    private final ServiceBackend service;
    private final ObjectMapper mapper = JsonProtocolo.mapper();

    public DespachadorOperaciones(ServiceBackend service) {
        this.service = service;
    }

    @Override
    public MensajeRespuesta procesar(MensajeSolicitud solicitud, Consumer<EventoServidor> emisorEventos) throws Exception {
        JsonNode d = solicitud.datos();
        Object resultado = switch (solicitud.operacion()) {

            // --- Auth ---
            case "LOGIN" -> service.login(d.get("id").asText(), d.get("clave").asText());
            case "CAMBIAR_CLAVE" -> {
                service.cambiarClave(d.get("id").asText(), d.get("claveActual").asText(),
                        d.get("claveNueva").asText(), d.get("claveConfirmar").asText());
                yield null;
            }

            // --- Funcionarios ---
            case "LISTAR_FUNCIONARIOS" -> service.getFuncionarios();
            case "GUARDAR_FUNCIONARIO" -> {
                service.guardarFuncionario(mapper.treeToValue(d, Funcionario.class));
                yield null;
            }
            case "ELIMINAR_FUNCIONARIO" -> {
                service.eliminarFuncionario(d.get("id").asText());
                yield null;
            }

            // --- Categorías ---
            case "LISTAR_CATEGORIAS" -> service.getCategorias();
            case "GUARDAR_CATEGORIA" -> {
                service.guardarCategoria(mapper.treeToValue(d, Categoria.class));
                yield null;
            }
            case "ELIMINAR_CATEGORIA" -> {
                service.eliminarCategoria(d.get("id").asText());
                yield null;
            }

            // --- Recursos ---
            case "LISTAR_RECURSOS" -> service.getRecursos();
            case "GUARDAR_RECURSO" -> {
                service.guardarRecurso(mapper.treeToValue(d, Recurso.class));
                yield null;
            }
            case "ELIMINAR_RECURSO" -> {
                service.eliminarRecurso(d.get("id").asText());
                yield null;
            }

            // --- Reservas ---
            case "LISTAR_RESERVAS" -> service.getReservas();
            case "CREAR_RESERVA" -> {
                List<String> idsCats = new ArrayList<>();
                for (JsonNode n : d.get("idsCategorias")) idsCats.add(n.asText());
                Reserva nueva = service.crearReserva(
                        d.get("titulo").asText(),
                        LocalDate.parse(d.get("fecha").asText()),
                        LocalTime.parse(d.get("horaInicio").asText()),
                        LocalTime.parse(d.get("horaFin").asText()),
                        idsCats,
                        d.get("idFuncionario").asText());
                emisorEventos.accept(new EventoServidor(EventoServidor.NUEVA_RESERVA,
                        mapper.valueToTree(nueva)));
                yield nueva;
            }
            case "CANCELAR_RESERVA" -> {
                service.cancelarReserva(d.get("id").asText());
                yield null;
            }
            case "ELIMINAR_RESERVA" -> {
                service.eliminarReserva(d.get("id").asText());
                yield null;
            }

            default -> throw new Exception("Operación desconocida: " + solicitud.operacion());
        };

        JsonNode nodoResultado = resultado == null ? null : mapper.valueToTree(resultado);
        return MensajeRespuesta.exito(solicitud.id(), nodoResultado);
    }
}
