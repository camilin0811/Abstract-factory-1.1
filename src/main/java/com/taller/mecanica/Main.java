package com.taller.mecanica;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        TallerMecanico tallerGasolina = new TallerMecanico(new GasolineVehicleFactory());
        tallerGasolina.atenderVehiculo(
                "ABC-123",
                List.of("dificultad para arrancar", "humo negro")
        );

        TallerMecanico tallerElectrico = new TallerMecanico(new ElectricVehicleFactory());
        tallerElectrico.atenderVehiculo(
                "EV-777",
                List.of("autonomia reducida", "no carga")
        );
    }
}
