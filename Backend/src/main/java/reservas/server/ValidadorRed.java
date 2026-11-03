package reservas.server;

public final class ValidadorRed {
    private ValidadorRed() { }

    public static String texto(Object valor, String campo) throws Exception {
        if (!(valor instanceof String texto)) {
            throw new Exception("Dato de red inválido: " + campo + " debe ser texto.");
        }
        return texto;
    }

    public static java.time.LocalDate fecha(Object valor, String campo) throws Exception {
        if (!(valor instanceof java.time.LocalDate fecha)) {
            throw new Exception("Dato de red inválido: " + campo + " debe ser una fecha.");
        }
        return fecha;
    }

    public static java.time.LocalTime hora(Object valor, String campo) throws Exception {
        if (!(valor instanceof java.time.LocalTime hora)) {
            throw new Exception("Dato de red inválido: " + campo + " debe ser una hora.");
        }
        return hora;
    }

    public static <T> T tipo(Object valor, Class<T> clase, String campo) throws Exception {
        if (valor == null || !clase.isInstance(valor)) {
            throw new Exception("Dato de red inválido: " + campo + " no tiene el tipo esperado.");
        }
        return clase.cast(valor);
    }

    public static java.util.List<String> ids(Object valor, String campo) throws Exception {
        if (!(valor instanceof java.util.List<?> lista)) {
            throw new Exception("Dato de red inválido: " + campo + " debe ser una lista de id.");
        }
        java.util.List<String> ids = new java.util.ArrayList<>();
        for (Object item : lista) {
            if (!(item instanceof String id) || id.isBlank()) {
                throw new Exception("Dato de red inválido: cada elemento de " + campo + " debe ser un id de texto.");
            }
            ids.add(id);
        }
        return ids;
    }
}
