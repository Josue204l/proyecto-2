package reservas.presentation;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class PanelBarras extends JPanel {
    private List<String> etiquetas = new ArrayList<>();
    private List<Integer> valores = new ArrayList<>();

    public PanelBarras() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(420, 240));
    }

    public void setDatos(List<String> etiquetas, List<Integer> valores) {
        this.etiquetas = etiquetas == null ? new ArrayList<>() : new ArrayList<>(etiquetas);
        this.valores = valores == null ? new ArrayList<>() : new ArrayList<>(valores);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (etiquetas.isEmpty()) {
            g.setColor(Color.DARK_GRAY);
            g.drawString("Sin datos para el período.", 16, 24);
            return;
        }
        int maximo = 1;
        for (int valor : valores) maximo = Math.max(maximo, valor);
        int alto = Math.max(18, (getHeight() - 20) / etiquetas.size());
        FontMetrics medidas = g.getFontMetrics();
        int texto = 140;
        int anchoUtil = Math.max(20, getWidth() - texto - 50);
        for (int i = 0; i < etiquetas.size(); i++) {
            int y = 10 + i * alto;
            String etiqueta = etiquetas.get(i);
            if (medidas.stringWidth(etiqueta) > texto - 8) {
                while (etiqueta.length() > 1 && medidas.stringWidth(etiqueta + "…") > texto - 8) {
                    etiqueta = etiqueta.substring(0, etiqueta.length() - 1);
                }
                etiqueta = etiqueta + "…";
            }
            g.setColor(Color.DARK_GRAY);
            g.drawString(etiqueta, 8, y + alto / 2);
            int cantidad = i < valores.size() ? valores.get(i) : 0;
            int ancho = (int) (anchoUtil * (cantidad / (double) maximo));
            g.setColor(new Color(55, 110, 170));
            g.fillRect(texto, y + 4, Math.max(cantidad == 0 ? 0 : 2, ancho), alto - 10);
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(cantidad), texto + ancho + 6, y + alto / 2);
        }
    }
}
