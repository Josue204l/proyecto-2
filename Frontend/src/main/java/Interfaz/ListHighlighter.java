package Interfaz;

import javax.swing.*;
import java.awt.*;

public class ListHighlighter {

    private final JList<?> lista;

    public ListHighlighter(JList<?> lista) {
        this.lista = lista;
    }

    public void instalar() {

        lista.setCellRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                // CAMBIO: Resaltar el elemento sobre el que esta el mouse
                Point punto = list.getMousePosition();

                if (punto != null && list.locationToIndex(punto) == index && !isSelected) {
                    label.setBackground(new Color(255, 245, 190));
                    label.setOpaque(true);
                }

                return label;
            }
        });

        // CAMBIO: Actualiza la lista cuando el mouse se mueve
        lista.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                lista.repaint();
            }
        });

        // CAMBIO: Actualiza la lista cuando el mouse sale
        lista.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                lista.repaint();
            }
        });
    }
}