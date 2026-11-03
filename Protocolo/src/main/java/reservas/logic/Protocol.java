package reservas.logic;

public class Protocol {
    public static final String SERVER = "localhost";
    public static final int PORT = 5050;

    public static final int SYNC = 10;
    public static final int ASYNC = 11;
    public static final int DISCONNECT = 12;

    public static final int ERROR_NO_ERROR = 0;
    public static final int ERROR_ERROR = 1;

    public static final int LOGIN = 100;
    public static final int CAMBIAR_CLAVE = 101;

    public static final int FUNCIONARIO_LISTAR = 200;
    public static final int FUNCIONARIO_GUARDAR = 201;
    public static final int FUNCIONARIO_ELIMINAR = 202;

    public static final int CATEGORIA_LISTAR = 300;
    public static final int CATEGORIA_GUARDAR = 301;
    public static final int CATEGORIA_ELIMINAR = 302;

    public static final int RECURSO_LISTAR = 310;
    public static final int RECURSO_GUARDAR = 311;
    public static final int RECURSO_ELIMINAR = 312;

    public static final int RESERVA_LISTAR = 400;
    public static final int RESERVA_CREAR = 401;
    public static final int RESERVA_CANCELAR = 402;

    public static final int DELIVER_RESERVA = 500;

    private Protocol() { }
}
