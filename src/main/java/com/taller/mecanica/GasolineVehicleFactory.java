package com.taller.mecanica;

public class GasolineVehicleFactory implements VehicleFactory {
    @Override
    public Engine createEngine() {
        return new GasolineEngine();
    }

    @Override
    public SparePart createSparePart() {
        return new SparkPlugPart();
    }

    @Override
    public DiagnosticAI createDiagnosticAI() {
        return new GasolineDiagnosticAI();
    }
}
