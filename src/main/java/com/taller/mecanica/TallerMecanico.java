package com.taller.mecanica;

import java.util.List;

/**
 * Cliente del patron: solo conoce la interfaz VehicleFactory,
 * nunca las clases concretas. Por eso puede atender cualquier
 * familia de vehiculos sin cambiar su codigo.
 */
public class TallerMecanico {

    private final VehicleFactory factory;

    public TallerMecanico(VehicleFactory factory) {
        this.factory = factory;
    }

    public void atenderVehiculo(String placa, List<String> sintomas) {
        Engine engine = factory.createEngine();
        SparePart part = factory.createSparePart();
        DiagnosticAI ai = factory.createDiagnosticAI();

        System.out.println("=== Atendiendo vehiculo " + placa + " ===");
        engine.start();
        System.out.println("Ficha tecnica: " + engine.getSpecs());
        System.out.printf("Repuesto sugerido en stock: %s ($%.2f)%n", part.getPartName(), part.getPrice());

        DiagnosticResult result = ai.diagnose(sintomas);
        System.out.println("Diagnostico IA -> " + result);
        System.out.println();
    }
}
