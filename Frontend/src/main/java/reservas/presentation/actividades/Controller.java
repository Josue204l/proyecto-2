package reservas.presentation.actividades;

import reservas.client.Service;
import reservas.presentation.ReportePdf;
import reservas.presentation.Ui;

import javax.swing.JTable;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private final View view;
    private final TableModel tabla = new TableModel();
    private final Service service;

    public Controller(View view, Service service) {
        this.view = view;
        this.service = service;
        view.setController(this);
        view.getTabla().setModel(tabla);
        mostrar();
    }

    public void mostrar() {
        try {
            tabla.setDatos(Ui.leerFecha(view.getFecha()), service.listarReservas());
            view.setTitulo(tabla.etiquetaSemana());
        } catch (Exception ex) {
            Ui.error(view, ex);
        }
    }

    public void refrescar() { mostrar(); }

    public void pdf() {
        JTable grilla = view.getTabla();
        List<String> lineas = new ArrayList<>();
        StringBuilder encabezado = new StringBuilder();
        for (int c = 0; c < grilla.getColumnCount(); c++) {
            if (c > 0) encabezado.append(" | ");
            encabezado.append(grilla.getColumnName(c));
        }
        lineas.add(encabezado.toString());
        for (int f = 0; f < grilla.getRowCount(); f++) {
            StringBuilder fila = new StringBuilder();
            for (int c = 0; c < grilla.getColumnCount(); c++) {
                if (c > 0) fila.append(" | ");
                Object valor = grilla.getValueAt(f, c);
                fila.append(valor == null ? "" : valor);
            }
            lineas.add(fila.toString());
        }
        ReportePdf.guardarYAbrir(view, "actividades.pdf", tabla.etiquetaSemana(), lineas);
    }
}
