package backend.datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionMySql {
    private final ConfiguracionBaseDatos configuracion;

    public ConexionMySql(ConfiguracionBaseDatos configuracion) {
        this.configuracion = configuracion;
    }

    public Connection abrir() throws SQLException {
        return DriverManager.getConnection(configuracion.url(), configuracion.usuario(), configuracion.clave());
    }
}
