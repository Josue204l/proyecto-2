package backend.logica;

import backend.datos.*;
import logic.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import protocolo.IService;

public class ServiceBackend implements IService {
    private final FuncionarioDao funcionarioDao;
    private final CategoriaDao categoriaDao;
    private final RecursoDao recursoDao;
    private final ReservaDao reservaDao;

    public ServiceBackend(Database conexion) {
        this.funcionarioDao = new FuncionarioDao(conexion);
        this.categoriaDao   = new CategoriaDao(conexion);
        this.recursoDao     = new RecursoDao(conexion, categoriaDao);
        this.reservaDao     = new ReservaDao(conexion, funcionarioDao, recursoDao, categoriaDao);
    }

    // --- Autenticación ---

    public Funcionario login(String id, String clave) throws Exception {
        Funcionario f = funcionarioDao.buscarPorId(id);
        if (f == null || f.getClave() == null || !f.getClave().equals(clave)) throw new Exception("Credenciales incorrectas.");
        f.setClave(null);
        return f;
    }

    public void cambiarClave(String id, String claveActual, String claveNueva, String claveConfirmar) throws Exception {
        if (claveNueva == null || claveNueva.length() > 20) throw new Exception("La clave debe tener entre 1 y 20 caracteres.");
        Funcionario f = funcionarioDao.buscarPorId(id);
        if (f == null) throw new Exception("Usuario no encontrado.");
        if (!f.getClave().equals(claveActual)) throw new Exception("La clave actual es incorrecta.");
        if (claveNueva.isBlank()) throw new Exception("La nueva clave no puede estar vacía.");
        if (!claveNueva.equals(claveConfirmar)) throw new Exception("La nueva clave y su confirmación no coinciden.");
        f.setClave(claveNueva);
        funcionarioDao.actualizar(f);
    }

    // --- Funcionarios ---

    public List<Funcionario> getFuncionarios() throws Exception {
        List<Funcionario> lista = funcionarioDao.listar();
        lista.forEach(f -> f.setClave(null));
        return lista;
    }

    public void guardarFuncionario(Funcionario f) throws Exception {
        if (f.getId() == null || f.getId().isBlank()) throw new Exception("El ID no puede estar vacío.");
        if (f.getId().length() > 10) throw new Exception("El ID no puede superar 10 caracteres.");
        if (f.getNombre() == null || f.getNombre().isBlank()) throw new Exception("El nombre es obligatorio.");
        if (f.getNombre().length() > 100) throw new Exception("El nombre no puede superar 100 caracteres.");
        if (f.getTelefono() != null && f.getTelefono().length() > 20) throw new Exception("El teléfono no puede superar 20 caracteres.");
        if (f.getPuesto() != null && f.getPuesto().length() > 100) throw new Exception("El puesto no puede superar 100 caracteres.");
        Funcionario existente = funcionarioDao.buscarPorId(f.getId());
        if (existente == null) {
            if (f.getClave() == null || f.getClave().isBlank()) f.setClave(f.getId());
            if (f.getRol() == null || f.getRol().isBlank()) f.setRol("FUNCIONARIO");
            funcionarioDao.insertar(f);
        } else {
            if (f.getClave() == null || f.getClave().isBlank()) f.setClave(existente.getClave());
            if (f.getRol() == null || f.getRol().isBlank()) f.setRol(existente.getRol());
            funcionarioDao.actualizar(f);
        }
    }

    public void eliminarFuncionario(String id) throws Exception {
        Funcionario funcionario = funcionarioDao.buscarPorId(id);
        if (funcionario == null) throw new Exception("Funcionario no encontrado.");
        if ("ADMIN".equalsIgnoreCase(funcionario.getRol())) throw new Exception("No se puede eliminar el administrador.");
        boolean tieneReservas = reservaDao.listar().stream()
                .anyMatch(r -> r.getSolicitante() != null && id.equals(r.getSolicitante().getId()));
        if (tieneReservas) throw new Exception("No se puede eliminar un funcionario con reservas registradas.");
        funcionarioDao.eliminar(id);
    }

    // --- Categorías ---

    public List<Categoria> getCategorias() throws Exception { return categoriaDao.listar(); }

