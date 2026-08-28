package com.taller.mecanica;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IA de diagnostico que consulta un modelo de lenguaje real a traves de la
 * API de Groq. El tipo de vehiculo (gasolina/electrico) se inyecta en el
 * prompt para que el modelo razone con el contexto correcto de cada
 * familia de productos.
 */
public class GroqDiagnosticAI implements DiagnosticAI {

    private static final Pattern RESPONSE_PATTERN = Pattern.compile(
            "FALLA:\\s*(?<falla>.+?)\\s*\\n\\s*CONFIANZA:\\s*(?<confianza>\\d+)\\s*\\n\\s*RECOMENDACION:\\s*(?<recomendacion>.+)",
            Pattern.DOTALL);

    private final String vehicleType;

    public GroqDiagnosticAI(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    @Override
    public DiagnosticResult diagnose(List<String> symptoms) {
        String systemPrompt =
                "Eres un mecanico experto en vehiculos de tipo " + vehicleType + ". "
                        + "Analiza los sintomas que reporta el cliente del taller y responde "
                        + "EXCLUSIVAMENTE con este formato de exactamente 3 lineas, sin texto adicional:\n"
                        + "FALLA: <nombre breve de la falla mas probable>\n"
                        + "CONFIANZA: <numero entero entre 0 y 100>\n"
                        + "RECOMENDACION: <accion concreta que debe tomar el taller>";

        String userPrompt = "Sintomas reportados: " + String.join(", ", symptoms);

        try {
            GroqClient client = new GroqClient();
            String content = client.chat(systemPrompt, userPrompt);
            Matcher matcher = RESPONSE_PATTERN.matcher(content.trim());
            if (matcher.find()) {
                return new DiagnosticResult(
                        matcher.group("falla").trim(),
                        Integer.parseInt(matcher.group("confianza").trim()),
                        matcher.group("recomendacion").trim());
            }
            return new DiagnosticResult("Respuesta de IA en formato inesperado", 0, content.trim());
        } catch (IllegalStateException e) {
            return new DiagnosticResult("IA no configurada", 0, e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new DiagnosticResult("Error al consultar la IA (Groq)", 0, e.getMessage());
        } catch (IOException e) {
            return new DiagnosticResult("Error al consultar la IA (Groq)", 0, e.getMessage());
        }
    }
}
