package com.autoshop.mechanic;

/**
 * Abstract factory: defines the family of products the shop needs
 * to service a given vehicle type (gasoline or electric).
 */
public interface VehicleFactory {
    Engine createEngine();
    SparePart createSparePart();
    DiagnosticAI createDiagnosticAI();
}
