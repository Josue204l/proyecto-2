package Interfaz.main;

import Interfaz.actividades.ControllerActividades;
import Interfaz.actividades.ModelActividades;
import Interfaz.actividades.actividadesView;
import Interfaz.calendarizacion.ControllerCalendarizacion;
import Interfaz.calendarizacion.ModelCalendarizacion;
import Interfaz.calendarizacion.calendarizacionView;
import Interfaz.cambiarclave.ControllerCambiarClave;
import Interfaz.cambiarclave.cambiarclaveView;
import Interfaz.categorias.ControllerCategoria;
import Interfaz.categorias.ModelCategoria;
import Interfaz.categorias.categoriasView;
import Interfaz.estadisticas.ControllerEstadisticas;
import Interfaz.estadisticas.ModelEstadisticas;
import Interfaz.estadisticas.estadisticasView;
import Interfaz.funcionarios.ControllerFuncionario;
import Interfaz.funcionarios.ModelFuncionario;
import Interfaz.funcionarios.funcionariosView;
import Interfaz.login.ControllerLogin;
import Interfaz.login.LoginView;
import Interfaz.login.ModelLogin;
import Interfaz.recursos.ControllerRecurso;
import Interfaz.recursos.ModelRecurso;
import Interfaz.recursos.recursosView;
import Interfaz.reservas.ControllerReserva;
import Interfaz.reservas.reservasView;
import frontend.red.ReceptorNotificaciones;
import frontend.red.ServiceProxy;
import logic.Funcionario;
import logic.Recurso;
import logic.Reserva;
import logic.Sesion;
import logic.Usuario;
import protocolo.Notificacion;
import utils.UiHelper;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class MainFrame extends JFrame implements ReceptorNotificaciones {
    private final ServiceProxy servicio;
    private final DefaultListModel<Reserva> alertas = new DefaultListModel<>();
    private final JList<Reserva> listaAlertas = new JList<>(alertas);

    public MainFrame(Usuario usuario, ServiceProxy servicio) {
        super("SISTEMA DE RESERVAS - " + usuario.getId() + " (" +
                (usuario.getRol() != null ? usuario.getRol().toUpperCase() : "") + ")");
        this.servicio = servicio;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ImageIcon iconoVentana = UiHelper.icono("icon.png");
        if (iconoVentana != null) {
            setIconImage(iconoVentana.getImage());
        }
        servicio.setReceptorNotificaciones(this);

        JTabbedPane tabbedPane = new JTabbedPane();
        boolean esAdmin = "ADMIN".equalsIgnoreCase(usuario.getRol());
        Funcionario funcionarioActual = usuario instanceof Funcionario f ? f : null;

        if (esAdmin) {
            funcionariosView vFunc = new funcionariosView();
            new ControllerFuncionario(vFunc, new ModelFuncionario());
            tabbedPane.addTab("Funcionarios", UiHelper.icono("funcionarios.png"), vFunc.getMainPanel());

            categoriasView vCat = new categoriasView();
            new ControllerCategoria(vCat, new ModelCategoria());
            tabbedPane.addTab("Categorías", UiHelper.icono("categorias.png"), vCat.getMainPanel());

            recursosView vRec = new recursosView();
            new ControllerRecurso(vRec, new ModelRecurso());
            tabbedPane.addTab("Recursos", UiHelper.icono("recursos.png"), vRec.getMainPanel());
        } else if (funcionarioActual != null) {
            reservasView vRes = new reservasView();
            new ControllerReserva(vRes, funcionarioActual);
            tabbedPane.addTab("Mis Reservas", UiHelper.icono("reservas.png"), vRes.getMainPanel());
        }

        calendarizacionView vCal = new calendarizacionView();
        new ControllerCalendarizacion(vCal, new ModelCalendarizacion());
        tabbedPane.addTab("Calendarización", UiHelper.icono("calendarizacion.png"), vCal.getMainPanel());

        actividadesView vAct = new actividadesView();
        new ControllerActividades(vAct, new ModelActividades());
        tabbedPane.addTab("Actividades", UiHelper.icono("actividades.png"), vAct.getMainPanel());

        estadisticasView vEst = new estadisticasView();
        new ControllerEstadisticas(vEst, new ModelEstadisticas());
        tabbedPane.addTab("Estadísticas", UiHelper.icono("statistics.png"), vEst.getMainPanel());

        JPanel contenido = new JPanel(new BorderLayout(8, 0));
        contenido.add(tabbedPane, BorderLayout.CENTER);
        contenido.add(crearPanelAlertas(), BorderLayout.EAST);
        setJMenuBar(crearMenu(usuario));
        setContentPane(contenido);
        setSize(1360, 760);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel crearPanelAlertas() {
        JPanel panel = new JPanel(new BorderLayout(4, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Alertas de reservas"));
        panel.setPreferredSize(new Dimension(285, 0));
        listaAlertas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaAlertas.setCellRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                                    boolean selected, boolean focus) {
                Reserva reserva = (Reserva) value;
                String fecha = reserva.getFecha() == null ? "" : reserva.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                return super.getListCellRendererComponent(list,
                        "Nueva reserva: " + reserva.getTitulo() + " · " + fecha, index, selected, focus);
            }
        });
        listaAlertas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) verAlerta();
            }
        });
        panel.add(new JScrollPane(listaAlertas), BorderLayout.CENTER);
        JButton ver = new JButton("Ver detalle");
        ver.addActionListener(e -> verAlerta());
        UiHelper.setIcon(ver, "detalles.png");
        JButton descartar = new JButton("Descartar");
        descartar.addActionListener(e -> descartarAlerta());
        UiHelper.setIcon(descartar, "descartar.png");
        JPanel acciones = new JPanel(new GridLayout(1, 2, 6, 0));
        acciones.add(ver);
        acciones.add(descartar);
        panel.add(acciones, BorderLayout.SOUTH);
        return panel;
    }

    @Override
    public void recibirNotificacion(Notificacion notificacion) {
        if (!Notificacion.NUEVA_RESERVA.equals(notificacion.getTipo()) || !(notificacion.getDatos() instanceof Reserva reserva)) return;
        SwingUtilities.invokeLater(() -> {
            alertas.add(0, reserva);
            listaAlertas.setSelectedIndex(0);
        });
    }

    private void verAlerta() {
        Reserva r = listaAlertas.getSelectedValue();
        if (r == null) return;
        String recursos = r.getRecursosAsignados() == null ? "" : r.getRecursosAsignados().stream()
                .map(Recurso::getDescripcion).reduce((a, b) -> a + ", " + b).orElse("");
        String detalle = "Título: " + r.getTitulo() + "\nFecha: " + r.getFecha() +
                "\nHorario: " + r.getHoraInicio() + " - " + r.getHoraFin() +
                "\nSolicitante: " + (r.getSolicitante() == null ? "" : r.getSolicitante().getNombre()) +
                "\nRecursos: " + recursos;
        JOptionPane.showMessageDialog(this, detalle, "Detalle de la alerta", JOptionPane.INFORMATION_MESSAGE);
    }

    private void descartarAlerta() {
        int index = listaAlertas.getSelectedIndex();
        if (index < 0) return;
        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Desea descartar esta alerta?",
                "Confirmar descarte", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) alertas.remove(index);
    }

    private JMenuBar crearMenu(Usuario usuario) {
        JMenuBar bar = new JMenuBar();
        JMenu archivo = new JMenu("Archivo");
        JMenuItem cambiar = new JMenuItem("Cambiar clave");
        UiHelper.setIcon(cambiar, "clave.png");
        cambiar.addActionListener(e -> {
            cambiarclaveView dialogo = new cambiarclaveView(this);
            new ControllerCambiarClave(dialogo, usuario, servicio);
            dialogo.setVisible(true);
        });
        JMenuItem cerrar = new JMenuItem("Cerrar sesión");
        cerrar.addActionListener(e -> cerrarSesion());
        JMenuItem salir = new JMenuItem("Salir");
        salir.addActionListener(e -> dispose());
        archivo.add(cambiar);
        archivo.add(cerrar);
        archivo.addSeparator();
        archivo.add(salir);
        bar.add(archivo);
        return bar;
    }

    private void cerrarSesion() {
        Sesion.logout();
        dispose();
        LoginView loginView = new LoginView(null);
        new ControllerLogin(new ModelLogin(), loginView, servicio);
        loginView.setVisible(true);
        if (Sesion.isLoggedIn()) new MainFrame(Sesion.getUsuario(), servicio);
    }
}
