package frontend.red;

import com.fasterxml.jackson.databind.JsonNode;
import protocolo.EventoServidor;
import protocolo.JsonProtocolo;
import protocolo.MensajeRespuesta;
import protocolo.MensajeSolicitud;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/** Cliente TCP con un hilo lector dedicado para respuestas y eventos asincrónicos. */
public final class ClienteSocket implements AutoCloseable {
    private final Socket socket = new Socket();
    private final BufferedReader entrada;
    private final BufferedWriter salida;
    private final Object candadoSalida = new Object();
    private final Map<String, CompletableFuture<MensajeRespuesta>> pendientes = new ConcurrentHashMap<>();
    private volatile Consumer<EventoServidor> receptorEventos = evento -> { };
    private volatile boolean cerrado;

    public ClienteSocket(String host, int puerto) throws IOException {
        socket.connect(new InetSocketAddress(host, puerto), (int) Duration.ofSeconds(5).toMillis());
        socket.setKeepAlive(true);
        entrada = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        salida = new BufferedWriter(new java.io.OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        Thread lector = new Thread(this::leer, "frontend-socket-reader");
        lector.setDaemon(true);
        lector.start();
    }

    public void onEvento(Consumer<EventoServidor> receptor) {
        receptorEventos = receptor == null ? evento -> { } : receptor;
    }

    public MensajeRespuesta solicitar(String operacion, JsonNode datos) throws Exception {
        if (cerrado) throw new IOException("La conexión con el Backend está cerrada.");
        MensajeSolicitud solicitud = new MensajeSolicitud(UUID.randomUUID().toString(), operacion, datos);
        CompletableFuture<MensajeRespuesta> futuro = new CompletableFuture<>();
        pendientes.put(solicitud.id(), futuro);
        try {
            synchronized (candadoSalida) {
                salida.write(JsonProtocolo.codificar(solicitud));
                salida.newLine();
                salida.flush();
            }
            MensajeRespuesta respuesta = futuro.get(30, TimeUnit.SECONDS);
            if (!respuesta.correcta()) throw new IOException(respuesta.error());
            return respuesta;
        } finally {
            pendientes.remove(solicitud.id());
        }
    }

    private void leer() {
        try {
            String linea;
            while (!cerrado && (linea = entrada.readLine()) != null) {
                JsonNode nodo = JsonProtocolo.mapper().readTree(linea);
                if (nodo.has("correcta")) {
                    MensajeRespuesta respuesta = JsonProtocolo.mapper().treeToValue(nodo, MensajeRespuesta.class);
                    CompletableFuture<MensajeRespuesta> futuro = pendientes.get(respuesta.id());
                    if (futuro != null) futuro.complete(respuesta);
                } else {
                    EventoServidor evento = JsonProtocolo.mapper().treeToValue(nodo, EventoServidor.class);
                    receptorEventos.accept(evento);
                }
            }
            if (!cerrado) fallarPendientes(new IOException("El Backend cerró la conexión."));
        } catch (Exception e) {
            if (!cerrado) fallarPendientes(e);
        }
    }

    private void fallarPendientes(Exception error) {
        pendientes.values().forEach(futuro -> futuro.completeExceptionally(error));
    }

    @Override
    public void close() {
        cerrado = true;
        fallarPendientes(new IOException("Cliente desconectado."));
        try { socket.close(); } catch (IOException ignored) { }
    }
}
