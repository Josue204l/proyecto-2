package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditoriaService {

    private static final String DIR = "datos/";
    private static final String ARCHIVO_LOG = DIR + "auditoria.log";

    public static void registrarAccion(String usuarioId, String accion, String detalle) {
        try {
            new File(DIR).mkdirs();
            try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_LOG, true))) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                writer.printf("[%s] USUARIO: %s | ACCION: %s | DETALLE: %s%n",
                        timestamp,
                        (usuarioId != null ? usuarioId : "SISTEMA"),
                        accion,
                        detalle);
            }
        } catch (Exception e) {
            System.err.println("Error al escribir en la bitácora de auditoría: " + e.getMessage());
        }
    }
}