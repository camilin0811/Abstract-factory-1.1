package com.autoshop.mechanic;

import java.util.List;
import java.util.Locale;

/**
 * Client of the pattern: only knows the VehicleFactory interface, never
 * the concrete classes. That is why it can service any vehicle family
 * without changing its own code.
 */
public class AutoRepairShop {

    private final VehicleFactory factory;

    public AutoRepairShop(VehicleFactory factory) {
        this.factory = factory;
    }

    public void serviceVehicle(String licensePlate, List<String> symptoms) {
        Engine engine = factory.createEngine();
        SparePart part = factory.createSparePart();
        DiagnosticAI ai = factory.createDiagnosticAI();

        System.out.println("=== Servicing vehicle " + licensePlate + " ===");
        engine.start();
        System.out.println("Technical specs: " + engine.getSpecs());
        System.out.printf(Locale.US, "Suggested spare part in stock: %s ($%.2f)%n", part.getPartName(), part.getPrice());

        DiagnosticResult result = ai.diagnose(symptoms);
        System.out.println("AI diagnosis -> " + result);
        System.out.println();
    }
}