    public void guardarCategoria(Categoria cat) throws Exception {
        if (cat.getId() == null || cat.getId().isBlank()) throw new Exception("El ID no puede estar vacío.");
        if (cat.getId().length() > 10) throw new Exception("El ID de categoría no puede superar 10 caracteres.");
        if (cat.getNombre() == null || cat.getNombre().isBlank()) throw new Exception("El nombre de la categoría es obligatorio.");
        if (cat.getNombre().length() > 100) throw new Exception("El nombre de la categoría no puede superar 100 caracteres.");
        if (cat.getDescripcion() != null && cat.getDescripcion().length() > 255) throw new Exception("La descripción no puede superar 255 caracteres.");
        if (categoriaDao.buscarPorId(cat.getId()) == null) categoriaDao.insertar(cat);
        else categoriaDao.actualizar(cat);
    }

    public void eliminarCategoria(String id) throws Exception {
        if (categoriaDao.buscarPorId(id) == null) throw new Exception("Categoría no encontrada.");
        if (recursoDao.listar().stream().anyMatch(r -> r.getCategoria() != null && id.equals(r.getCategoria().getId()))) {
            throw new Exception("No se puede eliminar una categoría asociada a recursos.");
        }
        if (reservaDao.listar().stream().anyMatch(r -> r.getCategoriasRequeridas().stream()
                .anyMatch(c -> id.equals(c.getId())))) {
            throw new Exception("No se puede eliminar una categoría usada en reservas registradas.");
        }
        categoriaDao.eliminar(id);
    }

    // --- Recursos ---

    public List<Recurso> getRecursos() throws Exception { return recursoDao.listar(); }

    public void guardarRecurso(Recurso r) throws Exception {
        if (r.getId() == null || r.getId().isBlank()) throw new Exception("El ID no puede estar vacío.");
        if (r.getId().length() > 10) throw new Exception("El ID del recurso no puede superar 10 caracteres.");
        if (r.getDescripcion() == null || r.getDescripcion().isBlank()) throw new Exception("La descripción del recurso es obligatoria.");
        if (r.getDescripcion().length() > 255) throw new Exception("La descripción no puede superar 255 caracteres.");
        if (r.getCategoria() == null || r.getCategoria().getId() == null) throw new Exception("Seleccione una categoría válida.");
        if (categoriaDao.buscarPorId(r.getCategoria().getId()) == null) throw new Exception("Categoría del recurso no encontrada.");
        if (recursoDao.buscarPorId(r.getId()) == null) recursoDao.insertar(r);
        else recursoDao.actualizar(r);
    }

    public void eliminarRecurso(String id) throws Exception {
        if (recursoDao.buscarPorId(id) == null) throw new Exception("Recurso no encontrado.");
        if (reservaDao.listar().stream().anyMatch(r -> r.usaRecurso(id))) {
            throw new Exception("No se puede eliminar un recurso utilizado en reservas registradas.");
        }
        recursoDao.eliminar(id);
    }

    // --- Reservas ---

    public List<Reserva> getReservas() throws Exception {
        List<Reserva> lista = reservaDao.listar();
        lista.stream().map(Reserva::getSolicitante).filter(java.util.Objects::nonNull)
                .forEach(f -> f.setClave(null));
        return lista;
    }

    public synchronized Reserva crearReserva(String titulo, LocalDate fecha, LocalTime inicio, LocalTime fin,
                                List<String> idsCategorias, String idFuncionario) throws Exception {
        if (titulo == null || titulo.isBlank()) throw new Exception("El título no puede estar vacío.");
        if (titulo.length() > 150) throw new Exception("El título no puede superar 150 caracteres.");
        if (fecha == null || inicio == null || fin == null) throw new Exception("Fecha y horas son obligatorias.");
        if (fecha.isBefore(LocalDate.now())) throw new Exception("La fecha de la reserva no puede estar en el pasado.");
        if (idsCategorias == null || idsCategorias.isEmpty()) throw new Exception("Seleccione al menos una categoría.");
        if (idsCategorias.stream().distinct().count() != idsCategorias.size()) throw new Exception("No repita categorías.");
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
        // El esquema SQL permite 10 caracteres para el ID de reserva.
        String id;
        do {
            id = "RES-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        } while (reservaDao.buscarPorId(id) != null);
        Reserva reserva = new Reserva(id, titulo, fecha, inicio, fin, recursos, categorias, solicitante);
        reservaDao.insertar(reserva);
        solicitante.setClave(null);
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
