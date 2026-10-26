package Interfaz.estadisticas;

import com.github.lgooddatepicker.components.DatePicker;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

// Imports de iText 7
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

import utils.PDFGenerator;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class ControllerEstadisticas {

    private final estadisticasView view;
    private final ModelEstadisticas model;

    public ControllerEstadisticas(estadisticasView view, ModelEstadisticas model) {
        this.view = view;
        this.model = model;
        if (view != null) {
            view.setController(this);

            // Establecer fechas por defecto si están vacías (primer día del año actual hasta hoy)
            if (view.getDpRecursosDesde().getDate() == null) {
                view.getDpRecursosDesde().setDate(LocalDate.now().withDayOfYear(1));
            }
            if (view.getDpRecursosHasta().getDate() == null) {
                view.getDpRecursosHasta().setDate(LocalDate.now());
            }
            if (view.getDpActividadesDesde().getDate() == null) {
                view.getDpActividadesDesde().setDate(LocalDate.now().withDayOfYear(1));
            }
            if (view.getDpActividadesHasta().getDate() == null) {
                view.getDpActividadesHasta().setDate(LocalDate.now());
            }
        }
    }

    public ControllerEstadisticas(ModelEstadisticas model) {
        this(null, model);
    }

    public void cargarDatos() {
        cargarRecursos();
        cargarActividades();
    }

    public void cargarRecursos() {
        if (view == null) return;
        try {
            LocalDate[] rango = leerRango(view.getDpRecursosDesde(), view.getDpRecursosHasta());
            List<EstadisticaFila> filas = model.calcularRecursos(rango[0], rango[1]);

            if (view.getTableRecursos() != null) {
                view.getTableRecursos().setModel(model.getTableRecursos());
            }

            pintarGrafico(view.getChartRecursos(), "Recursos reservados", filas);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cargarActividades() {
        if (view == null) return;
        try {
            LocalDate[] rango = leerRango(view.getDpActividadesDesde(), view.getDpActividadesHasta());
            List<EstadisticaFila> filas = model.calcularActividades(rango[0], rango[1]);

            if (view.getTableActividades() != null) {
                view.getTableActividades().setModel(model.getTableActividades());
            }

            pintarGrafico(view.getChartActividades(), "Actividades por semana", filas);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void print() {
        printRecursos();
    }

    public void printRecursos() {
        if (view == null) return;
        try {
            LocalDate[] rango = leerRango(view.getDpRecursosDesde(), view.getDpRecursosHasta());
            String dest = elegirArchivo("Reporte_Recursos.pdf");
            if (dest == null) return;

            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("REPORTE DE RECURSOS RESERVADOS").setBold().setFontSize(16));
            document.add(new Paragraph("Período: " + rango[0] + " a " + rango[1]));
            document.add(new Paragraph("\n"));

            List<EstadisticaFila> filas = model.calcularRecursos(rango[0], rango[1]);
            Table tabla = new Table(UnitValue.createPercentArray(new float[]{70, 30}));
            tabla.useAllAvailableWidth();
            tabla.addHeaderCell(PDFGenerator.getCell(new Paragraph("Categoría").setBold(), 0, true));
            tabla.addHeaderCell(PDFGenerator.getCell(new Paragraph("Cantidad").setBold(), 0, true));
            for (EstadisticaFila f : filas) {
                tabla.addCell(PDFGenerator.getCell(new Paragraph(f.getEtiqueta()), 0, true));
                tabla.addCell(PDFGenerator.getCell(new Paragraph(String.valueOf(f.getCantidad())), 0, true));
            }
            document.add(tabla);
            document.close();
            PDFGenerator.openPdf(dest);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), "Error al generar PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void printActividades() {
        if (view == null) return;
        try {
            LocalDate[] rango = leerRango(view.getDpActividadesDesde(), view.getDpActividadesHasta());
            String dest = elegirArchivo("Reporte_Actividades.pdf");
            if (dest == null) return;

            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("REPORTE DE ACTIVIDADES POR SEMANA").setBold().setFontSize(16));
            document.add(new Paragraph("Período: " + rango[0] + " a " + rango[1]));
            document.add(new Paragraph("\n"));

            List<EstadisticaFila> filas = model.calcularActividades(rango[0], rango[1]);
            Table tabla = new Table(UnitValue.createPercentArray(new float[]{70, 30}));
            tabla.useAllAvailableWidth();
            tabla.addHeaderCell(PDFGenerator.getCell(new Paragraph("Semana").setBold(), 0, true));
            tabla.addHeaderCell(PDFGenerator.getCell(new Paragraph("Cantidad").setBold(), 0, true));
            for (EstadisticaFila f : filas) {
                tabla.addCell(PDFGenerator.getCell(new Paragraph(f.getEtiqueta()), 0, true));
                tabla.addCell(PDFGenerator.getCell(new Paragraph(String.valueOf(f.getCantidad())), 0, true));
            }
            document.add(tabla);
            document.close();
            PDFGenerator.openPdf(dest);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), "Error al generar PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String elegirArchivo(String nombreDefault) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Guardar Reporte PDF");
        fc.setSelectedFile(new File(nombreDefault));
        if (fc.showSaveDialog(view.getMainPanel()) != JFileChooser.APPROVE_OPTION) return null;
        String dest = fc.getSelectedFile().getAbsolutePath();
        return dest.toLowerCase().endsWith(".pdf") ? dest : dest + ".pdf";
    }

    private LocalDate[] leerRango(DatePicker desdeCampo, DatePicker hastaCampo) throws Exception {
        LocalDate desde = desdeCampo != null ? desdeCampo.getDate() : null;
        LocalDate hasta = hastaCampo != null ? hastaCampo.getDate() : null;
        if (desde == null || hasta == null) {
            throw new Exception("Seleccione las fechas desde y hasta.");
        }
        if (hasta.isBefore(desde)) {
            throw new Exception("La fecha 'hasta' no puede ser anterior a 'desde'.");
        }
        return new LocalDate[]{desde, hasta};
    }

    private void pintarGrafico(JPanel contenedor, String titulo, List<EstadisticaFila> filas) {
        if (contenedor == null || filas == null) return;

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (EstadisticaFila f : filas) {
            dataset.addValue(f.getCantidad(), "Cantidad", f.getEtiqueta());
        }

        JFreeChart chart = ChartFactory.createBarChart(titulo, "Categoría / Semana", "Cantidad", dataset, PlotOrientation.VERTICAL, false, true, false);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(350, 220));

        contenedor.removeAll();
        contenedor.setLayout(new BorderLayout());
        contenedor.add(chartPanel, BorderLayout.CENTER);
        contenedor.revalidate();
        contenedor.repaint();
    }

    public ModelEstadisticas getModel() {
        return model;
    }
}