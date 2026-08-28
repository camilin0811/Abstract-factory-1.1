package com.taller.mecanica;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IA de diagnostico especializada en vehiculos electricos.
 * Usa la misma logica de sistema experto que su contraparte de gasolina,
 * pero con una base de conocimiento propia de fallas electricas/electronicas.
 */
public class ElectricDiagnosticAI implements DiagnosticAI {

    private final Map<String, Map<String, Integer>> knowledgeBase = new HashMap<>();
    private final Map<String, String> recommendations = new HashMap<>();

    public ElectricDiagnosticAI() {
        knowledgeBase.put("autonomia reducida", Map.of(
                "Degradacion de la bateria", 5,
                "Software de gestion desactualizado", 2
        ));
        knowledgeBase.put("no carga", Map.of(
                "Falla en el cargador", 4,
                "Conector de carga danado", 3,
                "Falla en el modulo de carga a bordo", 3
        ));
        knowledgeBase.put("ruido en el motor", Map.of(
                "Rodamientos desgastados", 4,
                "Falla en el rotor", 3
        ));
        knowledgeBase.put("luz de check engine", Map.of(
                "Falla en el BMS (sistema de gestion de bateria)", 5
        ));
        knowledgeBase.put("vibracion al acelerar", Map.of(
                "Desalineacion del motor electrico", 3,
                "Falla en el inversor", 3
        ));
        knowledgeBase.put("sobrecalentamiento de bateria", Map.of(
                "Falla en el sistema de refrigeracion de bateria", 5,
                "Celdas danadas", 4
        ));

        recommendations.put("Degradacion de la bateria", "Diagnostico de salud de bateria (State of Health).");
        recommendations.put("Software de gestion desactualizado", "Actualizar firmware del sistema de gestion de bateria.");
        recommendations.put("Falla en el cargador", "Probar con otro cargador y revisar el propio.");
        recommendations.put("Conector de carga danado", "Inspeccionar y reemplazar el conector de carga.");
        recommendations.put("Falla en el modulo de carga a bordo", "Diagnostico electronico del modulo OBC.");
        recommendations.put("Rodamientos desgastados", "Reemplazar rodamientos del motor.");
        recommendations.put("Falla en el rotor", "Inspeccion del rotor y balanceo del motor.");
        recommendations.put("Falla en el BMS (sistema de gestion de bateria)", "Escaner especializado y revision del BMS.");
        recommendations.put("Desalineacion del motor electrico", "Revisar soportes y alineacion del motor.");
        recommendations.put("Falla en el inversor", "Diagnostico del inversor de potencia.");
        recommendations.put("Falla en el sistema de refrigeracion de bateria", "Revisar bomba y liquido refrigerante de bateria.");
        recommendations.put("Celdas danadas", "Inspeccion de celdas; posible reemplazo del modulo.");
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
