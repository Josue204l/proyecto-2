package Interfaz;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TableHighlighter extends MouseAdapter implements PropertyChangeListener {

    // CAMBIO: Color amarillo suave para el efecto hover
    private final Color colorResaltado = new Color(255, 245, 190);

    private final JTable tabla;
    private int filaHover = -1;
    private boolean instalado = false;

    public TableHighlighter(JTable tabla) {
        this.tabla = tabla;
    }

    public void instalar() {
        if (tabla == null) {
            return;
        }

        if (!instalado) {
            tabla.addMouseMotionListener(this);
            tabla.addMouseListener(this);
            tabla.addPropertyChangeListener(this);
            instalado = true;
        }

        instalarRenderers();
    }

    private void instalarRenderers() {
        if (tabla.getColumnCount() == 0) {
            return;
        }

        for (int i = 0; i < tabla.getColumnCount(); i++) {

            TableCellRenderer renderer =
                    tabla.getColumnModel().getColumn(i).getCellRenderer();

            if (renderer == null) {
                renderer = tabla.getDefaultRenderer(
                        tabla.getColumnClass(i)
                );
            }

            if (renderer == null) {
                renderer = tabla.getDefaultRenderer(Object.class);
            }

            if (renderer instanceof HoverRenderer) {
                continue;
            }

            tabla.getColumnModel().getColumn(i).setCellRenderer(
                    new HoverRenderer(renderer)
            );
        }

        tabla.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int fila = tabla.rowAtPoint(e.getPoint());

        if (fila != filaHover) {
            filaHover = fila;
            tabla.repaint();
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        filaHover = -1;
        tabla.repaint();
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {

        // CAMBIO: Reinstala los renderers cuando cambia el modelo
        if ("model".equals(e.getPropertyName())) {
            SwingUtilities.invokeLater(this::instalarRenderers);
        }
    }

    private class HoverRenderer implements TableCellRenderer {

        private final TableCellRenderer rendererOriginal;

        public HoverRenderer(TableCellRenderer rendererOriginal) {
            this.rendererOriginal = rendererOriginal;
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            Component componente = rendererOriginal.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            if (!isSelected) {
                if (row == filaHover) {
                    componente.setBackground(colorResaltado);
                } else {
                    // Fuerza el color normal explícitamente
                    componente.setBackground(table.getBackground());
                }
            }

            return componente;
        }
    }
}