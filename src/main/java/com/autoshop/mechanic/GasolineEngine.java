package com.autoshop.mechanic;

public class GasolineEngine implements Engine {
    @Override
    public void start() {
        System.out.println("Starting internal combustion engine... vroom!");
    }

    @Override
    public String getSpecs() {
        return "4-cylinder gasoline engine, 1.6L, electronic fuel injection";
    }
}
