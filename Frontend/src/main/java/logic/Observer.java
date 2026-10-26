package logic;

public interface Observer {
    /**
     * Notifica a la vista o componente registrado para que actualice
     * sus tablas, controles o interfaz gráfica en tiempo real.
     */
    void update();
}