package com.autoshop.mechanic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Diagnostic AI specialized in internal combustion engines.
 *
 * Simulates an inference engine (expert system): every reported symptom
 * adds "suspicion points" to possible faults according to a knowledge base
 * built from real shop experience. The fault with the highest score is
 * the diagnosis conclusion.
 */
public class GasolineDiagnosticAI implements DiagnosticAI {

    private final Map<String, Map<String, Integer>> knowledgeBase = new HashMap<>();
    private final Map<String, String> recommendations = new HashMap<>();

    public GasolineDiagnosticAI() {
        knowledgeBase.put("metallic noise", Map.of(
                "Damaged connecting rod or bearings", 5,
                "Low engine oil", 3
        ));
        knowledgeBase.put("black smoke", Map.of(
                "Fuel mixture too rich", 4,
                "Faulty injector", 3
        ));
        knowledgeBase.put("white smoke", Map.of(
                "Damaged head gasket", 5,
                "Coolant burning in the chamber", 4
        ));
        knowledgeBase.put("difficulty starting", Map.of(
                "Worn spark plugs", 4,
                "Low battery", 3,
                "Faulty fuel pump", 2
        ));
        knowledgeBase.put("smell of gasoline", Map.of(
                "Fuel system leak", 5
        ));
        knowledgeBase.put("loss of power", Map.of(
                "Clogged air filter", 3,
                "Worn spark plugs", 3,
                "Faulty oxygen sensor", 2
        ));
        knowledgeBase.put("idle vibration", Map.of(
                "Worn spark plugs", 3,
                "Misaligned engine", 2
        ));

        recommendations.put("Damaged connecting rod or bearings", "Stop the vehicle and tow it to the shop; risk of major damage.");
        recommendations.put("Low engine oil", "Check oil level and top it off immediately.");
        recommendations.put("Fuel mixture too rich", "Check oxygen sensors and injectors.");
        recommendations.put("Faulty injector", "Clean or replace the injectors.");
        recommendations.put("Damaged head gasket", "Urgent inspection; can cause overheating.");
        recommendations.put("Coolant burning in the chamber", "Check the cooling system and head gasket.");
        recommendations.put("Worn spark plugs", "Replace the spark plug set.");
        recommendations.put("Low battery", "Charge or replace the battery.");
        recommendations.put("Faulty fuel pump", "Run a fuel pressure diagnostic.");
        recommendations.put("Fuel system leak", "Immediate inspection; fire hazard.");
        recommendations.put("Clogged air filter", "Replace the air filter.");
        recommendations.put("Faulty oxygen sensor", "Run an OBD-II scan and replace the sensor.");
        recommendations.put("Misaligned engine", "Check engine mounts and supports.");
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
            return new DiagnosticResult("No matches found in the knowledge base", 0,
                    "A general shop inspection is recommended.");
        }

        String bestFault = scores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get().getKey();

        int totalScore = scores.values().stream().mapToInt(Integer::intValue).sum();
        int confidence = (int) Math.round((scores.get(bestFault) * 100.0) / totalScore);

        return new DiagnosticResult(bestFault, confidence,
                recommendations.getOrDefault(bestFault, "A general inspection is recommended."));
    }
}
