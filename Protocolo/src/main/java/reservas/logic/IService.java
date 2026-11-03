package reservas.logic;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface IService {
    Funcionario login(String id, String clave) throws Exception;

    void cambiarClave(String claveActual, String claveNueva, String claveConfirmar) throws Exception;

    List<Funcionario> listarFuncionarios() throws Exception;

    void guardarFuncionario(Funcionario funcionario) throws Exception;

    void eliminarFuncionario(String id) throws Exception;

    List<Categoria> listarCategorias() throws Exception;

    void guardarCategoria(Categoria categoria) throws Exception;

    void eliminarCategoria(String id) throws Exception;

    List<Recurso> listarRecursos() throws Exception;

    void guardarRecurso(Recurso recurso) throws Exception;

    void eliminarRecurso(String id) throws Exception;

    List<Reserva> listarReservas() throws Exception;

    Reserva crearReserva(String titulo, LocalDate fecha, LocalTime inicio, LocalTime fin,
                         List<String> idsCategorias) throws Exception;

    void cancelarReserva(String id) throws Exception;

    void desconectar() throws Exception;
}
