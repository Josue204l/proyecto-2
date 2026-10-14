package backend.logica;

import backend.datos.*;
import logic.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServiceBackend {
    private final FuncionarioDao funcionarioDao;
    private final CategoriaDao categoriaDao;
    private final RecursoDao recursoDao;
    private final ReservaDao reservaDao;

    public ServiceBackend(ConexionMySql conexion) {
        this.funcionarioDao = new FuncionarioDao(conexion);
        this.categoriaDao   = new CategoriaDao(conexion);
        this.recursoDao     = new RecursoDao(conexion, categoriaDao);
        this.reservaDao     = new ReservaDao(conexion, funcionarioDao, recursoDao, categoriaDao);
    }

    // --- Autenticación ---

    public Funcionario login(String id, String clave) throws Exception {
        Funcionario f = funcionarioDao.buscarPorId(id);
        if (f == null || !f.getClave().equals(clave)) throw new Exception("Credenciales incorrectas.");
        return f;
    }

    public void cambiarClave(String id, String claveActual, String claveNueva, String claveConfirmar) throws Exception {
        Funcionario f = funcionarioDao.buscarPorId(id);
        if (f == null) throw new Exception("Usuario no encontrado.");
        if (!f.getClave().equals(claveActual)) throw new Exception("La clave actual es incorrecta.");
        if (claveNueva == null || claveNueva.isBlank()) throw new Exception("La nueva clave no puede estar vacía.");
        if (!claveNueva.equals(claveConfirmar)) throw new Exception("La nueva clave y su confirmación no coinciden.");
        f.setClave(claveNueva);
        funcionarioDao.actualizar(f);
    }

    // --- Funcionarios ---

    public List<Funcionario> getFuncionarios() throws Exception { return funcionarioDao.listar(); }

    public void guardarFuncionario(Funcionario f) throws Exception {
        if (f.getId() == null || f.getId().isBlank()) throw new Exception("El ID no puede estar vacío.");
        if (funcionarioDao.buscarPorId(f.getId()) == null) funcionarioDao.insertar(f);
        else funcionarioDao.actualizar(f);
    }

    public void eliminarFuncionario(String id) throws Exception {
        if (funcionarioDao.buscarPorId(id) == null) throw new Exception("Funcionario no encontrado.");
        funcionarioDao.eliminar(id);
    }

    // --- Categorías ---

    public List<Categoria> getCategorias() throws Exception { return categoriaDao.listar(); }

    public void guardarCategoria(Categoria cat) throws Exception {
        if (cat.getId() == null || cat.getId().isBlank()) throw new Exception("El ID no puede estar vacío.");
        if (categoriaDao.buscarPorId(cat.getId()) == null) categoriaDao.insertar(cat);
        else categoriaDao.actualizar(cat);
    }

    public void eliminarCategoria(String id) throws Exception {
        if (categoriaDao.buscarPorId(id) == null) throw new Exception("Categoría no encontrada.");
        categoriaDao.eliminar(id);
    }

    // --- Recursos ---

    public List<Recurso> getRecursos() throws Exception { return recursoDao.listar(); }

    public void guardarRecurso(Recurso r) throws Exception {
        if (r.getId() == null || r.getId().isBlank()) throw new Exception("El ID no puede estar vacío.");
        if (recursoDao.buscarPorId(r.getId()) == null) recursoDao.insertar(r);
        else recursoDao.actualizar(r);
    }

    public void eliminarRecurso(String id) throws Exception {
        if (recursoDao.buscarPorId(id) == null) throw new Exception("Recurso no encontrado.");
        recursoDao.eliminar(id);
    }

    // --- Reservas ---

    public List<Reserva> getReservas() throws Exception { return reservaDao.listar(); }

    public Reserva crearReserva(String titulo, LocalDate fecha, LocalTime inicio, LocalTime fin,
                                List<String> idsCategorias, String idFuncionario) throws Exception {
        if (titulo == null || titulo.isBlank()) throw new Exception("El título no puede estar vacío.");
        if (!fin.isAfter(inicio)) throw new Exception("La hora de fin debe ser posterior a la de inicio.");

        Funcionario solicitante = funcionarioDao.buscarPorId(idFuncionario);
        if (solicitante == null) throw new Exception("Funcionario no encontrado.");

        List<Categoria> categorias = new ArrayList<>();
        for (String idCat : idsCategorias) {
            Categoria cat = categoriaDao.buscarPorId(idCat);
            if (cat == null) throw new Exception("Categoría no encontrada: " + idCat);
            categorias.add(cat);
        }

        List<Recurso> recursos = asignarRecursosDisponibles(categorias, fecha, inicio, fin);
        String id = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Reserva reserva = new Reserva(id, titulo, fecha, inicio, fin, recursos, categorias, solicitante);
        reservaDao.insertar(reserva);
        return reserva;
    }

    public void cancelarReserva(String id) throws Exception {
        Reserva r = reservaDao.buscarPorId(id);
        if (r == null) throw new Exception("Reserva no encontrada.");
        if (!r.isActiva()) throw new Exception("La reserva ya no está activa.");
        if (!r.esFutura()) throw new Exception("Solo se pueden cancelar reservas futuras.");
        r.setEstado("CANCELADA");
        reservaDao.actualizar(r);
    }

    public void eliminarReserva(String id) throws Exception {
        if (reservaDao.buscarPorId(id) == null) throw new Exception("Reserva no encontrada.");
        reservaDao.eliminar(id);
    }

    // --- Disponibilidad ---

    private List<Recurso> asignarRecursosDisponibles(List<Categoria> categorias, LocalDate fecha,
                                                      LocalTime inicio, LocalTime fin) throws Exception {
        List<Reserva> reservasDelDia = reservaDao.listar().stream()
                .filter(r -> r.isActiva() && fecha.equals(r.getFecha()))
                .toList();

        List<Recurso> todosRecursos = recursoDao.listar();
        List<Recurso> asignados = new ArrayList<>();
        List<String> sinDisponibilidad = new ArrayList<>();

        for (Categoria cat : categorias) {
            Recurso encontrado = null;
            for (Recurso rec : todosRecursos) {
                if (rec.getCategoria() == null || !rec.getCategoria().getId().equals(cat.getId())) continue;
                if (asignados.stream().anyMatch(a -> a.getId().equals(rec.getId()))) continue;
                boolean libre = reservasDelDia.stream().noneMatch(r ->
                        inicio.isBefore(r.getHoraFin()) && r.getHoraInicio().isBefore(fin) && r.usaRecurso(rec.getId()));
                if (libre) { encontrado = rec; break; }
            }
            if (encontrado != null) asignados.add(encontrado);
            else sinDisponibilidad.add(cat.getEtiqueta());
        }

        if (!sinDisponibilidad.isEmpty())
            throw new Exception("Sin disponibilidad para: " + String.join(", ", sinDisponibilidad));

        return asignados;
    }
}
