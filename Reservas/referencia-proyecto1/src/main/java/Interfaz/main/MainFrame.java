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

import logic.Funcionario;
import logic.Sesion;
import logic.Usuario;

import javax.swing.*;

public class MainFrame extends JFrame {

    public MainFrame(Usuario usuario) {
        super("SISTEMA DE RESERVAS - " + usuario.getId() + " (" + (usuario.getRol() != null ? usuario.getRol().toUpperCase() : "") + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        boolean esAdmin = "ADMIN".equalsIgnoreCase(usuario.getRol());
        Funcionario funcionarioActual = (usuario instanceof Funcionario) ? (Funcionario) usuario : null;

        // Pestañas exclusivas para Administrador
        if (esAdmin) {
            funcionariosView vFunc = new funcionariosView();
            ModelFuncionario mFunc = new ModelFuncionario();
            new ControllerFuncionario(vFunc, mFunc);
            tabbedPane.addTab("Funcionarios", vFunc.getMainPanel());

            categoriasView vCat = new categoriasView();
            ModelCategoria mCat = new ModelCategoria();
            new ControllerCategoria(vCat, mCat);
            tabbedPane.addTab("Categorías", vCat.getMainPanel());

            recursosView vRec = new recursosView();
            ModelRecurso mRec = new ModelRecurso();
            new ControllerRecurso(vRec, mRec);
            tabbedPane.addTab("Recursos", vRec.getMainPanel());
        }

        // Pestaña exclusiva para Funcionario
        if (!esAdmin && funcionarioActual != null) {
            reservasView vRes = new reservasView();
            new ControllerReserva(vRes, funcionarioActual);
            tabbedPane.addTab("Mis Reservas", vRes.getMainPanel());
        }

        // Pestañas compartidas (Administrador y Funcionario)
        calendarizacionView vCal = new calendarizacionView();
        ModelCalendarizacion mCal = new ModelCalendarizacion();
        new ControllerCalendarizacion(vCal, mCal);
        tabbedPane.addTab("Calendarización", vCal.getMainPanel());

        actividadesView vAct = new actividadesView();
        ModelActividades mAct = new ModelActividades();
        new ControllerActividades(vAct, mAct);
        tabbedPane.addTab("Actividades", vAct.getMainPanel());

        estadisticasView vEst = new estadisticasView();
        ModelEstadisticas mEst = new ModelEstadisticas();
        new ControllerEstadisticas(vEst, mEst);
        tabbedPane.addTab("Estadísticas", vEst.getMainPanel());

        setJMenuBar(crearMenu(usuario));
        setContentPane(tabbedPane);
        setSize(1100, 760);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JMenuBar crearMenu(Usuario usuario) {
        JMenuBar bar = new JMenuBar();
        JMenu archivo = new JMenu("Archivo");

        JMenuItem cambiar = new JMenuItem("Cambiar clave");
        cambiar.addActionListener(e -> {
            cambiarclaveView dialogo = new cambiarclaveView(this);
            new ControllerCambiarClave(dialogo, usuario);
            dialogo.setVisible(true);
        });

        JMenuItem cerrar = new JMenuItem("Cerrar sesión");
        cerrar.addActionListener(e -> cerrarSesion());

        JMenuItem salir = new JMenuItem("Salir");
        salir.addActionListener(e -> System.exit(0));

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
        new ControllerLogin(new ModelLogin(), loginView);
        loginView.setVisible(true);
    }
}