package reservas.presentation;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class ReportePdf {
    private ReportePdf() { }

    public static void guardarYAbrir(Component padre, String nombre, String titulo, List<String> lineas) {
        JFileChooser selector = new JFileChooser();
        selector.setSelectedFile(new File(nombre));
        if (selector.showSaveDialog(padre) != JFileChooser.APPROVE_OPTION) return;
        File destino = selector.getSelectedFile();
        if (!destino.getName().toLowerCase().endsWith(".pdf")) {
            destino = new File(destino.getAbsolutePath() + ".pdf");
        }
        try {
            escribir(destino, titulo, lineas);
            if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(destino);
            else JOptionPane.showMessageDialog(padre, "Reporte guardado en " + destino.getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(padre, "No se pudo generar el PDF: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void escribir(File destino, String titulo, List<String> lineas) throws Exception {
        List<byte[]> contenidos = new ArrayList<>();
        int desde = 0;
        boolean primera = true;
        while (primera || desde < lineas.size()) {
            primera = false;
            StringBuilder texto = new StringBuilder();
            texto.append("BT\n/F1 16 Tf\n40 760 Td\n(").append(esc(titulo)).append(") Tj\n/F1 10 Tf\n");
            int y = 730;
            if (desde > 0) {
                texto.append("0 -30 Td\n");
                y = 730;
            } else {
                texto.append("0 -30 Td\n");
            }
            int escritos = 0;
            while (desde < lineas.size() && escritos < 42) {
                texto.append("(").append(esc(lineas.get(desde))).append(") Tj\n0 -15 Td\n");
                desde++;
                escritos++;
                y -= 15;
                if (y < 40) break;
            }
            texto.append("ET\n");
            contenidos.add(texto.toString().getBytes(StandardCharsets.ISO_8859_1));
            if (lineas.isEmpty()) break;
        }

        int paginas = contenidos.size();
        int objetos = 3 + paginas * 2;
        List<byte[]> piezas = new ArrayList<>();
        List<Integer> offsets = new ArrayList<>();
        byte[] encabezado = "%PDF-1.4\n".getBytes(StandardCharsets.US_ASCII);
        int cursor = encabezado.length;

        StringBuilder kids = new StringBuilder();
        for (int i = 0; i < paginas; i++) kids.append(3 + i * 2).append(" 0 R ");

        cursor = agregar(piezas, offsets, cursor, "1 0 obj << /Type /Catalog /Pages 2 0 R >> endobj\n");
        cursor = agregar(piezas, offsets, cursor,
                "2 0 obj << /Type /Pages /Count " + paginas + " /Kids [" + kids + "] >> endobj\n");
        for (int i = 0; i < paginas; i++) {
            int pagina = 3 + i * 2;
            int contenido = pagina + 1;
            cursor = agregar(piezas, offsets, cursor,
                    pagina + " 0 obj << /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Contents "
                            + contenido + " 0 R /Resources << /Font << /F1 " + objetos + " 0 R >> >> >> endobj\n");
            cursor = agregar(piezas, offsets, cursor,
                    contenido + " 0 obj << /Length " + contenidos.get(i).length + " >> stream\n");
            piezas.add(contenidos.get(i));
            cursor += contenidos.get(i).length;
            byte[] cierre = "endstream\nendobj\n".getBytes(StandardCharsets.US_ASCII);
            piezas.add(cierre);
            cursor += cierre.length;
        }
        cursor = agregar(piezas, offsets, cursor,
                objetos + " 0 obj << /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >> endobj\n");

        int xref = cursor;
        StringBuilder tabla = new StringBuilder("xref\n0 " + (objetos + 1) + "\n0000000000 65535 f \n");
        for (int offset : offsets) tabla.append(String.format("%010d 00000 n \n", offset));
        tabla.append("trailer << /Size ").append(objetos + 1).append(" /Root 1 0 R >>\nstartxref\n")
                .append(xref).append("\n%%EOF\n");

        try (FileOutputStream out = new FileOutputStream(destino)) {
            out.write(encabezado);
            for (byte[] pieza : piezas) out.write(pieza);
            out.write(tabla.toString().getBytes(StandardCharsets.US_ASCII));
        }
    }

    private static int agregar(List<byte[]> piezas, List<Integer> offsets, int cursor, String texto) {
        offsets.add(cursor);
        byte[] bytes = texto.getBytes(StandardCharsets.US_ASCII);
        piezas.add(bytes);
        return cursor + bytes.length;
    }

    private static String esc(String valor) {
        if (valor == null) return "";
        StringBuilder salida = new StringBuilder();
        for (int i = 0; i < valor.length(); i++) {
            char c = valor.charAt(i);
            if (c == '\\' || c == '(' || c == ')') salida.append('\\').append(c);
            else if (c >= 32 && c <= 255) salida.append(c);
            else salida.append(' ');
        }
        return salida.toString();
    }
}
