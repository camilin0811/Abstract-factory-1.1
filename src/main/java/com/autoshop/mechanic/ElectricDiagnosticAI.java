package com.autoshop.mechanic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Diagnostic AI specialized in electric vehicles.
 * Uses the same expert-system logic as its gasoline counterpart, but with
 * its own knowledge base of electric/electronic faults.
 */
public class ElectricDiagnosticAI implements DiagnosticAI {

    private final Map<String, Map<String, Integer>> knowledgeBase = new HashMap<>();
    private final Map<String, String> recommendations = new HashMap<>();

    public ElectricDiagnosticAI() {
        knowledgeBase.put("reduced range", Map.of(
                "Battery degradation", 5,
                "Outdated management software", 2
        ));
        knowledgeBase.put("not charging", Map.of(
                "Faulty charger", 4,
                "Damaged charging connector", 3,
                "Faulty on-board charging module", 3
        ));
        knowledgeBase.put("motor noise", Map.of(
                "Worn bearings", 4,
                "Rotor failure", 3
        ));
        knowledgeBase.put("check engine light", Map.of(
                "BMS (battery management system) failure", 5
        ));
        knowledgeBase.put("vibration when accelerating", Map.of(
                "Electric motor misalignment", 3,
                "Inverter failure", 3
        ));
        knowledgeBase.put("battery overheating", Map.of(
                "Battery cooling system failure", 5,
                "Damaged cells", 4
        ));

        recommendations.put("Battery degradation", "Run a battery State of Health diagnostic.");
        recommendations.put("Outdated management software", "Update the battery management system firmware.");
        recommendations.put("Faulty charger", "Test with another charger and inspect this one.");
        recommendations.put("Damaged charging connector", "Inspect and replace the charging connector.");
        recommendations.put("Faulty on-board charging module", "Run an electronic diagnostic of the OBC module.");
        recommendations.put("Worn bearings", "Replace the motor bearings.");
        recommendations.put("Rotor failure", "Inspect and balance the rotor.");
        recommendations.put("BMS (battery management system) failure", "Run a specialized scan and inspect the BMS.");
        recommendations.put("Electric motor misalignment", "Check motor mounts and alignment.");
        recommendations.put("Inverter failure", "Run a power inverter diagnostic.");
        recommendations.put("Battery cooling system failure", "Check the battery coolant pump and fluid.");
        recommendations.put("Damaged cells", "Inspect the cells; the module may need replacement.");
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
