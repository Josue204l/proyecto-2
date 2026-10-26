package Interfaz.reservas;

import utils.PDFGenerator;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import logic.Categoria;
import logic.Funcionario;
import logic.Recurso;
import logic.Reserva;
import services.AIService;
import services.ReservaExtraccion;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ControllerReserva {

    private final ModelReserva model;
    private final reservasView view;
    private final Funcionario usuarioActual;

    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm");

    public ControllerReserva(reservasView view, Funcionario usuarioActual) {
        this.view = view;
        this.model = new ModelReserva();
        this.usuarioActual = usuarioActual;
        this.model.setFuncionarioId(usuarioActual != null ? usuarioActual.getId() : null);
        view.setController(this);
        actualizarTabla();
    }

    public void extraerConIA() {
        if (view.getTextFrase() == null) return;

        String frase = view.getTextFrase().getText().trim();
        if (frase.isEmpty()) {
            JOptionPane.showMessageDialog(view.getMainPanel(),
                    "Por favor, ingrese una frase en el campo de texto para extraer los datos con IA.",
                    "Campo Vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Animación/Aviso de carga
            view.getExtraerButton().setEnabled(false);

            // Llamar al servicio de extracción con LangChain4j
            AIService aiService = new AIService();
            ReservaExtraccion datos = aiService.extraerReserva(frase);

            if (datos != null) {
                // 1. Actividad / Título
                if (datos.getActividad() != null && view.getTxtActividad() != null) {
                    view.getTxtActividad().setText(datos.getActividad());
                }

                // 2. Fecha (ISO yyyy-MM-dd devuelto por la IA)
                if (datos.getFecha() != null && view.getDatePickerFecha() != null) {
                    try {
                        LocalDate fechaIso = LocalDate.parse(datos.getFecha());
                        view.getDatePickerFecha().setDate(fechaIso);
                    } catch (Exception ignored) {
                    }
                }

                // 3. Hora Inicio
                if (datos.getHoraInicio() != null && view.getTxtHoraInicio() != null) {
                    view.getTxtHoraInicio().setText(datos.getHoraInicio());
                }

                // 4. Hora Fin
                if (datos.getHoraFinal() != null && view.getTxtHoraFin() != null) {
                    view.getTxtHoraFin().setText(datos.getHoraFinal());
                }

                // 5. Categorías seleccionadas
                if (datos.getCategoriasRecurso() != null && !datos.getCategoriasRecurso().isEmpty() && view.getListaCategorias() != null) {
                    List<String> categoriasNombreIA = datos.getCategoriasRecurso();
                    ListModel<Categoria> modelCat = view.getListaCategorias().getModel();
                    List<Integer> indicesParaSeleccionar = new ArrayList<>();

                    for (int i = 0; i < modelCat.getSize(); i++) {
                        Categoria cat = modelCat.getElementAt(i);
                        for (String nombreIa : categoriasNombreIA) {
                            String objetivo = nombreIa.trim();
                            boolean coincide = (cat.getEtiqueta() != null && cat.getEtiqueta().equalsIgnoreCase(objetivo))
                                    || (cat.getNombre() != null && cat.getNombre().equalsIgnoreCase(objetivo))
                                    || (cat.getDescripcion() != null && cat.getDescripcion().equalsIgnoreCase(objetivo));
                            if (coincide) {
                                indicesParaSeleccionar.add(i);
                                break;
                            }
                        }
                    }

                    int[] indicesArray = indicesParaSeleccionar.stream().mapToInt(Integer::intValue).toArray();
                    view.getListaCategorias().setSelectedIndices(indicesArray);

                    // Reflejar nombres de categorías en el campo de texto informativo
                    if (view.getTxtCategoriasRequeridas() != null) {
                        view.getTxtCategoriasRequeridas().setText(String.join(", ", categoriasNombreIA));
                    }
                }

                JOptionPane.showMessageDialog(view.getMainPanel(),
                        "Datos extraídos correctamente por la IA.",
                        "Extracción Exitosa", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(),
                    "Error al comunicarse con el servicio de IA: " + ex.getMessage(),
                    "Error IA", JOptionPane.ERROR_MESSAGE);
        } finally {
            view.getExtraerButton().setEnabled(true);
        }
    }

    public void guardar() {
        try {
            String titulo = view.getTxtActividad() != null ? view.getTxtActividad().getText().trim() : "";
            LocalDate fecha = view.getDatePickerFecha() != null ? view.getDatePickerFecha().getDate() : null;
            String horaInicioStr = view.getTxtHoraInicio() != null ? view.getTxtHoraInicio().getText().trim() : "";
            String horaFinStr = view.getTxtHoraFin() != null ? view.getTxtHoraFin().getText().trim() : "";

            if (titulo.isEmpty()) throw new Exception("El título de la actividad es obligatorio.");
            if (fecha == null) throw new Exception("La fecha es obligatoria.");
            if (horaInicioStr.isEmpty() || horaFinStr.isEmpty()) throw new Exception("Las horas son obligatorias.");

            if (fecha.isBefore(LocalDate.now())) throw new Exception("La fecha no puede ser en el pasado.");

            LocalTime horaInicio = LocalTime.parse(horaInicioStr, FMT_HORA);
            LocalTime horaFin = LocalTime.parse(horaFinStr, FMT_HORA);
            if (!horaFin.isAfter(horaInicio)) throw new Exception("La hora de fin debe ser posterior a la hora de inicio.");

            // Obtener las categorías seleccionadas desde la vista
            List<Categoria> categoriasSeleccionadas = obtenerCategoriasSeleccionadas();
            if (categoriasSeleccionadas.isEmpty()) {
                throw new Exception("Debe seleccionar al menos una categoría de recursos.");
            }

            // Asignar primer recurso libre de cada categoría
            List<Recurso> recursosAsignados = model.asignarRecursosDisponibles(categoriasSeleccionadas, fecha, horaInicio, horaFin);

            Reserva nueva = new Reserva(model.generarId(), titulo, fecha, horaInicio, horaFin,
                    recursosAsignados, categoriasSeleccionadas, usuarioActual);

            model.guardar(nueva);
            actualizarTabla();
            limpiar();
            JOptionPane.showMessageDialog(view.getMainPanel(), "Reserva guardada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), "Formato inválido. Use dd/MM/yyyy para fecha y HH:mm para hora.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getMainPanel(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Categoria> obtenerCategoriasSeleccionadas() {
        List<Categoria> lista = new ArrayList<>();
        if (view.getListaCategorias() != null) {
            lista = view.getListaCategorias().getSelectedValuesList();
        }
        return lista;
    }

    public void cancelarSeleccionada() {
        if (view.getTablaMisReservas() == null) return;
        int fila = view.getTablaMisReservas().getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(view.getMainPanel(), "Seleccione una reserva para cancelar.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String id = (String) view.getTablaMisReservas().getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(view.getMainPanel(), "¿Cancelar la reserva seleccionada?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                model.cancelar(id);
                actualizarTabla();
                JOptionPane.showMessageDialog(view.getMainPanel(),
                        "Reserva cancelada. Los recursos quedaron libres.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view.getMainPanel(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void limpiar() {
        if (view.getTextFrase() != null) view.getTextFrase().setText("");
        if (view.getTxtActividad() != null) view.getTxtActividad().setText("");
        if (view.getDatePickerFecha() != null) view.getDatePickerFecha().clear();
        if (view.getTxtHoraInicio() != null) view.getTxtHoraInicio().setText("");
        if (view.getTxtHoraFin() != null) view.getTxtHoraFin().setText("");
        if (view.getTxtCategoriasRequeridas() != null) view.getTxtCategoriasRequeridas().setText("");
        if (view.getListaCategorias() != null) view.getListaCategorias().clearSelection();
    }

    private void actualizarTabla() {
        if (view.getTablaMisReservas() != null) {
            model.refrescarTabla();
            view.getTablaMisReservas().setModel(model.getTableModel());
        }
    }

    public void elegirHoraInicio() {
        elegirHora(view.getTxtHoraInicio());
    }

    public void elegirHoraFin() {
        elegirHora(view.getTxtHoraFin());
    }

    private void elegirHora(JTextField campo) {
        if (campo == null) return;
        String[] horas = new String[15];
        for (int i = 0; i < horas.length; i++) {
            horas[i] = String.format("%02d:00", 7 + i);
        }
        String elegido = (String) JOptionPane.showInputDialog(view.getMainPanel(), "Seleccione hora",
                "Hora", JOptionPane.PLAIN_MESSAGE, null, horas, campo.getText());
        if (elegido != null) campo.setText(elegido);
    }

    public void print() {
        try {
            String dest = "reservas.pdf";
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            document.add(new Paragraph("Mis Reservas"));
            JTable tabla = view.getTablaMisReservas();
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

    public ModelReserva getModel() { return model; }

    public List<Reserva> getReservas() { return model.getReservas(); }
}