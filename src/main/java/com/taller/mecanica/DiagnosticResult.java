package com.taller.mecanica;

/**
 * Resultado producido por un DiagnosticAI: la falla más probable,
 * el nivel de confianza calculado y la recomendación para el cliente.
 */
public class DiagnosticResult {
    private final String probableFault;
    private final int confidencePercentage;
    private final String recommendation;

    public DiagnosticResult(String probableFault, int confidencePercentage, String recommendation) {
        this.probableFault = probableFault;
        this.confidencePercentage = confidencePercentage;
        this.recommendation = recommendation;
    }

    public String getProbableFault() {
        return probableFault;
    }

    public int getConfidencePercentage() {
        return confidencePercentage;
    }

    public String getRecommendation() {
        return recommendation;
    }

    @Override
    public String toString() {
        return probableFault + " (confianza: " + confidencePercentage + "%) -> " + recommendation;
    }
}
