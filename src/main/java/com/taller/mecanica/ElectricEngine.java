package com.taller.mecanica;

public class ElectricEngine implements Engine {
    @Override
    public void start() {
        System.out.println("Activando motor electrico... silencioso y listo.");
    }

    @Override
    public String getSpecs() {
        return "Motor electrico sincrono, 150kW, baterias de iones de litio 60kWh";
    }
}
