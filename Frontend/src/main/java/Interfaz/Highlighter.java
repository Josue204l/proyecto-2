package Interfaz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Highlighter extends MouseAdapter {

    // CAMBIO: Color amarillo suave para el efecto hover
    private final Color colorResaltado = new Color(255, 245, 190);

    @Override
    public void mouseEntered(MouseEvent e) {
        JComponent componente = (JComponent) e.getSource();

        // Guarda el color original del componente
        componente.putClientProperty("colorOriginal", componente.getBackground());

        // Aplica el color amarillo de resaltado
        componente.setBackground(colorResaltado);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        JComponent componente = (JComponent) e.getSource();

        // Recupera el color original del componente
        Color colorOriginal =
                (Color) componente.getClientProperty("colorOriginal");

        // Restaura el color original
        if (colorOriginal != null) {
            componente.setBackground(colorOriginal);
        }
    }
}