package com.autoshop.mechanic;

public class ElectricEngine implements Engine {
    @Override
    public void start() {
        System.out.println("Activating electric motor... quiet and ready.");
    }

    @Override
    public String getSpecs() {
        return "Synchronous electric motor, 150kW, 60kWh lithium-ion battery pack";
    }
}
