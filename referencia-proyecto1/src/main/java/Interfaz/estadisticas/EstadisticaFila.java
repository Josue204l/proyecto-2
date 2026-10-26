package Interfaz.estadisticas;

public class EstadisticaFila {
    private final String etiqueta;
    private final int cantidad;

    public EstadisticaFila(String etiqueta, int cantidad) {
        this.etiqueta = etiqueta;
        this.cantidad = cantidad;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public int getCantidad() {
        return cantidad;
    }
}
