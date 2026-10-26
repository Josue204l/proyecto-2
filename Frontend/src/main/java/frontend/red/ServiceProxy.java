package frontend.red;

import logic.Categoria;
import logic.Funcionario;
import logic.Recurso;
import logic.Reserva;
import protocolo.IService;
import protocolo.Respuesta;
import protocolo.Solicitud;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Proxy local de IService: convierte cada operación en una Solicitud TCP y
 * espera la Respuesta correlacionada que entrega ClienteSocket.
 */
public class ServiceProxy implements IService {

    private final ClienteSocket cliente;

    public ServiceProxy(String host, int puerto) throws Exception {
        this.cliente = new ClienteSocket(host, puerto);
    }

    /** Registra quién recibirá las notificaciones asíncronas (alertas). */
    public void setReceptorNotificaciones(ReceptorNotificaciones receptor) {
        cliente.setReceptorNotificaciones(receptor);
    }

    public void cerrar() {
        cliente.cerrar();
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private Respuesta ejecutar(String operacion, Object... parametros) throws Exception {
        Solicitud solicitud = new Solicitud(operacion, parametros);
        Respuesta respuesta = cliente.enviar(solicitud);
        if (!respuesta.isExitosa()) throw new Exception(respuesta.getError());
        return respuesta;
    }

    // ---------------------------------------------------------------
    // IService
    // ---------------------------------------------------------------

    @Override
    public Funcionario login(String id, String clave) throws Exception {
        return (Funcionario) ejecutar("LOGIN", id, clave).getDatos();
    }

    @Override
    public void cambiarClave(String id, String claveActual, String claveNueva, String claveConfirmar) throws Exception {
        ejecutar("CAMBIAR_CLAVE", id, claveActual, claveNueva, claveConfirmar);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Funcionario> getFuncionarios() throws Exception {
        return (List<Funcionario>) ejecutar("LISTAR_FUNCIONARIOS").getDatos();
    }

    @Override
    public void guardarFuncionario(Funcionario f) throws Exception {
        ejecutar("GUARDAR_FUNCIONARIO", f);
    }

    @Override
    public void eliminarFuncionario(String id) throws Exception {
        ejecutar("ELIMINAR_FUNCIONARIO", id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Categoria> getCategorias() throws Exception {
        return (List<Categoria>) ejecutar("LISTAR_CATEGORIAS").getDatos();
    }

    @Override
    public void guardarCategoria(Categoria cat) throws Exception {
        ejecutar("GUARDAR_CATEGORIA", cat);
    }

    @Override
    public void eliminarCategoria(String id) throws Exception {
        ejecutar("ELIMINAR_CATEGORIA", id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Recurso> getRecursos() throws Exception {
        return (List<Recurso>) ejecutar("LISTAR_RECURSOS").getDatos();
    }

    @Override
    public void guardarRecurso(Recurso r) throws Exception {
        ejecutar("GUARDAR_RECURSO", r);
    }

    @Override
    public void eliminarRecurso(String id) throws Exception {
        ejecutar("ELIMINAR_RECURSO", id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Reserva> getReservas() throws Exception {
        return (List<Reserva>) ejecutar("LISTAR_RESERVAS").getDatos();
    }

    @Override
    public Reserva crearReserva(String titulo, LocalDate fecha, LocalTime horaInicio,
                                LocalTime horaFin, List<String> idsCategorias,
                                String idFuncionario) throws Exception {
        return (Reserva) ejecutar("CREAR_RESERVA", titulo, fecha, horaInicio, horaFin,
                idsCategorias, idFuncionario).getDatos();
    }

    @Override
    public void cancelarReserva(String id) throws Exception {
        ejecutar("CANCELAR_RESERVA", id);
    }

    @Override
    public void eliminarReserva(String id) throws Exception {
        ejecutar("ELIMINAR_RESERVA", id);
    }

    // ---------------------------------------------------------------
    // Configuración desde variables de entorno
    // ---------------------------------------------------------------

    public static String hostConfigurado() {
        String h = System.getenv("BACKEND_HOST");
        return (h == null || h.isBlank()) ? "localhost" : h;
    }

    public static int puertoConfigurado() {
        String p = System.getenv("BACKEND_PORT");
        return (p == null || p.isBlank()) ? 5050 : Integer.parseInt(p);
    }
}
