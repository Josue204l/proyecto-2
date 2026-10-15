package backend.logica;

import backend.red.ManejadorSolicitudes;
import backend.red.ServidorTCP;
import backend.red.WorkerCliente;
import logic.Categoria;
import logic.Funcionario;
import logic.Recurso;
import logic.Reserva;
import protocolo.Notificacion;
import protocolo.Respuesta;
import protocolo.Solicitud;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class DespachadorOperaciones implements ManejadorSolicitudes {

    private final ServiceBackend service;
    private final ServidorTCP    servidor;

    public DespachadorOperaciones(ServiceBackend service, ServidorTCP servidor) {
        this.service  = service;
        this.servidor = servidor;
    }

    @Override
    public Respuesta procesar(Solicitud solicitud, WorkerCliente emisor) throws Exception {
        Object[] p = solicitud.getParametros();
        Object resultado = null;
        String operacion = solicitud.getOperacion();
        if (!"LOGIN".equals(operacion) && !"CAMBIAR_CLAVE".equals(operacion) && !emisor.estaAutenticado()) {
            throw new Exception("Debe iniciar sesión antes de realizar operaciones.");
        }
        if (requiereAdministrador(operacion) && !emisor.esAdministrador()) {
            throw new Exception("Esta operación requiere permisos de administrador.");
        }

        switch (operacion) {

            // --- Auth ---
            case "LOGIN":
                resultado = service.login((String) p[0], (String) p[1]);
                emisor.autenticar((Funcionario) resultado);
                break;
            case "CAMBIAR_CLAVE":
                if (emisor.estaAutenticado() && !emisor.getUsuarioId().equals(p[0])) {
                    throw new Exception("Solo puede cambiar su propia clave.");
                }
                service.cambiarClave((String) p[0], (String) p[1], (String) p[2], (String) p[3]);
                break;

            // --- Funcionarios ---
            case "LISTAR_FUNCIONARIOS":
                resultado = service.getFuncionarios();
                break;
            case "GUARDAR_FUNCIONARIO":
                service.guardarFuncionario((Funcionario) p[0]);
                break;
            case "ELIMINAR_FUNCIONARIO":
                service.eliminarFuncionario((String) p[0]);
                break;

            // --- Categorías ---
            case "LISTAR_CATEGORIAS":
                resultado = service.getCategorias();
                break;
            case "GUARDAR_CATEGORIA":
                service.guardarCategoria((Categoria) p[0]);
                break;
            case "ELIMINAR_CATEGORIA":
                service.eliminarCategoria((String) p[0]);
                break;

            // --- Recursos ---
            case "LISTAR_RECURSOS":
                resultado = service.getRecursos();
                break;
            case "GUARDAR_RECURSO":
                service.guardarRecurso((Recurso) p[0]);
                break;
            case "ELIMINAR_RECURSO":
                service.eliminarRecurso((String) p[0]);
                break;

            // --- Reservas ---
            case "LISTAR_RESERVAS":
                resultado = service.getReservas();
                break;
            case "CREAR_RESERVA": {
                @SuppressWarnings("unchecked")
                List<String> idsCats = (List<String>) p[4];
                Reserva nueva = service.crearReserva(
                        (String)    p[0],
                        (LocalDate) p[1],
                        (LocalTime) p[2],
                        (LocalTime) p[3],
                        idsCats,
                        emisor.getUsuarioId());
                // Notificar a todos los demás frontends
                servidor.publicarATodos(new Notificacion(Notificacion.NUEVA_RESERVA, nueva), emisor);
                resultado = nueva;
                break;
            }
            case "CANCELAR_RESERVA":
                validarPropietarioReserva((String) p[0], emisor);
                service.cancelarReserva((String) p[0]);
                break;
            case "ELIMINAR_RESERVA":
                service.eliminarReserva((String) p[0]);
                break;

            default:
                throw new Exception("Operación desconocida: " + solicitud.getOperacion());
        }

        return Respuesta.exito(solicitud.getId(), resultado);
    }

    private void validarPropietarioReserva(String reservaId, WorkerCliente emisor) throws Exception {
        if (emisor.esAdministrador()) return;
        Reserva reserva = service.getReservas().stream().filter(r -> r.getId().equals(reservaId))
                .findFirst().orElseThrow(() -> new Exception("Reserva no encontrada."));
        if (reserva.getSolicitante() == null || !emisor.getUsuarioId().equals(reserva.getSolicitante().getId())) {
            throw new Exception("No puede modificar una reserva de otro funcionario.");
        }
    }

    private boolean requiereAdministrador(String operacion) {
        return switch (operacion) {
            case "LISTAR_FUNCIONARIOS", "GUARDAR_FUNCIONARIO", "ELIMINAR_FUNCIONARIO",
                    "GUARDAR_CATEGORIA", "ELIMINAR_CATEGORIA", "GUARDAR_RECURSO", "ELIMINAR_RECURSO",
                    "ELIMINAR_RESERVA" -> true;
            default -> false;
        };
    }
}
