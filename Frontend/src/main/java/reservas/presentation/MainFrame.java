package reservas.presentation;

import reservas.client.Service;
import reservas.logic.Funcionario;
import reservas.logic.Reserva;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

public class MainFrame extends JFrame implements ThreadListener {
    private final Service servicio;
    private SocketListener avisos;
    private boolean cerrado;
    private final DefaultListModel<Reserva> alertas = new DefaultListModel<>();
    private final JList<Reserva> listaAlertas = new JList<>(alertas);
    private reservas.presentation.reservas.Controller reservas;
    private reservas.presentation.calendarizacion.Controller calendarizacion;
    private reservas.presentation.actividades.Controller actividades;

    public MainFrame(Funcionario usuario, Service servicio) {
        super("Sistema de reservas - " + usuario.getId() + " (" + usuario.getRol() + ")");
        this.servicio = servicio;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        if (Ui.icono("icon.png") != null) setIconImage(Ui.icono("icon.png").getImage());

        JTabbedPane pestanas = new JTabbedPane();
        boolean admin = "ADMIN".equalsIgnoreCase(usuario.getRol());
        if (admin) {
            reservas.presentation.funcionarios.View funcionarios = new reservas.presentation.funcionarios.View();
            new reservas.presentation.funcionarios.Controller(funcionarios,
                    new reservas.presentation.funcionarios.Model(), servicio);
            pestanas.addTab("Funcionarios", Ui.icono("funcionarios.png"), funcionarios);

            reservas.presentation.categorias.View categorias = new reservas.presentation.categorias.View();
            new reservas.presentation.categorias.Controller(categorias,
                    new reservas.presentation.categorias.Model(), servicio);
            pestanas.addTab("Categorías", Ui.icono("categorias.png"), categorias);

            reservas.presentation.recursos.View recursos = new reservas.presentation.recursos.View();
            new reservas.presentation.recursos.Controller(recursos,
                    new reservas.presentation.recursos.Model(), servicio);
            pestanas.addTab("Recursos", Ui.icono("recursos.png"), recursos);
        } else {
            reservas.presentation.reservas.View vistaReservas = new reservas.presentation.reservas.View();
            reservas = new reservas.presentation.reservas.Controller(vistaReservas,
                    new reservas.presentation.reservas.Model(), servicio, usuario);
            pestanas.addTab("Mis reservas", Ui.icono("reservas.png"), vistaReservas);
        }

        reservas.presentation.calendarizacion.View vistaCal = new reservas.presentation.calendarizacion.View();
        calendarizacion = new reservas.presentation.calendarizacion.Controller(vistaCal, servicio);
        pestanas.addTab("Calendarización", Ui.icono("calendarizacion.png"), vistaCal);

        reservas.presentation.actividades.View vistaAct = new reservas.presentation.actividades.View();
        actividades = new reservas.presentation.actividades.Controller(vistaAct, servicio);
        pestanas.addTab("Actividades", Ui.icono("actividades.png"), vistaAct);

        reservas.presentation.estadisticas.View vistaEst = new reservas.presentation.estadisticas.View();
        new reservas.presentation.estadisticas.Controller(vistaEst,
                new reservas.presentation.estadisticas.Model(), servicio);
        pestanas.addTab("Estadísticas", Ui.icono("statistics.png"), vistaEst);

        JPanel contenido = new JPanel(new BorderLayout(8, 0));
        contenido.add(pestanas, BorderLayout.CENTER);
        contenido.add(panelAlertas(), BorderLayout.EAST);
        setJMenuBar(menu());
        setContentPane(contenido);
        setSize(1280, 760);
        setLocationRelativeTo(null);
    }

    private JPanel panelAlertas() {
        JPanel panel = new JPanel(new BorderLayout(4, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Alertas de reservas"));
        panel.setPreferredSize(new Dimension(280, 0));
        listaAlertas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaAlertas.setCellRenderer((lista, valor, indice, seleccionado, foco) -> {
            Reserva reserva = (Reserva) valor;
            String texto = "Nueva reserva: " + reserva.getTitulo() + " · " + reserva.getFecha();
            javax.swing.JLabel etiqueta = new javax.swing.JLabel(texto);
            if (seleccionado) {
                etiqueta.setOpaque(true);
                etiqueta.setBackground(lista.getSelectionBackground());
                etiqueta.setForeground(lista.getSelectionForeground());
            }
            return etiqueta;
        });
        panel.add(new JScrollPane(listaAlertas), BorderLayout.CENTER);
        JButton ver = new JButton("Ver detalle");
        JButton descartar = new JButton("Descartar");
        Ui.icono(ver, "detalles.png");
        Ui.icono(descartar, "descartar.png");
        ver.addActionListener(e -> verAlerta());
        descartar.addActionListener(e -> {
            int indice = listaAlertas.getSelectedIndex();
            if (indice >= 0) alertas.remove(indice);
        });
        JPanel acciones = new JPanel(new GridLayout(1, 2, 6, 0));
        acciones.add(ver);
        acciones.add(descartar);
        panel.add(acciones, BorderLayout.SOUTH);
        return panel;
    }

    private JMenuBar menu() {
        JMenuBar barra = new JMenuBar();
        JMenu archivo = new JMenu("Archivo");
        JMenuItem cambiar = new JMenuItem("Cambiar clave");
        Ui.icono(cambiar, "clave.png");
        cambiar.addActionListener(e -> {
            reservas.presentation.clave.View dialogo = new reservas.presentation.clave.View(this);
            new reservas.presentation.clave.Controller(dialogo, servicio);
            dialogo.setVisible(true);
        });
        JMenuItem salir = new JMenuItem("Salir");
        salir.addActionListener(e -> dispose());
        archivo.add(cambiar);
        archivo.add(salir);
        barra.add(archivo);
        return barra;
    }

    private void verAlerta() {
        Reserva reserva = listaAlertas.getSelectedValue();
        if (reserva == null) return;
        String recursos = "";
        for (int i = 0; i < reserva.getRecursosAsignados().size(); i++) {
            if (i > 0) recursos += ", ";
            recursos += reserva.getRecursosAsignados().get(i).getDescripcion();
        }
        JOptionPane.showMessageDialog(this,
                "Título: " + reserva.getTitulo()
                        + "\nFecha: " + reserva.getFecha()
                        + "\nHorario: " + reserva.getHoraInicio() + " - " + reserva.getHoraFin()
                        + "\nSolicitante: " + (reserva.getSolicitante() == null ? "" : reserva.getSolicitante().getNombre())
                        + "\nRecursos: " + recursos,
                "Detalle de la alerta", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void reservaCreada(Reserva reserva) {
        alertas.add(0, reserva);
        listaAlertas.setSelectedIndex(0);
        if (reservas != null) reservas.refrescar();
        if (calendarizacion != null) calendarizacion.refrescar();
        if (actividades != null) actividades.refrescar();
    }

    public void setAvisos(SocketListener avisos) {
        this.avisos = avisos;
    }

    public void cerrar() {
        if (cerrado) return;
        cerrado = true;
        if (avisos != null) avisos.detener();
        try {
            servicio.desconectar();
        } catch (Exception ignored) { }
    }
}
