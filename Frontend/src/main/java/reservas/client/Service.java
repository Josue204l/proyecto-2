package reservas.client;

import reservas.logic.Categoria;
import reservas.logic.Funcionario;
import reservas.logic.IService;
import reservas.logic.Protocol;
import reservas.logic.Recurso;
import reservas.logic.Reserva;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Service implements IService {
    private final Socket s;
    private final ObjectOutputStream os;
    private final ObjectInputStream is;
    private final String sid;

    public Service(String host, int puerto) throws Exception {
        s = new Socket(host, puerto);
        os = new ObjectOutputStream(s.getOutputStream());
        os.flush();
        is = new ObjectInputStream(s.getInputStream());
        os.writeInt(Protocol.SYNC);
        os.flush();
        Object recibido = is.readObject();
        if (!(recibido instanceof String id) || id.isBlank()) {
            throw new Exception("Dato de red inválido: el servidor no envió el id de sesión.");
        }
        sid = id;
    }

    public String getSid() { return sid; }

    @Override
    public Funcionario login(String id, String clave) throws Exception {
        os.writeInt(Protocol.LOGIN);
        os.writeObject(id);
        os.writeObject(clave);
        os.flush();
        return leer(Funcionario.class, "funcionario");
    }

    @Override
    public void cambiarClave(String claveActual, String claveNueva, String claveConfirmar) throws Exception {
        os.writeInt(Protocol.CAMBIAR_CLAVE);
        os.writeObject(claveActual);
        os.writeObject(claveNueva);
        os.writeObject(claveConfirmar);
        os.flush();
        estado();
    }

    @Override
    public List<Funcionario> listarFuncionarios() throws Exception {
        os.writeInt(Protocol.FUNCIONARIO_LISTAR);
        os.flush();
        return leerLista(Funcionario.class, "funcionarios");
    }

    @Override
    public void guardarFuncionario(Funcionario funcionario) throws Exception {
        os.writeInt(Protocol.FUNCIONARIO_GUARDAR);
        os.writeObject(funcionario);
        os.flush();
        estado();
    }

    @Override
    public void eliminarFuncionario(String id) throws Exception {
        os.writeInt(Protocol.FUNCIONARIO_ELIMINAR);
        os.writeObject(id);
        os.flush();
        estado();
    }

    @Override
    public List<Categoria> listarCategorias() throws Exception {
        os.writeInt(Protocol.CATEGORIA_LISTAR);
        os.flush();
        return leerLista(Categoria.class, "categorías");
    }

    @Override
    public void guardarCategoria(Categoria categoria) throws Exception {
        os.writeInt(Protocol.CATEGORIA_GUARDAR);
        os.writeObject(categoria);
        os.flush();
        estado();
    }

    @Override
    public void eliminarCategoria(String id) throws Exception {
        os.writeInt(Protocol.CATEGORIA_ELIMINAR);
        os.writeObject(id);
        os.flush();
        estado();
    }

    @Override
    public List<Recurso> listarRecursos() throws Exception {
        os.writeInt(Protocol.RECURSO_LISTAR);
        os.flush();
        return leerLista(Recurso.class, "recursos");
    }

    @Override
    public void guardarRecurso(Recurso recurso) throws Exception {
        os.writeInt(Protocol.RECURSO_GUARDAR);
        os.writeObject(recurso);
        os.flush();
        estado();
    }

    @Override
    public void eliminarRecurso(String id) throws Exception {
        os.writeInt(Protocol.RECURSO_ELIMINAR);
        os.writeObject(id);
        os.flush();
        estado();
    }

    @Override
    public List<Reserva> listarReservas() throws Exception {
        os.writeInt(Protocol.RESERVA_LISTAR);
        os.flush();
        return leerLista(Reserva.class, "reservas");
    }

    @Override
    public Reserva crearReserva(String titulo, LocalDate fecha, LocalTime inicio, LocalTime fin,
                                List<String> idsCategorias) throws Exception {
        os.writeInt(Protocol.RESERVA_CREAR);
        os.writeObject(titulo);
        os.writeObject(fecha);
        os.writeObject(inicio);
        os.writeObject(fin);
        os.writeObject(new ArrayList<>(idsCategorias));
        os.flush();
        return leer(Reserva.class, "reserva");
    }

    @Override
    public void cancelarReserva(String id) throws Exception {
        os.writeInt(Protocol.RESERVA_CANCELAR);
        os.writeObject(id);
        os.flush();
        estado();
    }

    @Override
    public void desconectar() throws Exception {
        try {
            os.writeInt(Protocol.DISCONNECT);
            os.flush();
        } finally {
            s.close();
        }
    }

    private void estado() throws Exception {
        int codigo = is.readInt();
        if (codigo == Protocol.ERROR_ERROR) {
            Object mensaje = is.readObject();
            throw new Exception(mensaje instanceof String texto ? texto : "Error.");
        }
        if (codigo != Protocol.ERROR_NO_ERROR) {
            throw new Exception("Dato de red inválido: estado desconocido.");
        }
    }

    private <T> T leer(Class<T> clase, String campo) throws Exception {
        estado();
        Object dato = is.readObject();
        if (!clase.isInstance(dato)) {
            throw new Exception("Dato de red inválido: " + campo + " no tiene el tipo esperado.");
        }
        return clase.cast(dato);
    }

    private <T> List<T> leerLista(Class<T> clase, String campo) throws Exception {
        estado();
        Object dato = is.readObject();
        if (!(dato instanceof List<?> lista)) {
            throw new Exception("Dato de red inválido: se esperaba la lista de " + campo + ".");
        }
        List<T> resultado = new ArrayList<>();
        for (Object item : lista) {
            if (!clase.isInstance(item)) {
                throw new Exception("Dato de red inválido: un elemento de " + campo + " no tiene el tipo esperado.");
            }
            resultado.add(clase.cast(item));
        }
        return resultado;
    }
}
