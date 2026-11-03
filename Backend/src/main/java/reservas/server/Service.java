package reservas.server;

import reservas.data.AlmacenXml;
import reservas.logic.Categoria;
import reservas.logic.Funcionario;
import reservas.logic.Recurso;
import reservas.logic.Reserva;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Service {
    public interface Accion {
        void ejecutar() throws Exception;
    }

    private final AlmacenXml datos;
    private String idSesion;
    private String rolSesion;

    public Service(Path archivo) throws Exception {
        this.datos = new AlmacenXml(archivo);
    }

    public synchronized boolean estaVacio() {
        return datos.getFuncionarios().isEmpty();
    }

    public synchronized void sembrarDemo() throws Exception {
        if (!estaVacio()) return;
        datos.getFuncionarios().add(new Funcionario("admin", "222", "ADMIN", "Administrador", "2222-0000"));
        datos.getFuncionarios().add(new Funcionario("111", "111", "FUNCIONARIO", "Funcionario Test", "8888-1234"));
        Categoria sala = new Categoria("CAT-001", "Sala para 10 personas");
        Categoria laptop = new Categoria("CAT-002", "Laptop windows 11");
        datos.getCategorias().add(sala);
        datos.getCategorias().add(laptop);
        datos.getRecursos().add(new Recurso("SALA-1", "Sala 1 primer piso", sala));
        datos.getRecursos().add(new Recurso("SALA-2", "Sala 2 segundo piso", sala));
        datos.getRecursos().add(new Recurso("LAP-001", "Laptop #238715", laptop));
        Reserva demo = new Reserva();
        demo.setId("RES-DEMO");
        demo.setTitulo("Reunion de coordinacion");
        demo.setFecha(LocalDate.of(2026, 11, 10));
        demo.setHoraInicio(LocalTime.of(10, 0));
        demo.setHoraFin(LocalTime.of(12, 0));
        demo.setSolicitante(buscarFuncionario("111"));
        demo.setEstado("ACTIVA");
        demo.getCategoriasRequeridas().add(sala);
        demo.getRecursosAsignados().add(buscarRecurso("SALA-1"));
        datos.getReservas().add(demo);
        datos.guardar();
    }

    public synchronized void enSesion(String id, String rol, Accion accion) throws Exception {
        Funcionario funcionario = buscarFuncionario(id);
        if (funcionario == null || rol == null || !rol.equalsIgnoreCase(funcionario.getRol())) {
            throw new Exception("Debe iniciar sesión antes de realizar operaciones.");
        }
        this.idSesion = funcionario.getId();
        this.rolSesion = funcionario.getRol();
        try {
            accion.ejecutar();
        } finally {
            this.idSesion = null;
            this.rolSesion = null;
        }
    }

    public synchronized Funcionario login(String id, String clave) throws Exception {
        if (id == null || id.isBlank() || clave == null || clave.isBlank()) {
            throw new Exception("Debe indicar el id y la clave.");
        }
        Funcionario funcionario = buscarFuncionario(id.trim());
        if (funcionario == null || funcionario.getClave() == null || !funcionario.getClave().equals(clave)) {
            throw new Exception("Credenciales incorrectas.");
        }
        return copiaFuncionario(funcionario);
    }

    public synchronized void cambiarClave(String claveActual, String claveNueva, String claveConfirmar) throws Exception {
        exigirSesion();
        if (claveNueva == null || claveNueva.isBlank() || claveNueva.length() > 20) {
            throw new Exception("La clave debe tener entre 1 y 20 caracteres.");
        }
        if (!claveNueva.equals(claveConfirmar)) {
            throw new Exception("La nueva clave y su confirmación no coinciden.");
        }
        Funcionario funcionario = buscarFuncionario(idSesion);
        if (funcionario == null) throw new Exception("Usuario no encontrado.");
        if (funcionario.getClave() == null || !funcionario.getClave().equals(claveActual)) {
            throw new Exception("La clave actual es incorrecta.");
        }
        funcionario.setClave(claveNueva);
        datos.guardar();
    }

    public synchronized List<Funcionario> listarFuncionarios() throws Exception {
        exigirAdmin();
        List<Funcionario> copia = new ArrayList<>();
        for (Funcionario funcionario : datos.getFuncionarios()) copia.add(copiaFuncionario(funcionario));
        return copia;
    }

    public synchronized void guardarFuncionario(Funcionario funcionario) throws Exception {
        exigirAdmin();
        if (funcionario == null) throw new Exception("Dato de red inválido: el funcionario viene vacío.");
        String id = limpiar(funcionario.getId());
        String nombre = limpiar(funcionario.getNombre());
        String telefono = funcionario.getTelefono() == null ? "" : funcionario.getTelefono().trim();
        if (id.isBlank()) throw new Exception("El ID no puede estar vacío.");
        if (id.length() > 10) throw new Exception("El ID no puede superar 10 caracteres.");
        if (nombre.isBlank()) throw new Exception("El nombre es obligatorio.");
        if (nombre.length() > 100) throw new Exception("El nombre no puede superar 100 caracteres.");
        if (telefono.length() > 20) throw new Exception("El teléfono no puede superar 20 caracteres.");
        Funcionario existente = buscarFuncionario(id);
        if (existente == null) {
            datos.getFuncionarios().add(new Funcionario(id, id, "FUNCIONARIO", nombre, telefono));
        } else {
            existente.setNombre(nombre);
            existente.setTelefono(telefono);
        }
        datos.guardar();
    }

    public synchronized void eliminarFuncionario(String id) throws Exception {
        exigirAdmin();
        Funcionario funcionario = buscarFuncionario(id);
        if (funcionario == null) throw new Exception("Funcionario no encontrado.");
        if ("ADMIN".equalsIgnoreCase(funcionario.getRol())) {
            throw new Exception("No se puede eliminar el administrador.");
        }
        for (Reserva reserva : datos.getReservas()) {
            if (reserva.getSolicitante() != null && id.equals(reserva.getSolicitante().getId())) {
                throw new Exception("No se puede eliminar un funcionario con reservas registradas.");
            }
        }
        datos.getFuncionarios().remove(funcionario);
        datos.guardar();
    }

    public synchronized List<Categoria> listarCategorias() throws Exception {
        exigirSesion();
        List<Categoria> copia = new ArrayList<>();
        for (Categoria categoria : datos.getCategorias()) copia.add(copiaCategoria(categoria));
        return copia;
    }

    public synchronized void guardarCategoria(Categoria categoria) throws Exception {
        exigirAdmin();
        if (categoria == null) throw new Exception("Dato de red inválido: la categoría viene vacía.");
        String descripcion = limpiar(categoria.getDescripcion());
        if (descripcion.isBlank()) throw new Exception("La descripción de la categoría es obligatoria.");
        if (descripcion.length() > 255) throw new Exception("La descripción no puede superar 255 caracteres.");
        String id = limpiar(categoria.getId());
        if (id.isBlank()) {
            datos.getCategorias().add(new Categoria(nuevoIdCategoria(), descripcion));
        } else {
            Categoria existente = buscarCategoria(id);
            if (existente == null) {
                throw new Exception("El id de la categoría se genera automáticamente. Déjelo vacío para crear una nueva.");
            }
            existente.setDescripcion(descripcion);
        }
        datos.guardar();
    }

    public synchronized void eliminarCategoria(String id) throws Exception {
        exigirAdmin();
        Categoria categoria = buscarCategoria(id);
        if (categoria == null) throw new Exception("Categoría no encontrada.");
        for (Recurso recurso : datos.getRecursos()) {
            if (recurso.getCategoria() != null && id.equals(recurso.getCategoria().getId())) {
                throw new Exception("No se puede eliminar una categoría asociada a recursos.");
            }
        }
        for (Reserva reserva : datos.getReservas()) {
            for (Categoria pedida : reserva.getCategoriasRequeridas()) {
                if (id.equals(pedida.getId())) {
                    throw new Exception("No se puede eliminar una categoría usada en reservas registradas.");
                }
            }
        }
        datos.getCategorias().remove(categoria);
        datos.guardar();
    }

    public synchronized List<Recurso> listarRecursos() throws Exception {
        exigirSesion();
        List<Recurso> copia = new ArrayList<>();
        for (Recurso recurso : datos.getRecursos()) copia.add(copiaRecurso(recurso));
        return copia;
    }

    public synchronized void guardarRecurso(Recurso recurso) throws Exception {
        exigirAdmin();
        if (recurso == null) throw new Exception("Dato de red inválido: el recurso viene vacío.");
        String id = limpiar(recurso.getId());
        String descripcion = limpiar(recurso.getDescripcion());
        if (id.isBlank()) throw new Exception("El ID no puede estar vacío.");
        if (id.length() > 10) throw new Exception("El ID del recurso no puede superar 10 caracteres.");
        if (descripcion.isBlank()) throw new Exception("La descripción del recurso es obligatoria.");
        if (descripcion.length() > 255) throw new Exception("La descripción no puede superar 255 caracteres.");
        if (recurso.getCategoria() == null || recurso.getCategoria().getId() == null) {
            throw new Exception("Seleccione una categoría válida.");
        }
        Categoria categoria = buscarCategoria(recurso.getCategoria().getId());
        if (categoria == null) throw new Exception("Categoría del recurso no encontrada.");
        Recurso existente = buscarRecurso(id);
        if (existente == null) {
            datos.getRecursos().add(new Recurso(id, descripcion, categoria));
        } else {
            existente.setDescripcion(descripcion);
            existente.setCategoria(categoria);
        }
        datos.guardar();
    }

    public synchronized void eliminarRecurso(String id) throws Exception {
        exigirAdmin();
        Recurso recurso = buscarRecurso(id);
        if (recurso == null) throw new Exception("Recurso no encontrado.");
        for (Reserva reserva : datos.getReservas()) {
            if (reserva.usaRecurso(id)) {
                throw new Exception("No se puede eliminar un recurso utilizado en reservas registradas.");
            }
        }
        datos.getRecursos().remove(recurso);
        datos.guardar();
    }

    public synchronized List<Reserva> listarReservas() throws Exception {
        exigirSesion();
        List<Reserva> copia = new ArrayList<>();
        for (Reserva reserva : datos.getReservas()) copia.add(copiaReserva(reserva));
        return copia;
    }

    public synchronized Reserva crearReserva(String titulo, LocalDate fecha, LocalTime inicio, LocalTime fin,
                                             List<String> idsCategorias) throws Exception {
        exigirFuncionario();
        String actividad = limpiar(titulo);
        if (actividad.isBlank()) throw new Exception("El título no puede estar vacío.");
        if (actividad.length() > 150) throw new Exception("El título no puede superar 150 caracteres.");
        if (fecha == null || inicio == null || fin == null) throw new Exception("Fecha y horas son obligatorias.");
        if (fecha.isBefore(LocalDate.now())) throw new Exception("La fecha de la reserva no puede estar en el pasado.");
        if (!fin.isAfter(inicio)) throw new Exception("La hora de fin debe ser posterior a la de inicio.");
        if (idsCategorias == null || idsCategorias.isEmpty()) throw new Exception("Seleccione al menos una categoría.");
        if (idsCategorias.stream().distinct().count() != idsCategorias.size()) {
            throw new Exception("No repita categorías.");
        }
        Funcionario solicitante = buscarFuncionario(idSesion);
        if (solicitante == null) throw new Exception("Funcionario no encontrado.");

        List<Categoria> categorias = new ArrayList<>();
        for (String idCat : idsCategorias) {
            Categoria categoria = buscarCategoria(idCat);
            if (categoria == null) throw new Exception("Categoría no encontrada: " + idCat);
            categorias.add(categoria);
        }
        List<Recurso> asignados = asignarRecursos(categorias, fecha, inicio, fin);
        Reserva reserva = new Reserva();
        reserva.setId(nuevoIdReserva());
        reserva.setTitulo(actividad);
        reserva.setFecha(fecha);
        reserva.setHoraInicio(inicio);
        reserva.setHoraFin(fin);
        reserva.setCategoriasRequeridas(categorias);
        reserva.setRecursosAsignados(asignados);
        reserva.setSolicitante(solicitante);
        reserva.setEstado("ACTIVA");
        datos.getReservas().add(reserva);
        datos.guardar();
        return copiaReserva(reserva);
    }

    public synchronized void cancelarReserva(String id) throws Exception {
        exigirFuncionario();
        Reserva reserva = buscarReserva(id);
        if (reserva == null) throw new Exception("Reserva no encontrada.");
        if (reserva.getSolicitante() == null || !idSesion.equals(reserva.getSolicitante().getId())) {
            throw new Exception("No puede modificar una reserva de otro funcionario.");
        }
        if (!reserva.isActiva()) throw new Exception("La reserva ya no está activa.");
        if (!reserva.esFutura()) throw new Exception("Solo se pueden cancelar reservas futuras.");
        reserva.setEstado("CANCELADA");
        datos.guardar();
    }

    private List<Recurso> asignarRecursos(List<Categoria> categorias, LocalDate fecha,
                                          LocalTime inicio, LocalTime fin) throws Exception {
        List<Reserva> delDia = new ArrayList<>();
        for (Reserva reserva : datos.getReservas()) {
            if (reserva.isActiva() && fecha.equals(reserva.getFecha())) delDia.add(reserva);
        }
        List<Recurso> asignados = new ArrayList<>();
        List<String> sinCupo = new ArrayList<>();
        for (Categoria categoria : categorias) {
            Recurso elegido = null;
            for (Recurso recurso : datos.getRecursos()) {
                if (recurso.getCategoria() == null || !categoria.getId().equals(recurso.getCategoria().getId())) continue;
                boolean yaTomado = false;
                for (Recurso previo : asignados) {
                    if (previo.getId().equals(recurso.getId())) yaTomado = true;
                }
                if (yaTomado) continue;
                boolean libre = true;
                for (Reserva reserva : delDia) {
                    if (inicio.isBefore(reserva.getHoraFin()) && reserva.getHoraInicio().isBefore(fin)
                            && reserva.usaRecurso(recurso.getId())) {
                        libre = false;
                        break;
                    }
                }
                if (libre) {
                    elegido = recurso;
                    break;
                }
            }
            if (elegido == null) sinCupo.add(categoria.getEtiqueta());
            else asignados.add(elegido);
        }
        if (!sinCupo.isEmpty()) {
            throw new Exception("Sin disponibilidad para: " + String.join(", ", sinCupo));
        }
        return asignados;
    }

    private void exigirSesion() throws Exception {
        if (idSesion == null) throw new Exception("Debe iniciar sesión antes de realizar operaciones.");
    }

    private void exigirAdmin() throws Exception {
        exigirSesion();
        if (!"ADMIN".equalsIgnoreCase(rolSesion)) {
            throw new Exception("Esta operación requiere permisos de administrador.");
        }
    }

    private void exigirFuncionario() throws Exception {
        exigirSesion();
        if (!"FUNCIONARIO".equalsIgnoreCase(rolSesion)) {
            throw new Exception("Esta operación solo la puede ejecutar un funcionario.");
        }
    }

    private Funcionario buscarFuncionario(String id) {
        for (Funcionario funcionario : datos.getFuncionarios()) {
            if (funcionario.getId().equals(id)) return funcionario;
        }
        return null;
    }

    private Categoria buscarCategoria(String id) {
        for (Categoria categoria : datos.getCategorias()) {
            if (categoria.getId().equals(id)) return categoria;
        }
        return null;
    }

    private Recurso buscarRecurso(String id) {
        for (Recurso recurso : datos.getRecursos()) {
            if (recurso.getId().equals(id)) return recurso;
        }
        return null;
    }

    private Reserva buscarReserva(String id) {
        for (Reserva reserva : datos.getReservas()) {
            if (reserva.getId().equals(id)) return reserva;
        }
        return null;
    }

    private String nuevoIdCategoria() {
        int max = 0;
        for (Categoria categoria : datos.getCategorias()) {
            String id = categoria.getId();
            if (id != null && id.toUpperCase().startsWith("CAT-")) {
                try {
                    max = Math.max(max, Integer.parseInt(id.substring(4)));
                } catch (NumberFormatException ignored) { }
            }
        }
        return String.format("CAT-%03d", max + 1);
    }

    private String nuevoIdReserva() {
        int n = datos.getReservas().size() + 1;
        String id;
        do {
            id = String.format("RES-%04d", n);
            n++;
        } while (buscarReserva(id) != null);
        return id;
    }

    private static String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private static Funcionario copiaFuncionario(Funcionario origen) {
        return new Funcionario(origen.getId(), null, origen.getRol(), origen.getNombre(), origen.getTelefono());
    }

    private static Categoria copiaCategoria(Categoria origen) {
        return new Categoria(origen.getId(), origen.getDescripcion());
    }

    private static Recurso copiaRecurso(Recurso origen) {
        return new Recurso(origen.getId(), origen.getDescripcion(),
                origen.getCategoria() == null ? null : copiaCategoria(origen.getCategoria()));
    }

    private static Reserva copiaReserva(Reserva origen) {
        Reserva copia = new Reserva();
        copia.setId(origen.getId());
        copia.setTitulo(origen.getTitulo());
        copia.setFecha(origen.getFecha());
        copia.setHoraInicio(origen.getHoraInicio());
        copia.setHoraFin(origen.getHoraFin());
        copia.setEstado(origen.getEstado());
        copia.setSolicitante(origen.getSolicitante() == null ? null : copiaFuncionario(origen.getSolicitante()));
        List<Categoria> categorias = new ArrayList<>();
        for (Categoria categoria : origen.getCategoriasRequeridas()) categorias.add(copiaCategoria(categoria));
        List<Recurso> recursos = new ArrayList<>();
        for (Recurso recurso : origen.getRecursosAsignados()) recursos.add(copiaRecurso(recurso));
        copia.setCategoriasRequeridas(categorias);
        copia.setRecursosAsignados(recursos);
        return copia;
    }
}
