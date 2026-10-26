package backend.datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Acceso JDBC puro; vive únicamente en el proceso Backend. */
public final class Database {
    private final ConfiguracionBaseDatos configuracion;

    public Database(ConfiguracionBaseDatos configuracion) {
        this.configuracion = configuracion;
    }

    public Connection abrir() throws SQLException {
        return DriverManager.getConnection(configuracion.url(), configuracion.usuario(), configuracion.clave());
    }

    public void verificarConexion() throws SQLException {
        try (Connection ignored = abrir()) { }
    }
}
