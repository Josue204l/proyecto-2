package Interfaz.calendarizacion;

import utils.PDFGenerator;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import logic.Categoria;

import javax.swing.*;
import java.time.LocalDate;

public class ControllerCalendarizacion {

    private final ModelCalendarizacion model;
    private final calendarizacionView view;

    public ControllerCalendarizacion(calendarizacionView view, ModelCalendarizacion model) {
        this.view = view;
        this.model = model;
        this.view.setController(this);
        inicializar();
    }

    public ControllerCalendarizacion(calendarizacionView view, ModelCalendarizacion model, logic.Funcionario ignorado) {
        this(view, model);
    }

    private void inicializar() {
        view.cargarCategorias(model.getCategorias());
        if (view.getTblCalendarizacion() != null) {
            view.getTblCalendarizacion().setModel(model.getTableModel());
            view.getTblCalendarizacion().setRowHeight(28);
            view.getTblCalendarizacion().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }
    }

    public void filtrar() {
        LocalDate fecha = view.getDatePicker() != null ? view.getDatePicker().getDate() : null;
        Object seleccionCat = view.getCmbCategoria().getSelectedItem();
        Categoria categoriaSeleccionada = (seleccionCat instanceof Categoria) ? (Categoria) seleccionCat : null;

        if (fecha == null || categoriaSeleccionada == null) {
            JOptionPane.showMessageDialog(view.getMainPanel(),
                    "Seleccione una fecha y una categoría.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        model.cargarMatriz(fecha, categoriaSeleccionada);
        view.getTblCalendarizacion().setModel(model.getTableModel());
        ajustarAnchoColumnas();
    }

    public void print() {
        try {
            String dest = "calendarizacion.pdf";
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            document.add(new Paragraph("Calendarización de recursos"));
            JTable tabla = view.getTblCalendarizacion();
            agregarTabla(document, tabla);
            document.close();
            PDFGenerator.openPdf(dest);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), "No se pudo generar el PDF: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarTabla(Document document, JTable tabla) {
        if (tabla == null || tabla.getColumnCount() == 0) return;
        int cols = tabla.getColumnCount();
        Table table = new Table(cols);
        for (int c = 0; c < cols; c++) {
            table.addHeaderCell(PDFGenerator.getCell(new Paragraph(String.valueOf(tabla.getColumnName(c))), 1, true));
        }
        for (int r = 0; r < tabla.getRowCount(); r++) {
            for (int c = 0; c < cols; c++) {
                Object val = tabla.getValueAt(r, c);
                table.addCell(PDFGenerator.getCell(new Paragraph(val == null ? "" : val.toString()), 0, true));
            }
        }
        document.add(table);
    }

    private void ajustarAnchoColumnas() {
        JTable tabla = view.getTblCalendarizacion();
        if (tabla == null) return;
        tabla.getColumnModel().getColumn(0).setPreferredWidth(70);
        for (int i = 1; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(160);
        }
    }
}
