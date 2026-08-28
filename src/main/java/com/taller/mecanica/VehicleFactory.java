package com.taller.mecanica;

/**
 * Fábrica abstracta: define la familia de productos que el taller
 * necesita para atender un tipo de vehículo (gasolina o eléctrico).
 */
public interface VehicleFactory {
    Engine createEngine();
    SparePart createSparePart();
    DiagnosticAI createDiagnosticAI();
}
