package reservas.presentation.estadisticas;

import reservas.logic.Categoria;
import reservas.logic.Recurso;
import reservas.logic.Reserva;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Model {
    public static class Fila {
        public final String etiqueta;
        public final int cantidad;

        public Fila(String etiqueta, int cantidad) {
            this.etiqueta = etiqueta;
            this.cantidad = cantidad;
        }
    }

    public List<Fila> recursos(List<Reserva> reservas, LocalDate desde, LocalDate hasta) {
        Map<String, Integer> conteo = new LinkedHashMap<>();
        for (Reserva reserva : reservas) {
            if (!cuenta(reserva, desde, hasta)) continue;
            for (Recurso recurso : reserva.getRecursosAsignados()) {
                Categoria categoria = recurso.getCategoria();
                String nombre = categoria == null ? recurso.getId() : categoria.getEtiqueta();
                conteo.put(nombre, conteo.getOrDefault(nombre, 0) + 1);
            }
        }
        return filas(conteo);
    }

    public List<Fila> actividades(List<Reserva> reservas, LocalDate desde, LocalDate hasta) {
        Map<String, Integer> conteo = new LinkedHashMap<>();
        WeekFields semanas = WeekFields.ISO;
        if (desde != null && hasta != null) {
            LocalDate cursor = desde;
            while (!cursor.isAfter(hasta)) {
                conteo.putIfAbsent(clave(cursor, semanas), 0);
                cursor = cursor.plusDays(1);
            }
        }
        for (Reserva reserva : reservas) {
            if (!cuenta(reserva, desde, hasta)) continue;
            String clave = clave(reserva.getFecha(), semanas);
            conteo.put(clave, conteo.getOrDefault(clave, 0) + 1);
        }
        return filas(conteo);
    }

    private static boolean cuenta(Reserva reserva, LocalDate desde, LocalDate hasta) {
        if (reserva == null || !reserva.isActiva() || reserva.getFecha() == null) return false;
        if (desde != null && reserva.getFecha().isBefore(desde)) return false;
        return hasta == null || !reserva.getFecha().isAfter(hasta);
    }

    private static String clave(LocalDate fecha, WeekFields semanas) {
        int semana = fecha.get(semanas.weekOfWeekBasedYear());
        int anio = fecha.get(semanas.weekBasedYear());
        return "Semana " + semana + " (" + anio + ")";
    }

    private static List<Fila> filas(Map<String, Integer> conteo) {
        List<Fila> filas = new ArrayList<>();
        for (Map.Entry<String, Integer> entrada : conteo.entrySet()) {
            filas.add(new Fila(entrada.getKey(), entrada.getValue()));
        }
        return filas;
    }
}
