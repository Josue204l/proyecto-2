package backend.datos;

/** Lee configuración sin guardar credenciales dentro del código fuente. */
public record ConfiguracionBaseDatos(String url, String usuario, String clave, int puertoServidor) {
    public static ConfiguracionBaseDatos desdeEntorno() {
        String host = variable("DB_HOST", "localhost");
        String puertoDb = variable("DB_PORT", "3306");
        String esquema = variable("DB_NAME", "sistema_reservas");
        String url = variable("DB_URL", "jdbc:mysql://" + host + ":" + puertoDb + "/" + esquema
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Costa_Rica");
        return new ConfiguracionBaseDatos(url, variable("DB_USER", "root"), variable("DB_PASSWORD", ""),
                Integer.parseInt(variable("SERVER_PORT", "5050")));
    }

    private static String variable(String nombre, String predeterminado) {
        String valor = System.getenv(nombre);
        return valor == null || valor.isBlank() ? predeterminado : valor;
    }
}
