package com.autoshop.mechanic;

public class BatteryCellPart implements SparePart {
    @Override
    public String getPartName() {
        return "Lithium battery cell module";
    }

    @Override
    public double getPrice() {
        return 890.00;
    }
}
