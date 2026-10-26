package Interfaz.funcionarios;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import logic.Funcionario;

import javax.swing.*;
import java.awt.Desktop;
import java.io.File;

public class ControllerFuncionario {

    private final funcionariosView view;
    private final ModelFuncionario model;

    public ControllerFuncionario(funcionariosView view, ModelFuncionario model) {
        this.view = view;
        this.model = model;
        if (view != null) {
            view.setController(this);
        }
        actualizarTabla(model.getFuncionarios());
    }

    public ControllerFuncionario(ModelFuncionario model) {
        this(null, model);
    }

    public void guardar() {
        if (view == null) return;
        try {
            String id = texto(view.getTxtId());
            String nombre = texto(view.getTxtNombre());
            String telefono = texto(view.getTxtTelefono());
            if (id.isEmpty()) throw new Exception("El ID o cédula del funcionario es obligatorio.");
            if (nombre.isEmpty()) throw new Exception("El nombre del funcionario es obligatorio.");

            Funcionario funcionario = new Funcionario(id, id, "FUNCIONARIO", nombre, telefono);
            model.guardar(funcionario);
            actualizarTabla(model.getFuncionarios());
            limpiar();
            JOptionPane.showMessageDialog(view.getMainPanel(), "Funcionario guardado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void eliminar() {
        if (view == null) return;
        String id = texto(view.getTxtId());
        if (id.isEmpty() && view.getTable() != null && view.getTable().getSelectedRow() >= 0) {
            id = String.valueOf(view.getTable().getValueAt(view.getTable().getSelectedRow(), 0));
        }
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(view.getMainPanel(), "Seleccione un funcionario de la lista.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(view.getMainPanel(),
                "¿Desea eliminar al funcionario " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            model.eliminar(id);
            actualizarTabla(model.getFuncionarios());
            limpiar();
            JOptionPane.showMessageDialog(view.getMainPanel(), "Funcionario eliminado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void buscar() {
        if (view == null) return;
        actualizarTabla(model.buscar(texto(view.getTxtBuscarId()), texto(view.getTxtBuscarNombre())));
    }

    public void cargarSeleccionado() {
        if (view == null || view.getTable() == null) return;
        int fila = view.getTable().getSelectedRow();
        if (fila < 0) return;
        Funcionario f = model.getTableModel().getRowAt(fila);
        if (f == null) return;
        view.getTxtId().setText(f.getId());
        view.getTxtNombre().setText(f.getNombre());
        view.getTxtTelefono().setText(f.getTelefono() != null ? f.getTelefono() : "");
        view.getTxtId().setEditable(false);
    }

    public void limpiar() {
        if (view == null) return;
        if (view.getTxtId() != null) {
            view.getTxtId().setText("");
            view.getTxtId().setEditable(true);
        }
        if (view.getTxtNombre() != null) view.getTxtNombre().setText("");
        if (view.getTxtTelefono() != null) view.getTxtTelefono().setText("");
        if (view.getTable() != null) view.getTable().clearSelection();
    }

    private void actualizarTabla(java.util.List<Funcionario> lista) {
        if (view != null && view.getTable() != null) {
            model.mostrar(lista);
            view.getTable().setModel(model.getTableModel());
        }
    }

    private String texto(JTextField campo) {
        return campo == null || campo.getText() == null ? "" : campo.getText().trim();
    }

    public void print() {
        if (view == null) return;
        try {
            String dest = "funcionarios.pdf";
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Reporte - Listado de Funcionarios"));
            JTable tabla = view.getTable();

            if (tabla != null && tabla.getColumnCount() > 0) {
                Table table = new Table(tabla.getColumnCount());
                for (int c = 0; c < tabla.getColumnCount(); c++) {
                    table.addHeaderCell(new Cell().add(new Paragraph(String.valueOf(tabla.getColumnName(c)))));
                }
                for (int r = 0; r < tabla.getRowCount(); r++) {
                    for (int c = 0; c < tabla.getColumnCount(); c++) {
                        Object val = tabla.getValueAt(r, c);
                        table.addCell(new Cell().add(new Paragraph(val == null ? "" : val.toString())));
                    }
                }
                document.add(table);
            }
            document.close();

            File pdfFile = new File(dest);
            if (pdfFile.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), "No se pudo generar el PDF: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public ModelFuncionario getModel() {
        return model;
    }
}