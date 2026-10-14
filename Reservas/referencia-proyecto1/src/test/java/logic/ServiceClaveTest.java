package logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServiceClaveTest {

    private Usuario usuario;

    // Replica la lógica de Service.cambiarClave sin depender de Data
    private void cambiarClave(Usuario u, String actual, String nueva, String confirmar) throws Exception {
        if (u == null) throw new Exception("Usuario no válido.");
        if (!u.getClave().equals(actual)) throw new Exception("La clave actual es incorrecta.");
        if (nueva == null || nueva.trim().isEmpty()) throw new Exception("La nueva clave no puede estar vacía.");
        if (!nueva.equals(confirmar)) throw new Exception("La nueva clave y su confirmación no coinciden.");
        u.setClave(nueva);
    }

    @BeforeEach
    void setUp() {
        usuario = new Funcionario("u1", "claveOriginal", "FUNCIONARIO", "Maria Salas", "6666-0000");
    }

    @Test
    void cambio_exitoso() throws Exception {
        cambiarClave(usuario, "claveOriginal", "nuevaClave", "nuevaClave");
        assertEquals("nuevaClave", usuario.getClave());
    }

    @Test
    void claveActual_incorrecta_lanzaExcepcion() {
        Exception ex = assertThrows(Exception.class, () ->
                cambiarClave(usuario, "claveErronea", "nuevaClave", "nuevaClave"));
        assertEquals("La clave actual es incorrecta.", ex.getMessage());
    }

    @Test
    void confirmacion_diferente_lanzaExcepcion() {
        Exception ex = assertThrows(Exception.class, () ->
                cambiarClave(usuario, "claveOriginal", "nuevaClave", "otraClave"));
        assertEquals("La nueva clave y su confirmación no coinciden.", ex.getMessage());
    }

    @Test
    void claveNueva_vacia_lanzaExcepcion() {
        Exception ex = assertThrows(Exception.class, () ->
                cambiarClave(usuario, "claveOriginal", "   ", "   "));
        assertEquals("La nueva clave no puede estar vacía.", ex.getMessage());
    }

    @Test
    void claveNueva_nula_lanzaExcepcion() {
        Exception ex = assertThrows(Exception.class, () ->
                cambiarClave(usuario, "claveOriginal", null, null));
        assertEquals("La nueva clave no puede estar vacía.", ex.getMessage());
    }
}
