package reservas.server;

public class Application {
    public static void main(String[] args) {
        try {
            Server server = new Server();
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                public void run() {
                    server.detener();
                }
            }));
            server.run();
        } catch (Exception ex) {
            System.out.println("No se pudo iniciar el servidor: " + ex.getMessage());
        }
    }
}
