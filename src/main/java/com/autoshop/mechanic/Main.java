package com.autoshop.mechanic;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        AutoRepairShop gasolineShop = new AutoRepairShop(new GasolineVehicleFactory());
        gasolineShop.serviceVehicle(
                "ABC-123",
                List.of("difficulty starting", "black smoke")
        );

        AutoRepairShop electricShop = new AutoRepairShop(new ElectricVehicleFactory());
        electricShop.serviceVehicle(
                "EV-777",
                List.of("reduced range", "not charging")
        );
    }
}
