package com.taller.mecanica;

public class GasolineEngine implements Engine {
    @Override
    public void start() {
        System.out.println("Encendiendo motor de combustión interna... vroom!");
    }

    @Override
    public String getSpecs() {
        return "Motor a gasolina 4 cilindros, 1.6L, inyección electrónica";
    }
}
