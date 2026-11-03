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
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class MainFrame extends JFrame implements ReceptorNotificaciones {
    private final ServiceProxy servicio;
    private final DefaultListModel<Reserva> alertas = new DefaultListModel<>();
    private final DefaultTableModel modeloAlertas = new DefaultTableModel(new Object[]{"Actividad", "Ver", "Desc."}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tablaAlertas = new JTable(modeloAlertas);

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
        panel.setBorder(BorderFactory.createTitledBorder("Alertas"));
        panel.setPreferredSize(new Dimension(250, 0));
        tablaAlertas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaAlertas.setRowHeight(30);
        tablaAlertas.getTableHeader().setReorderingAllowed(false);
        tablaAlertas.getColumnModel().getColumn(0).setPreferredWidth(190);
        configurarColumnaAccion(1, "detalles.png", "Ver detalle");
        configurarColumnaAccion(2, "remove.png", "Descartar");
        tablaAlertas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int fila = tablaAlertas.rowAtPoint(e.getPoint());
                int columna = tablaAlertas.columnAtPoint(e.getPoint());
                if (fila < 0) return;
                if (columna == 1 || (columna == 0 && e.getClickCount() == 2)) verAlerta(fila);
                else if (columna == 2) descartarAlerta(fila);
            }
        });
        panel.add(new JScrollPane(tablaAlertas), BorderLayout.CENTER);
        return panel;
    }

    private void configurarColumnaAccion(int columna, String icono, String descripcion) {
        Icon iconoAccion = columna == 1 ? new FolderIcon() : UiHelper.icono(icono);
        tablaAlertas.getColumnModel().getColumn(columna).setCellRenderer((table, value, selected, focus, row, col) -> {
            JButton boton = new JButton();
            boton.setIcon(iconoAccion);
            boton.setToolTipText(descripcion);
            boton.setBorderPainted(false);
            boton.setContentAreaFilled(false);
            boton.setHorizontalAlignment(SwingConstants.CENTER);
            return boton;
        });
        tablaAlertas.getColumnModel().getColumn(columna).setPreferredWidth(42);
        tablaAlertas.getColumnModel().getColumn(columna).setMaxWidth(48);
    }

    private static final class FolderIcon implements Icon {
        @Override public int getIconWidth() { return 20; }
        @Override public int getIconHeight() { return 16; }

        @Override public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.translate(x, y);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(246, 194, 72));
            g.fillRoundRect(1, 2, 9, 5, 2, 2);
            g.setColor(new Color(62, 139, 202));
            g.fillRoundRect(1, 5, 18, 10, 3, 3);
            g.setColor(new Color(36, 103, 162));
            g.drawRoundRect(1, 5, 18, 10, 3, 3);
            g.setColor(new Color(130, 193, 235));
            g.drawLine(4, 7, 16, 7);
            g.dispose();
        }
    }

    @Override
    public void recibirNotificacion(Notificacion notificacion) {
        if (!Notificacion.NUEVA_RESERVA.equals(notificacion.getTipo()) || !(notificacion.getDatos() instanceof Reserva reserva)) return;
        SwingUtilities.invokeLater(() -> {
            alertas.add(0, reserva);
            String encargado = reserva.getSolicitante() == null ? "" : reserva.getSolicitante().getNombre();
            String actividad = (encargado.isBlank() ? "" : encargado + " - ") + reserva.getTitulo();
            modeloAlertas.insertRow(0, new Object[]{actividad, "", ""});
            tablaAlertas.setRowSelectionInterval(0, 0);
        });
    }

    private void verAlerta(int fila) {
        if (fila < 0 || fila >= alertas.size()) return;
        Reserva r = alertas.get(fila);
        JPanel detalle = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 4, 2, 6);
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        detalle.add(new JLabel("Encargado:"), c);
        c.gridx = 1;
        detalle.add(new JLabel(r.getSolicitante() == null ? "" : r.getSolicitante().getNombre()), c);
        c.gridx = 0;
        c.gridy++;
        detalle.add(new JLabel("Fecha:"), c);
        c.gridx = 1;
        detalle.add(new JLabel(r.getFecha() == null ? "" : r.getFecha().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))), c);
        c.gridx = 0;
        c.gridy++;
        detalle.add(new JLabel("Desde:"), c);
        c.gridx = 1;
        detalle.add(new JLabel(r.getHoraInicio() == null ? "" : r.getHoraInicio().toString()), c);
        c.gridx = 0;
        c.gridy++;
        detalle.add(new JLabel("Hasta:"), c);
        c.gridx = 1;
        detalle.add(new JLabel(r.getHoraFin() == null ? "" : r.getHoraFin().toString()), c);
        c.gridx = 0;
        c.gridy++;
        c.anchor = GridBagConstraints.NORTHWEST;
        detalle.add(new JLabel("Recursos:"), c);
        JList<String> recursos = new JList<>(r.getRecursosAsignados() == null ? new String[0] : r.getRecursosAsignados().stream()
                .map(Recurso::getDescripcion).toArray(String[]::new));
        recursos.setVisibleRowCount(3);
        c.gridx = 1;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        detalle.add(new JScrollPane(recursos), c);
        detalle.setPreferredSize(new Dimension(235, 165));
        JOptionPane.showMessageDialog(this, detalle, r.getTitulo(), JOptionPane.PLAIN_MESSAGE);
    }

    private void descartarAlerta(int fila) {
        if (fila < 0 || fila >= alertas.size()) return;
        int confirmacion = JOptionPane.showConfirmDialog(this, alertas.get(fila).getTitulo(), "¿Descartar?", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            alertas.remove(fila);
            modeloAlertas.removeRow(fila);
        }
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
