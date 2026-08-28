package com.autoshop.mechanic;

/**
 * Result produced by a DiagnosticAI: the most likely fault, the computed
 * confidence level, and the recommendation for the customer.
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
        return probableFault + " (confidence: " + confidencePercentage + "%) -> " + recommendation;
    }
}
