package com.autoshop.mechanic;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive console front-end for the shop. The user picks a vehicle
 * family and types in the reported symptoms; the corresponding
 * VehicleFactory is built and the AI diagnosis runs live against Groq.
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== AutoAI Repair Shop ===");
        System.out.println("Type 'exit' at any prompt to quit.\n");

        while (true) {
            String vehicleType = askVehicleType(scanner);
            if (vehicleType == null) {
                break;
            }

            String licensePlate = askLicensePlate(scanner);
            if (licensePlate == null) {
                break;
            }

            List<String> symptoms = askSymptoms(scanner);
            if (symptoms == null) {
                break;
            }
            if (symptoms.isEmpty()) {
                System.out.println("No symptoms entered, skipping this vehicle.\n");
                continue;
            }

            VehicleFactory factory = vehicleType.equals("electric")
                    ? new ElectricVehicleFactory()
                    : new GasolineVehicleFactory();

            new AutoRepairShop(factory).serviceVehicle(licensePlate, symptoms);

            if (!askContinue(scanner)) {
                break;
            }
            System.out.println();
        }

        System.out.println("Thanks for visiting AutoAI Repair Shop. Goodbye!");
        scanner.close();
    }

    private static String askVehicleType(Scanner scanner) {
        while (true) {
            System.out.print("Vehicle type - [1] Gasoline  [2] Electric  (or 'exit'): ");
            String input = scanner.nextLine().trim();
            if (isExit(input)) {
                return null;
            }
            if (input.equals("1")) {
                return "gasoline";
            }
            if (input.equals("2")) {
                return "electric";
            }
            System.out.println("Please enter 1 or 2.");
        }
    }

    private static String askLicensePlate(Scanner scanner) {
        System.out.print("License plate (e.g. ABC-123): ");
        String input = scanner.nextLine().trim();
        if (isExit(input)) {
            return null;
        }
        return input.isEmpty() ? "UNKNOWN" : input;
    }

    private static List<String> askSymptoms(Scanner scanner) {
        System.out.println("Enter symptoms one per line. Press Enter on an empty line to finish.");
        List<String> symptoms = new ArrayList<>();
        while (true) {
            System.out.print("  symptom> ");
            String input = scanner.nextLine().trim();
            if (isExit(input)) {
                return null;
            }
            if (input.isEmpty()) {
                return symptoms;
            }
            symptoms.add(input);
        }
    }

    private static boolean askContinue(Scanner scanner) {
        System.out.print("Service another vehicle? (y/n): ");
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("y") || input.equals("yes");
    }

    private static boolean isExit(String input) {
        return input.equalsIgnoreCase("exit");
    }
}
