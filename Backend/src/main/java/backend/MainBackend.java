package backend;

import backend.datos.ConfiguracionBaseDatos;
import backend.datos.Database;
import backend.logica.DespachadorOperaciones;
import backend.logica.ServiceBackend;
import backend.red.ServidorTCP;

public class MainBackend {

    public static void main(String[] args) {
        ConfiguracionBaseDatos config      = ConfiguracionBaseDatos.desdeEntorno();
        Database               conexion    = new Database(config);
        try {
            conexion.verificarConexion();
        } catch (Exception e) {
            System.err.println("No se pudo conectar a MySQL: " + e.getMessage());
            return;
        }
        ServiceBackend         service     = new ServiceBackend(conexion);
        ServidorTCP            servidor    = new ServidorTCP(config.puertoServidor());
        DespachadorOperaciones despachador = new DespachadorOperaciones(service, servidor);
        servidor.setManejador(despachador);
        Runtime.getRuntime().addShutdownHook(new Thread(servidor::detener, "detener-backend"));

        System.out.println("Backend iniciando en puerto " + config.puertoServidor() + "...");
        try {
            servidor.iniciar();
        } catch (Exception e) {
            System.err.println("Error fatal: " + e.getMessage());
        }
    }
}
