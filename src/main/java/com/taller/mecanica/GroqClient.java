package com.taller.mecanica;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Properties;

/**
 * Cliente minimo para la API de Groq (compatible con el formato de Chat
 * Completions de OpenAI). No depende de librerias externas: solo usa
 * java.net.http y JsonUtil para construir/leer los mensajes JSON.
 */
public class GroqClient {

    private static final String ENDPOINT = "https://api.groq.com/openai/v1/chat/completions";
    private static final String DEFAULT_MODEL = "openai/gpt-oss-120b";

    private final String apiKey;
    private final String model;
    private final HttpClient httpClient;

    public GroqClient() {
        this.apiKey = resolveApiKey();
        this.model = System.getenv().getOrDefault("GROQ_MODEL", DEFAULT_MODEL);
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    private String resolveApiKey() {
        String fromEnv = System.getenv("GROQ_API_KEY");
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv.trim();
        }

        Path propsFile = Path.of("groq.properties");
        if (Files.exists(propsFile)) {
            try (var in = new FileInputStream(propsFile.toFile())) {
                Properties props = new Properties();
                props.load(in);
                String fromFile = props.getProperty("GROQ_API_KEY");
                if (fromFile != null && !fromFile.isBlank()) {
                    return fromFile.trim();
                }
            } catch (IOException ignored) {
                // Se reporta como clave faltante mas abajo.
            }
        }

        throw new IllegalStateException(
                "No se encontro GROQ_API_KEY. Define la variable de entorno GROQ_API_KEY "
                        + "o crea un archivo groq.properties (ver groq.properties.example) "
                        + "con tu clave de https://console.groq.com/keys");
    }

    public String chat(String systemPrompt, String userPrompt) throws IOException, InterruptedException {
        String requestBody = "{"
                + "\"model\":\"" + JsonUtil.escape(model) + "\","
                + "\"temperature\":0.2,"
                + "\"messages\":["
                + "{\"role\":\"system\",\"content\":\"" + JsonUtil.escape(systemPrompt) + "\"},"
                + "{\"role\":\"user\",\"content\":\"" + JsonUtil.escape(userPrompt) + "\"}"
                + "]}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT))
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Groq respondio con codigo " + response.statusCode() + ": " + response.body());
        }

        return JsonUtil.extractStringField(response.body(), "content")
                .orElseThrow(() -> new IOException(
                        "No se pudo leer el campo 'content' en la respuesta de Groq: " + response.body()));
    }
}
