package com.taller.mecanica;

public class BatteryCellPart implements SparePart {
    @Override
    public String getPartName() {
        return "Modulo de celdas de bateria de litio";
    }

    @Override
    public double getPrice() {
        return 890.00;
    }
}
