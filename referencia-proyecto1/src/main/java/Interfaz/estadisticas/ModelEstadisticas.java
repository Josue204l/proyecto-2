package Interfaz.estadisticas;

import data.Data;
import logic.Categoria;
import logic.Reserva;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

public class ModelEstadisticas {

    public static final String LISTA = "lista";

    private TableModelEstadisticas tableModelRecursos;
    private TableModelEstadisticas tableModelActividades;
    private final PropertyChangeSupport propertyChangeSupport;

    public ModelEstadisticas() {
        this.tableModelRecursos = new TableModelEstadisticas(new ArrayList<>());
        this.tableModelActividades = new TableModelEstadisticas(new ArrayList<>());
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    // --- GETTERS DE TABLAS PARA EL CONTROLADOR ---
    public TableModelEstadisticas getTableRecursos() {
        return tableModelRecursos;
    }

    public TableModelEstadisticas getTableActividades() {
        return tableModelActividades;
    }

    /**
     * Calcula los recursos/categorías usadas y actualiza su respectiva tabla.
     */
    public List<EstadisticaFila> calcularRecursos(LocalDate desde, LocalDate hasta) {
        Map<String, Integer> conteo = new LinkedHashMap<>();

        for (Reserva r : Data.getInstancia().getReservas()) {
            if ("CANCELADA".equalsIgnoreCase(r.getEstado())) continue;
            if (r.getFecha() == null) continue;
            if (desde != null && r.getFecha().isBefore(desde)) continue;
            if (hasta != null && r.getFecha().isAfter(hasta)) continue;

            if (r.getCategoriasRequeridas() != null) {
                for (Categoria c : r.getCategoriasRequeridas()) {
                    String nombre = c.getNombre() != null ? c.getNombre() : c.getId();
                    conteo.put(nombre, conteo.getOrDefault(nombre, 0) + 1);
                }
            }
        }

        // Crear filas para el gráfico
        List<EstadisticaFila> resultado = new ArrayList<>();
        conteo.forEach((etiqueta, cantidad) -> resultado.add(new EstadisticaFila(etiqueta, cantidad)));

        // Actualizar la JTable de Recursos
        List<String[]> filasTabla = conteo.entrySet().stream().map(e -> new String[]{e.getKey(), String.valueOf(e.getValue())}).collect(Collectors.toList());
        tableModelRecursos.setFilasGenericas(filasTabla, new String[]{"Categoría", "Cantidad de Reservas"});

        propertyChangeSupport.firePropertyChange(LISTA, null, resultado);
        return resultado;
    }

    /**
     * Calcula las actividades por semana y actualiza su respectiva tabla.
     */
    public List<EstadisticaFila> calcularActividades(LocalDate desde, LocalDate hasta) {
        Map<String, Integer> conteo = new LinkedHashMap<>();
        WeekFields wf = WeekFields.of(Locale.getDefault());

        // Pre-poblar todas las semanas del período con 0
        if (desde != null && hasta != null) {
            LocalDate cursor = desde;
            while (!cursor.isAfter(hasta)) {
                int semana = cursor.get(wf.weekOfWeekBasedYear());
                int anio = cursor.get(wf.weekBasedYear());
                String clave = "Semana " + semana + " (" + anio + ")";
                conteo.putIfAbsent(clave, 0);
                cursor = cursor.plusWeeks(1);
            }
        }

        for (Reserva r : Data.getInstancia().getReservas()) {
            if ("CANCELADA".equalsIgnoreCase(r.getEstado())) continue;
            if (r.getFecha() == null) continue;
            if (desde != null && r.getFecha().isBefore(desde)) continue;
            if (hasta != null && r.getFecha().isAfter(hasta)) continue;

            int semana = r.getFecha().get(wf.weekOfWeekBasedYear());
            int anio = r.getFecha().get(wf.weekBasedYear());
            String clave = "Semana " + semana + " (" + anio + ")";
            conteo.put(clave, conteo.getOrDefault(clave, 0) + 1);
        }

        List<EstadisticaFila> resultado = new ArrayList<>();
        conteo.forEach((etiqueta, cantidad) -> resultado.add(new EstadisticaFila(etiqueta, cantidad)));

        List<String[]> filasTabla = conteo.entrySet().stream()
                .map(e -> new String[]{e.getKey(), String.valueOf(e.getValue())})
                .collect(Collectors.toList());
        tableModelActividades.setFilasGenericas(filasTabla, new String[]{"Semana", "Cantidad de Actividades"});

        propertyChangeSupport.firePropertyChange(LISTA, null, resultado);
        return resultado;
    }
}