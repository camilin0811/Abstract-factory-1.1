package com.taller.mecanica;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IA de diagnóstico especializada en motores de combustión interna.
 *
 * Simula un motor de inferencia (sistema experto): cada síntoma reportado
 * suma "puntos de sospecha" a posibles fallas según una base de conocimiento
 * construida con experiencia real de taller. La falla con mayor puntaje
 * es la conclusión del diagnóstico.
 */
public class GasolineDiagnosticAI implements DiagnosticAI {

    private final Map<String, Map<String, Integer>> knowledgeBase = new HashMap<>();
    private final Map<String, String> recommendations = new HashMap<>();

    public GasolineDiagnosticAI() {
        knowledgeBase.put("ruido metalico", Map.of(
                "Biela o cojinetes danados", 5,
                "Falta de aceite en el motor", 3
        ));
        knowledgeBase.put("humo negro", Map.of(
                "Mezcla de combustible demasiado rica", 4,
                "Inyector defectuoso", 3
        ));
        knowledgeBase.put("humo blanco", Map.of(
                "Junta de culata danada", 5,
                "Refrigerante quemandose en la camara", 4
        ));
        knowledgeBase.put("dificultad para arrancar", Map.of(
                "Bujias desgastadas", 4,
                "Bateria baja", 3,
                "Bomba de combustible defectuosa", 2
        ));
        knowledgeBase.put("olor a gasolina", Map.of(
                "Fuga en el sistema de combustible", 5
        ));
        knowledgeBase.put("perdida de potencia", Map.of(
                "Filtro de aire obstruido", 3,
                "Bujias desgastadas", 3,
                "Sensor de oxigeno defectuoso", 2
        ));
        knowledgeBase.put("vibracion en ralenti", Map.of(
                "Bujias desgastadas", 3,
                "Motor desalineado", 2
        ));

        recommendations.put("Biela o cojinetes danados", "Detener el vehiculo y remolcar al taller; riesgo de dano mayor.");
        recommendations.put("Falta de aceite en el motor", "Revisar nivel de aceite y completar de inmediato.");
        recommendations.put("Mezcla de combustible demasiado rica", "Revisar sensores de oxigeno e inyectores.");
        recommendations.put("Inyector defectuoso", "Limpieza o reemplazo de inyectores.");
        recommendations.put("Junta de culata danada", "Inspeccion urgente; puede causar sobrecalentamiento.");
        recommendations.put("Refrigerante quemandose en la camara", "Revisar sistema de refrigeracion y junta de culata.");
        recommendations.put("Bujias desgastadas", "Reemplazar juego de bujias.");
        recommendations.put("Bateria baja", "Cargar o reemplazar la bateria.");
        recommendations.put("Bomba de combustible defectuosa", "Diagnostico de presion de combustible.");
        recommendations.put("Fuga en el sistema de combustible", "Revision inmediata; riesgo de incendio.");
        recommendations.put("Filtro de aire obstruido", "Reemplazar filtro de aire.");
        recommendations.put("Sensor de oxigeno defectuoso", "Escaner OBD-II y reemplazo del sensor.");
        recommendations.put("Motor desalineado", "Revisar soportes y bases del motor.");
    }

    @Override
    public DiagnosticResult diagnose(List<String> symptoms) {
        Map<String, Integer> scores = new HashMap<>();

        for (String symptom : symptoms) {
            String normalized = symptom.toLowerCase().trim();
            for (Map.Entry<String, Map<String, Integer>> entry : knowledgeBase.entrySet()) {
                if (normalized.contains(entry.getKey()) || entry.getKey().contains(normalized)) {
                    entry.getValue().forEach((fault, weight) ->
                            scores.merge(fault, weight, Integer::sum));
                }
            }
        }

        if (scores.isEmpty()) {
            return new DiagnosticResult("Sin coincidencias en la base de conocimiento", 0,
                    "Se recomienda una revision general en taller.");
        }

        String bestFault = scores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get().getKey();

        int totalScore = scores.values().stream().mapToInt(Integer::intValue).sum();
        int confidence = (int) Math.round((scores.get(bestFault) * 100.0) / totalScore);

        return new DiagnosticResult(bestFault, confidence,
                recommendations.getOrDefault(bestFault, "Revision general recomendada."));
    }
}
