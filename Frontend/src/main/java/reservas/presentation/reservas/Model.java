package reservas.presentation.reservas;

import reservas.logic.Reserva;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private final TableModel tabla = new TableModel();

    public TableModel getTabla() { return tabla; }

    public void mostrar(List<Reserva> reservas) {
        tabla.setFilas(reservas);
    }

    public List<Reserva> propias(List<Reserva> reservas, String funcionarioId) {
        List<Reserva> propias = new ArrayList<>();
        for (Reserva reserva : reservas) {
            if (reserva.getSolicitante() != null && funcionarioId.equals(reserva.getSolicitante().getId())) {
                propias.add(reserva);
            }
        }
        return propias;
    }
}
