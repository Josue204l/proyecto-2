package reservas.presentation.estadisticas;

import reservas.presentation.PanelBarras;
import reservas.presentation.Ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class View extends JPanel {
    private final JSpinner desdeRecursos = Ui.fecha();
    private final JSpinner hastaRecursos = Ui.fecha();
    private final JSpinner desdeActividades = Ui.fecha();
    private final JSpinner hastaActividades = Ui.fecha();
    private final JTable tablaRecursos = new JTable();
    private final JTable tablaActividades = new JTable();
    private final PanelBarras graficoRecursos = new PanelBarras();
    private final PanelBarras graficoActividades = new PanelBarras();
    private Controller controller;

    public View() {
        super(new BorderLayout());
        Ui.ponerFecha(desdeRecursos, java.time.LocalDate.now().withDayOfMonth(1));
        Ui.ponerFecha(desdeActividades, java.time.LocalDate.now().withDayOfMonth(1));
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                bloque("Recursos reservados", desdeRecursos, hastaRecursos, tablaRecursos, graficoRecursos, true),
                bloque("Actividades calendarizadas", desdeActividades, hastaActividades, tablaActividades, graficoActividades, false));
        split.setResizeWeight(0.5);
        add(split, BorderLayout.CENTER);
    }

    private JPanel bloque(String titulo, JSpinner desde, JSpinner hasta, JTable tabla, PanelBarras grafico, boolean recursos) {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createTitledBorder(titulo));
        JButton calcular = new JButton("Calcular");
        JButton pdf = new JButton("PDF");
        Ui.icono(calcular, "statistics.png");
        Ui.icono(pdf, "pdf.png");
        JPanel norte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        norte.add(new JLabel("Desde"));
        norte.add(desde);
        norte.add(new JLabel("Hasta"));
        norte.add(hasta);
        norte.add(calcular);
        norte.add(pdf);
        panel.add(norte, BorderLayout.NORTH);
        JSplitPane cuerpo = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(tabla), grafico);
        cuerpo.setResizeWeight(0.45);
        panel.add(cuerpo, BorderLayout.CENTER);
        calcular.addActionListener(e -> {
            if (controller == null) return;
            if (recursos) controller.calcularRecursos();
            else controller.calcularActividades();
        });
        pdf.addActionListener(e -> {
            if (controller == null) return;
            if (recursos) controller.pdfRecursos();
            else controller.pdfActividades();
        });
        return panel;
    }

    public void setController(Controller controller) { this.controller = controller; }
    public JSpinner getDesdeRecursos() { return desdeRecursos; }
    public JSpinner getHastaRecursos() { return hastaRecursos; }
    public JSpinner getDesdeActividades() { return desdeActividades; }
    public JSpinner getHastaActividades() { return hastaActividades; }
    public JTable getTablaRecursos() { return tablaRecursos; }
    public JTable getTablaActividades() { return tablaActividades; }
    public PanelBarras getGraficoRecursos() { return graficoRecursos; }
    public PanelBarras getGraficoActividades() { return graficoActividades; }
}
