package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;

public final class UiHelper {

    private UiHelper() {}

    public static ImageIcon icono(String nombre) {
        URL url = UiHelper.class.getResource("/iconos/" + nombre);
        return url != null ? new ImageIcon(url) : null;
    }

    public static void setIcon(AbstractButton boton, String nombre) {
        if (boton == null) return;
        ImageIcon img = icono(nombre);
        if (img != null) boton.setIcon(img);
    }

    public static boolean existeImagen(String nombre) {
        return UiHelper.class.getResource("/iconos/" + nombre) != null;
    }

    public static void bind(AbstractButton boton, ActionListener listener) {
        if (boton == null || listener == null) return;
        for (ActionListener actual : boton.getActionListeners()) {
            boton.removeActionListener(actual);
        }
        boton.addActionListener(listener);
    }

    public static JButton findButton(Container root, String texto) {
        if (root == null) return null;
        for (Component c : root.getComponents()) {
            if (c instanceof JButton b && texto.equals(b.getText())) {
                return b;
            }
            if (c instanceof Container cont) {
                JButton found = findButton(cont, texto);
                if (found != null) return found;
            }
        }
        return null;
    }

    public static JLabel findLabel(Container root, String texto) {
        if (root == null) return null;
        for (Component c : root.getComponents()) {
            if (c instanceof JLabel l && texto.equals(l.getText())) {
                return l;
            }
            if (c instanceof Container cont) {
                JLabel found = findLabel(cont, texto);
                if (found != null) return found;
            }
        }
        return null;
    }

    public static JScrollPane findScrollPane(Container root) {
        if (root == null) return null;
        for (Component c : root.getComponents()) {
            if (c instanceof JScrollPane sp) return sp;
            if (c instanceof Container cont) {
                JScrollPane found = findScrollPane(cont);
                if (found != null) return found;
            }
        }
        return null;
    }

    public static void attachTable(Container root, JTable table) {
        JScrollPane sp = findScrollPane(root);
        if (sp != null) {
            sp.setViewportView(table);
        }
    }
}
