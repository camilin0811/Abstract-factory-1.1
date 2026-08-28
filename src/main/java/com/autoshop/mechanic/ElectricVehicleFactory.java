package com.autoshop.mechanic;

public class ElectricVehicleFactory implements VehicleFactory {
    @Override
    public Engine createEngine() {
        return new ElectricEngine();
    }

    @Override
    public SparePart createSparePart() {
        return new BatteryCellPart();
    }

    @Override
    public DiagnosticAI createDiagnosticAI() {
        return new GroqDiagnosticAI("electric");
    }
}
