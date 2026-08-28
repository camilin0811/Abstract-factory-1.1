package com.autoshop.mechanic;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Diagnostic AI that queries a real language model through the Groq API.
 * The vehicle type (gasoline/electric) is injected into the prompt so the
 * model reasons with the correct context for each product family.
 */
public class GroqDiagnosticAI implements DiagnosticAI {

    private static final Pattern RESPONSE_PATTERN = Pattern.compile(
            "FAULT:\\s*(?<fault>.+?)\\s*\\n\\s*CONFIDENCE:\\s*(?<confidence>\\d+)\\s*\\n\\s*RECOMMENDATION:\\s*(?<recommendation>.+)",
            Pattern.DOTALL);

    private final String vehicleType;

    public GroqDiagnosticAI(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    @Override
    public DiagnosticResult diagnose(List<String> symptoms) {
        String systemPrompt =
                "You are an expert mechanic for " + vehicleType + " vehicles. "
                        + "Analyze the symptoms reported by the shop's customer and reply "
                        + "EXCLUSIVELY with this exact 3-line format, no extra text:\n"
                        + "FAULT: <short name of the most likely fault>\n"
                        + "CONFIDENCE: <integer between 0 and 100>\n"
                        + "RECOMMENDATION: <concrete action the shop should take>";

        String userPrompt = "Reported symptoms: " + String.join(", ", symptoms);

        try {
            GroqClient client = new GroqClient();
            String content = client.chat(systemPrompt, userPrompt);
            Matcher matcher = RESPONSE_PATTERN.matcher(content.trim());
            if (matcher.find()) {
                return new DiagnosticResult(
                        matcher.group("fault").trim(),
                        Integer.parseInt(matcher.group("confidence").trim()),
                        matcher.group("recommendation").trim());
            }
            return new DiagnosticResult("Unexpected AI response format", 0, content.trim());
        } catch (IllegalStateException e) {
            return new DiagnosticResult("AI not configured", 0, e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new DiagnosticResult("Error contacting the AI (Groq)", 0, e.getMessage());
        } catch (IOException e) {
            return new DiagnosticResult("Error contacting the AI (Groq)", 0, e.getMessage());
        }
    }
}
