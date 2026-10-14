package backend;

import backend.datos.ConfiguracionBaseDatos;
import backend.datos.ConexionMySql;
import backend.logica.DespachadorOperaciones;
import backend.logica.ServiceBackend;
import backend.red.ServidorSocket;

public class MainBackend {
    public static void main(String[] args) {
        ConfiguracionBaseDatos config = ConfiguracionBaseDatos.desdeEntorno();
        ConexionMySql conexion = new ConexionMySql(config);
        ServiceBackend service = new ServiceBackend(conexion);
        DespachadorOperaciones despachador = new DespachadorOperaciones(service);

        System.out.println("Iniciando Backend en puerto " + config.puertoServidor() + "...");
        try (ServidorSocket servidor = new ServidorSocket(config.puertoServidor(), despachador)) {
            servidor.iniciar();
        } catch (Exception e) {
            System.err.println("Error fatal en el Backend: " + e.getMessage());
        }
    }
}
