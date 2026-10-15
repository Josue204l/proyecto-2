package logic;

public class SessionManager {

    private static SessionManager instance;
    private Usuario usuarioLogueado;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public void setUsuarioLogueado(Usuario usuario) {
        this.usuarioLogueado = usuario;
    }

    public void logout() {
        this.usuarioLogueado = null;
    }

    public boolean estaAutenticado() {
        return usuarioLogueado != null;
    }

    public boolean esAdmin() {
        return usuarioLogueado != null && "ADMIN".equalsIgnoreCase(usuarioLogueado.getRol());
    }
}