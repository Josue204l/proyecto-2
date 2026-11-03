package reservas.presentation;

import reservas.logic.Categoria;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractorFrase {
    private static final String[] MESES = {
            "enero", "febrero", "marzo", "abril", "mayo", "junio",
            "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
    };

    private final LocalDate hoy;

    public ExtractorFrase(LocalDate hoy) {
        this.hoy = hoy == null ? LocalDate.now() : hoy;
    }

    public Datos extraer(String frase, List<Categoria> categorias) throws Exception {
        if (frase == null || frase.isBlank()) throw new Exception("Escriba una frase para extraer los datos.");
        String texto = frase.trim();
        Datos datos = new Datos();
        datos.titulo = texto.length() > 150 ? texto.substring(0, 150) : texto;
        datos.fecha = fecha(texto.toLowerCase(Locale.ROOT));
        LocalTime[] horas = horas(texto.toLowerCase(Locale.ROOT));
        datos.inicio = horas[0];
        datos.fin = horas[1];
        datos.categorias = categorias(texto.toLowerCase(Locale.ROOT), categorias);
        if (datos.fecha == null && datos.inicio == null && datos.categorias.isEmpty()) {
            throw new Exception("No se pudieron extraer fecha, hora ni categorías de la frase.");
        }
        return datos;
    }

    private LocalDate fecha(String texto) {
        if (texto.contains("pasado mañana")) return hoy.plusDays(2);
        if (texto.contains("mañana")) return hoy.plusDays(1);
        if (texto.contains("hoy")) return hoy;
        Matcher numerica = Pattern.compile("(\\d{1,2})/(\\d{1,2})/(\\d{4})").matcher(texto);
        if (numerica.find()) {
            return LocalDate.of(Integer.parseInt(numerica.group(3)),
                    Integer.parseInt(numerica.group(2)), Integer.parseInt(numerica.group(1)));
        }
        StringBuilder meses = new StringBuilder();
        for (int i = 0; i < MESES.length; i++) {
            if (i > 0) meses.append("|");
            meses.append(MESES[i]);
        }
        Matcher escrita = Pattern.compile("(\\d{1,2})\\s+de\\s+(" + meses + ")(?:\\s+de\\s+(\\d{4}))?").matcher(texto);
        if (escrita.find()) {
            int mes = 1;
            for (int i = 0; i < MESES.length; i++) if (MESES[i].equals(escrita.group(2))) mes = i + 1;
            int anio = escrita.group(3) == null ? hoy.getYear() : Integer.parseInt(escrita.group(3));
            return LocalDate.of(anio, mes, Integer.parseInt(escrita.group(1)));
        }
        return null;
    }

    private LocalTime[] horas(String texto) {
        Matcher deLas = Pattern.compile("de\\s+las\\s+(\\d{1,2})(?::(\\d{2}))?\\s+a\\s+las\\s+(\\d{1,2})(?::(\\d{2}))?")
                .matcher(texto);
        if (deLas.find()) return par(deLas.group(1), deLas.group(2), deLas.group(3), deLas.group(4));
        Matcher reloj = Pattern.compile("(\\d{1,2}):(\\d{2})\\s*(?:a|hasta|-)\\s*(\\d{1,2}):(\\d{2})").matcher(texto);
        if (reloj.find()) return par(reloj.group(1), reloj.group(2), reloj.group(3), reloj.group(4));
        Matcher simple = Pattern.compile("\\bde\\s+(\\d{1,2})\\s+a\\s+(\\d{1,2})\\b").matcher(texto);
        if (simple.find()) return par(simple.group(1), null, simple.group(2), null);
        return new LocalTime[]{null, null};
    }

    private LocalTime[] par(String h1, String m1, String h2, String m2) {
        LocalTime inicio = hora(h1, m1);
        LocalTime fin = hora(h2, m2);
        if (inicio == null || fin == null) return new LocalTime[]{null, null};
        return new LocalTime[]{inicio, fin};
    }

    private LocalTime hora(String hora, String minuto) {
        int h = Integer.parseInt(hora);
        int m = minuto == null ? 0 : Integer.parseInt(minuto);
        if (h < 0 || h > 23 || m < 0 || m > 59) return null;
        return LocalTime.of(h, m);
    }

    private List<Categoria> categorias(String texto, List<Categoria> categorias) {
        List<Categoria> encontradas = new ArrayList<>();
        if (categorias == null) return encontradas;
        List<Categoria> orden = new ArrayList<>(categorias);
        orden.sort((a, b) -> Integer.compare(b.getEtiqueta().length(), a.getEtiqueta().length()));
        for (Categoria categoria : orden) {
            String etiqueta = categoria.getEtiqueta().toLowerCase(Locale.ROOT);
            if (!etiqueta.isBlank() && texto.contains(etiqueta)) encontradas.add(categoria);
        }
        return encontradas;
    }

    public static class Datos {
        public String titulo;
        public LocalDate fecha;
        public LocalTime inicio;
        public LocalTime fin;
        public List<Categoria> categorias = new ArrayList<>();
    }
}
