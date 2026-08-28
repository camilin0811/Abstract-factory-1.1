package com.autoshop.mechanic;

public class SparkPlugPart implements SparePart {
    @Override
    public String getPartName() {
        return "Iridium spark plug set";
    }

    @Override
    public double getPrice() {
        return 45.90;
    }
}
