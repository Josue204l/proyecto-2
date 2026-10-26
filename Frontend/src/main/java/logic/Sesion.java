package logic;

public class Sesion {
    private static Usuario usuario;

    public static Usuario getUsuario() {
        return usuario;
    }

    public static void setUsuario(Usuario usuario) {
        Sesion.usuario = usuario;
    }

    public static void logout() {
        Sesion.usuario = null;
    }

    public static boolean isLoggedIn() {
        return usuario != null;
    }
}