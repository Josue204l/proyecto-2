package Interfaz.recursos;

import utils.PDFGenerator;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import logic.Categoria;
import logic.Recurso;

import javax.swing.*;
import java.util.List;

public class ControllerRecurso {
    private final recursosView view;
    private final ModelRecurso model;

    public ControllerRecurso(recursosView view, ModelRecurso model) {
        this.view = view;
        this.model = model;
        cargarCombos();
        if (view != null) view.setController(this);
        actualizarTabla(model.getRecursos());
    }

    public ControllerRecurso(ModelRecurso model) {
        this(null, model);
    }

    @SuppressWarnings("unchecked")
    private void cargarCombos() {
        if (view == null) return;
        DefaultComboBoxModel<Categoria> filtro = new DefaultComboBoxModel<>();
        filtro.addElement(null);
        DefaultComboBoxModel<Categoria> form = new DefaultComboBoxModel<>();
        for (Categoria c : model.getCategorias()) {
            filtro.addElement(c);
            form.addElement(c);
        }
        if (view.getCmbFiltroCategoria() != null) {
            view.getCmbFiltroCategoria().setModel(filtro);
            view.getCmbFiltroCategoria().setRenderer(new DefaultListCellRenderer() {
                @Override
                public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    setText(value == null ? "Todas" : value.toString());
                    return this;
                }
            });
        }
        if (view.getCmbCategoria() != null) {
            view.getCmbCategoria().setModel(form);
        }
    }

    public void buscar() {
        Categoria cat = null;
        if (view.getCmbFiltroCategoria() != null && view.getCmbFiltroCategoria().getSelectedItem() instanceof Categoria c) {
            cat = c;
        }
        String desc = view.getTxtBuscar() != null ? view.getTxtBuscar().getText() : "";
        actualizarTabla(model.filtrar(cat, desc));
    }

    public void guardar() {
        try {
            String id = view.getTxtId() != null ? view.getTxtId().getText().trim() : "";
            String desc = view.getTxtDescripcion() != null ? view.getTxtDescripcion().getText().trim() : "";
            Categoria cat = null;
            if (view.getCmbCategoria() != null && view.getCmbCategoria().getSelectedItem() instanceof Categoria c) {
                cat = c;
            }
            Recurso recurso = new Recurso(id, desc, cat);
            if (recurso.getId() == null || recurso.getId().isBlank()) {
                throw new Exception("El ID o número de activo es obligatorio.");
            }
            if (recurso.getDescripcion() == null || recurso.getDescripcion().isBlank()) {
                throw new Exception("La descripción es obligatoria.");
            }
            if (recurso.getCategoria() == null) {
                throw new Exception("Debe seleccionar una categoría.");
            }
            model.guardar(recurso);
            actualizarTabla(model.getRecursos());
            limpiar();
            JOptionPane.showMessageDialog(view.getMainPanel(), "Recurso guardado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void eliminar() {
        String id = view.getTxtId() != null ? view.getTxtId().getText().trim() : "";
        if (id.isEmpty() && view.getTable() != null && view.getTable().getSelectedRow() >= 0) {
            id = String.valueOf(view.getTable().getValueAt(view.getTable().getSelectedRow(), 0));
        }
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(view.getMainPanel(), "Seleccione un recurso.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(view.getMainPanel(), "¿Borrar el recurso " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            model.eliminar(id);
            actualizarTabla(model.getRecursos());
            limpiar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cargarSeleccionado() {
        if (view.getTable() == null) return;
        int fila = view.getTable().getSelectedRow();
        Recurso r = model.getTableModel().getRowAt(fila);
        if (r == null) return;
        view.getTxtId().setText(r.getId());
        view.getTxtDescripcion().setText(r.getDescripcion());
        if (view.getCmbCategoria() != null && r.getCategoria() != null) {
            ComboBoxModel<?> combo = view.getCmbCategoria().getModel();
            for (int i = 0; i < combo.getSize(); i++) {
                Object item = combo.getElementAt(i);
                if (item instanceof Categoria c && r.getCategoria().getId().equals(c.getId())) {
                    view.getCmbCategoria().setSelectedIndex(i);
                    break;
                }
            }
        }
        view.getTxtId().setEditable(false);
    }

    public void limpiar() {
        if (view.getTxtId() != null) {
            view.getTxtId().setText("");
            view.getTxtId().setEditable(true);
        }
        if (view.getTxtDescripcion() != null) view.getTxtDescripcion().setText("");
        if (view.getCmbCategoria() != null && view.getCmbCategoria().getItemCount() > 0) {
            view.getCmbCategoria().setSelectedIndex(0);
        }
        if (view.getTable() != null) view.getTable().clearSelection();
    }

    private void actualizarTabla(List<Recurso> lista) {
        if (view != null && view.getTable() != null) {
            model.mostrar(lista);
            view.getTable().setModel(model.getTableModel());
        }
    }

    public List<Recurso> buscarPorCategoria(Categoria categoria) {
        return model.getRecursosPorCategoria(categoria);
    }

    public void guardar(Recurso recurso) throws Exception {
        model.guardar(recurso);
    }

    public boolean eliminar(String id) throws Exception {
        return model.eliminar(id);
    }

    public List<Categoria> getCategorias() {
        return model.getCategorias();
    }

    public void print() {
        try {
            String dest = "recursos.pdf";
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            document.add(new Paragraph("Listado de Recursos"));
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
            JOptionPane.showMessageDialog(view.getMainPanel(), "No se pudo generar el PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
