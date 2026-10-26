package logic;

import data.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Service {

    private static Service instance;
    private Data data;

    private Service() {
        this.data = Data.getInstancia();
    }

    public static synchronized Service instance() {
        if (instance == null) {
            instance = new Service();
        }
        return instance;
    }

    public static Service getInstancia() {
        return instance();
    }

    public Data getData() {
        return data;
    }

    public void store() {
        if (data != null) {
            data.guardarTodo();
        }
    }

    // --- Autenticación ---

    public Usuario login(String id, String clave) {
        Usuario usuario = buscarPorId(id);
        if (usuario != null && usuario.getClave() != null && usuario.getClave().equals(clave)) {
            return usuario;
        }
        return null;
    }

    public Usuario buscarPorId(String id) {
        if (id == null || data == null || data.getFuncionarios() == null) return null;
        for (Funcionario f : data.getFuncionarios()) {
            if (id.equals(f.getId())) return f;
        }
        return null;
    }

    public Funcionario getFuncionario(String id) {
        Usuario u = buscarPorId(id);
        return (u instanceof Funcionario) ? (Funcionario) u : null;
    }

    // --- Cambio de clave ---

    public void cambiarClave(Usuario usuario, String claveActual, String claveNueva, String claveConfirmar) throws Exception {
        if (usuario == null) throw new Exception("Usuario no válido.");
        if (!usuario.getClave().equals(claveActual)) throw new Exception("La clave actual es incorrecta.");
        if (claveNueva == null || claveNueva.trim().isEmpty()) throw new Exception("La nueva clave no puede estar vacía.");
        if (!claveNueva.equals(claveConfirmar)) throw new Exception("La nueva clave y su confirmación no coinciden.");
        usuario.setClave(claveNueva);
        data.guardarFuncionarios();
    }

    // --- Reservas ---

    public List<Recurso> asignarRecursosDisponibles(List<Categoria> categorias, LocalDate fecha,
                                                     LocalTime inicio, LocalTime fin) throws Exception {
        List<Recurso> asignados = new ArrayList<>();
        List<String> noDisponibles = new ArrayList<>();

        for (Categoria cat : categorias) {
            Recurso encontrado = null;
            for (Recurso r : data.getRecursos()) {
                if (asignados.stream().anyMatch(a -> a.getId().equals(r.getId()))) continue;
                if (r.getCategoria() == null || !r.getCategoria().getId().equals(cat.getId())) continue;
                boolean libre = true;
                for (Reserva res : data.getReservas()) {
                    if (res.isActiva() && fecha.equals(res.getFecha())) {
                        boolean solapa = inicio.isBefore(res.getHoraFin()) && res.getHoraInicio().isBefore(fin);
                        if (solapa && res.usaRecurso(r.getId())) {
                            libre = false;
                            break;
                        }
                    }
                }
                if (libre) { encontrado = r; break; }
            }
            if (encontrado != null) asignados.add(encontrado);
            else noDisponibles.add(cat.getEtiqueta());
        }

        if (!noDisponibles.isEmpty())
            throw new Exception("Sin disponibilidad para las categorías: " + String.join(", ", noDisponibles));

        return asignados;
    }

    public void guardarReserva(Reserva reserva) throws Exception {
        List<Reserva> lista = data.getReservas();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId().equals(reserva.getId())) {
                lista.set(i, reserva);
                data.guardarReservas();
                return;
            }
        }
        lista.add(reserva);
        data.guardarReservas();
    }

    public boolean cancelarReserva(String id) throws Exception {
        Reserva reserva = data.getReservas().stream()
                .filter(r -> r.getId().equals(id))
                .findFirst().orElse(null);
        if (reserva == null) return false;
        if (!reserva.isActiva()) throw new Exception("La reserva ya no está activa.");
        if (!reserva.esFutura()) throw new Exception("Solo se pueden cancelar reservas futuras.");
        reserva.setEstado("CANCELADA");
        data.guardarReservas();
        return true;
    }

    // --- Listas ---

    public List<Funcionario> getFuncionarios() { return data.getFuncionarios(); }
    public List<Categoria> getCategorias() { return data.getCategorias(); }
    public List<Recurso> getRecursos() { return data.getRecursos(); }
    public List<Reserva> getReservas() { return data.getReservas(); }
}