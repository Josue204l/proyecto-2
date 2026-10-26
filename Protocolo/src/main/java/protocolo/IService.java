package protocolo;

import logic.Categoria;
import logic.Funcionario;
import logic.Recurso;
import logic.Reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface IService {

    // --- Autenticación ---
    Funcionario login(String id, String clave) throws Exception;
    void cambiarClave(String id, String claveActual, String claveNueva, String claveConfirmar) throws Exception;

    // --- Funcionarios ---
    List<Funcionario> getFuncionarios() throws Exception;
    void guardarFuncionario(Funcionario f) throws Exception;
    void eliminarFuncionario(String id) throws Exception;

    // --- Categorías ---
    List<Categoria> getCategorias() throws Exception;
    void guardarCategoria(Categoria cat) throws Exception;
    void eliminarCategoria(String id) throws Exception;

    // --- Recursos ---
    List<Recurso> getRecursos() throws Exception;
    void guardarRecurso(Recurso r) throws Exception;
    void eliminarRecurso(String id) throws Exception;

    // --- Reservas ---
    List<Reserva> getReservas() throws Exception;
    Reserva crearReserva(String titulo, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                         List<String> idsCategorias, String idFuncionario) throws Exception;
    void cancelarReserva(String id) throws Exception;
    void eliminarReserva(String id) throws Exception;
}
