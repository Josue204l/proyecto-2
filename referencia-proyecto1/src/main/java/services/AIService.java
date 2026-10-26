package services;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import logic.Categoria;
import logic.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class AIService {

    private final ReservaExtractorService extractorService;

    public AIService() {
        // Opción 1: Conexión con OpenAI GPT (Configura tu API Key o variable de entorno)
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = "demo"; // Clave demo/fallback para pruebas sin crash
        }

        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gpt-4o-mini")
                .temperature(0.0) // 0.0 para respuestas estructuradas y precisas
                .build();

        // Construcción dinámica de la interfaz con LangChain4j
        this.extractorService = AiServices.create(ReservaExtractorService.class, model);
    }

    /**
     * Método invocado por ControllerReserva.
     * Envía las categorías registradas en la app y la fecha de hoy a LangChain4j.
     */
    public ReservaExtraccion extraerReserva(String frase) {
        if (frase == null || frase.isBlank()) {
            return new ReservaExtraccion();
        }

        // 1. Obtener la lista de categorías registradas en el sistema
        String categoriasStr = "";
        try {
            List<Categoria> categoriasList = Service.getInstancia() != null
                    ? Service.getInstancia().getCategorias()
                    : Service.instance().getCategorias();

            if (categoriasList != null) {
                categoriasStr = categoriasList.stream()
                        .map(Categoria::getEtiqueta)
                        .collect(Collectors.joining(", "));
            }
        } catch (Exception ignored) {
            categoriasStr = "Laboratorio, Proyector, Auditorio, Computadoras, Sala de Reuniones";
        }

        // 2. Obtener la fecha actual para el parámetro {{hoy}}
        String hoyStr = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        // 3. Llamar al servicio de LangChain4j
        ReservaExtraccion resultado = extractorService.extraer(frase, categoriasStr, hoyStr);

        return resultado != null ? resultado : new ReservaExtraccion();
    }
}