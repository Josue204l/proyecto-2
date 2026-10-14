package Interfaz.categorias;

import utils.PDFGenerator;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import logic.Categoria;

import javax.swing.*;

public class ControllerCategoria {

    private final categoriasView view;
    private final ModelCategoria model;

    public ControllerCategoria(categoriasView view, ModelCategoria model) {
        this.view = view;
        this.model = model;
        if (view != null) view.setController(this);
        actualizarTabla(model.getCategorias());
    }

    public ControllerCategoria(ModelCategoria model) {
        this(null, model);
    }

    public void guardar() {
        try {
            Categoria categoria = new Categoria(
                    texto(view.getTxtId()),
                    texto(view.getTxtDescripcion()),
                    texto(view.getTxtDescripcion())
            );
            model.guardar(categoria);
            actualizarTabla(model.getCategorias());
            limpiar();
            JOptionPane.showMessageDialog(view.getMainPanel(), "Categoría guardada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void eliminar() {
        String id = texto(view.getTxtId());
        if (id.isEmpty() && view.getTable() != null && view.getTable().getSelectedRow() >= 0) {
            id = String.valueOf(view.getTable().getValueAt(view.getTable().getSelectedRow(), 0));
        }
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(view.getMainPanel(), "Seleccione una categoría.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(view.getMainPanel(),
                "¿Borrar la categoría " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            model.eliminar(id);
            actualizarTabla(model.getCategorias());
            limpiar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void buscar() {
        actualizarTabla(model.buscar(texto(view.getTxtBuscar())));
    }

    public void cargarSeleccionado() {
        int fila = view.getTable().getSelectedRow();
        Categoria c = model.getTableModel().getRowAt(fila);
        if (c == null) return;
        view.getTxtId().setText(c.getId());
        view.getTxtDescripcion().setText(c.getEtiqueta());
    }

    public void limpiar() {
        if (view.getTxtId() != null) view.getTxtId().setText("");
        if (view.getTxtDescripcion() != null) view.getTxtDescripcion().setText("");
        if (view.getTable() != null) view.getTable().clearSelection();
    }

    private void actualizarTabla(java.util.List<Categoria> lista) {
        if (view != null && view.getTable() != null) {
            model.mostrar(lista);
            view.getTable().setModel(model.getTableModel());
        }
    }

    private String texto(JTextField campo) {
        return campo == null || campo.getText() == null ? "" : campo.getText().trim();
    }

    public void print() {
        try {
            String dest = "categorias.pdf";
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            document.add(new Paragraph("Listado de Categorías"));
            JTable tabla = view.getTable();
            if (tabla != null && tabla.getColumnCount() > 0) {
                Table table = new Table(tabla.getColumnCount());
                for (int c = 0; c < tabla.getColumnCount(); c++) {
                    table.addHeaderCell(PDFGenerator.getCell(new Paragraph(String.valueOf(tabla.getColumnName(c))), 1, true));
                }
                for (int r = 0; r < tabla.getRowCount(); r++) {
                    for (int c = 0; c < tabla.getColumnCount(); c++) {
                        Object val = tabla.getValueAt(r, c);
                        table.addCell(PDFGenerator.getCell(new Paragraph(val == null ? "" : val.toString()), 0, true));
                    }
                }
                document.add(table);
            }
            document.close();
            PDFGenerator.openPdf(dest);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), "No se pudo generar el PDF: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public ModelCategoria getModel() {
        return model;
    }
}
