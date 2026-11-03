package reservas.server;

import reservas.logic.Categoria;
import reservas.logic.Funcionario;
import reservas.logic.Protocol;
import reservas.logic.Recurso;
import reservas.logic.Reserva;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Worker {
    final String sid;
    private final Server srv;
    private final Socket s;
    private final ObjectOutputStream os;
    private final ObjectInputStream is;
    private final Service service;
    private Socket as;
    private ObjectOutputStream aos;
    private volatile boolean activo = true;
    private String usuarioId;
    private String rol;

    public Worker(Server srv, Socket s, ObjectOutputStream os, ObjectInputStream is, String sid, Service service) {
        this.srv = srv;
        this.s = s;
        this.os = os;
        this.is = is;
        this.sid = sid;
        this.service = service;
    }

    public void setAs(Socket as, ObjectOutputStream aos, ObjectInputStream ais) {
        this.as = as;
        this.aos = aos;
    }

    public void start() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                listen();
            }
        });
        activo = true;
        t.start();
    }

    public void detener() {
        activo = false;
        cerrar(s);
        cerrar(as);
    }

    public void listen() {
        while (activo) {
            try {
                int metodo = is.readInt();
                try {
                    atender(metodo);
                } catch (IOException ex) {
                    throw ex;
                } catch (Exception ex) {
                    error(ex.getMessage() == null ? "No se pudo completar la operación." : ex.getMessage());
                }
                os.flush();
            } catch (IOException ex) {
                detener();
                srv.remove(this);
            }
        }
    }

    private void atender(int metodo) throws Exception {
        switch (metodo) {
            case Protocol.LOGIN:
                String id = ValidadorRed.texto(is.readObject(), "id");
                String clave = ValidadorRed.texto(is.readObject(), "clave");
                Funcionario funcionario = service.login(id, clave);
                this.usuarioId = funcionario.getId();
                this.rol = funcionario.getRol();
                ok(funcionario);
                break;
            case Protocol.CAMBIAR_CLAVE:
                String actual = ValidadorRed.texto(is.readObject(), "clave actual");
                String nueva = ValidadorRed.texto(is.readObject(), "clave nueva");
                String confirmar = ValidadorRed.texto(is.readObject(), "confirmación");
                enSesion(() -> service.cambiarClave(actual, nueva, confirmar));
                ok();
                break;
            case Protocol.FUNCIONARIO_LISTAR:
                ok(enSesionValor(() -> service.listarFuncionarios()));
                break;
            case Protocol.FUNCIONARIO_GUARDAR:
                Funcionario entrante = ValidadorRed.tipo(is.readObject(), Funcionario.class, "funcionario");
                enSesion(() -> service.guardarFuncionario(entrante));
                ok();
                break;
            case Protocol.FUNCIONARIO_ELIMINAR:
                String idFuncionario = ValidadorRed.texto(is.readObject(), "id del funcionario");
                enSesion(() -> service.eliminarFuncionario(idFuncionario));
                ok();
                break;
            case Protocol.CATEGORIA_LISTAR:
                ok(enSesionValor(() -> service.listarCategorias()));
                break;
            case Protocol.CATEGORIA_GUARDAR:
                Categoria categoria = ValidadorRed.tipo(is.readObject(), Categoria.class, "categoría");
                enSesion(() -> service.guardarCategoria(categoria));
                ok();
                break;
            case Protocol.CATEGORIA_ELIMINAR:
                String idCategoria = ValidadorRed.texto(is.readObject(), "id de la categoría");
                enSesion(() -> service.eliminarCategoria(idCategoria));
                ok();
                break;
            case Protocol.RECURSO_LISTAR:
                ok(enSesionValor(() -> service.listarRecursos()));
                break;
            case Protocol.RECURSO_GUARDAR:
                Recurso recurso = ValidadorRed.tipo(is.readObject(), Recurso.class, "recurso");
                enSesion(() -> service.guardarRecurso(recurso));
                ok();
                break;
            case Protocol.RECURSO_ELIMINAR:
                String idRecurso = ValidadorRed.texto(is.readObject(), "id del recurso");
                enSesion(() -> service.eliminarRecurso(idRecurso));
                ok();
                break;
            case Protocol.RESERVA_LISTAR:
                ok(enSesionValor(() -> service.listarReservas()));
                break;
            case Protocol.RESERVA_CREAR:
                String titulo = ValidadorRed.texto(is.readObject(), "título");
                LocalDate fecha = ValidadorRed.fecha(is.readObject(), "fecha");
                LocalTime inicio = ValidadorRed.hora(is.readObject(), "hora de inicio");
                LocalTime fin = ValidadorRed.hora(is.readObject(), "hora de fin");
                List<String> categorias = ValidadorRed.ids(is.readObject(), "categorías");
                Reserva creada = enSesionValor(() -> service.crearReserva(titulo, fecha, inicio, fin, categorias));
                srv.deliver_reserva(this, creada);
                ok(creada);
                break;
            case Protocol.RESERVA_CANCELAR:
                String idReserva = ValidadorRed.texto(is.readObject(), "id de la reserva");
                enSesion(() -> service.cancelarReserva(idReserva));
                ok();
                break;
            case Protocol.DISCONNECT:
                detener();
                srv.remove(this);
                break;
            default:
                throw new Exception("Operación desconocida.");
        }
    }

    private void enSesion(Service.Accion accion) throws Exception {
        if (usuarioId == null) throw new Exception("Debe iniciar sesión antes de realizar operaciones.");
        service.enSesion(usuarioId, rol, accion);
    }

    private <T> T enSesionValor(Valor<T> accion) throws Exception {
        if (usuarioId == null) throw new Exception("Debe iniciar sesión antes de realizar operaciones.");
        final Object[] caja = new Object[1];
        service.enSesion(usuarioId, rol, () -> caja[0] = accion.obtener());
        @SuppressWarnings("unchecked")
        T valor = (T) caja[0];
        return valor;
    }

    public synchronized void deliver_reserva(Reserva reserva) {
        if (aos == null || !activo) return;
        try {
            aos.writeInt(Protocol.DELIVER_RESERVA);
            aos.writeObject(reserva);
            aos.reset();
            aos.flush();
        } catch (IOException ex) {
            detener();
        }
    }

    private void ok() throws IOException {
        os.writeInt(Protocol.ERROR_NO_ERROR);
    }

    private void ok(Object dato) throws IOException {
        os.writeInt(Protocol.ERROR_NO_ERROR);
        os.writeObject(dato);
        os.reset();
    }

    private void error(String mensaje) throws IOException {
        os.writeInt(Protocol.ERROR_ERROR);
        os.writeObject(mensaje);
        os.reset();
    }

    private static void cerrar(Socket socket) {
        if (socket == null) return;
        try {
            socket.close();
        } catch (IOException ignored) { }
    }

    private interface Valor<T> {
        T obtener() throws Exception;
    }
}
