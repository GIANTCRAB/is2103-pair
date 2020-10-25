package org.example;

import controllers.FlightRouteBeanRemote;
import entities.Employee;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Scanner;

@RequiredArgsConstructor
public class FlightRouteClient implements SystemClient {
    @NonNull
    private final Scanner scanner;
    @NonNull
    private final Employee authenticatedEmployee;
    @NonNull
    private final FlightRouteBeanRemote flightRouteBeanRemote;

    @Override
    public void runApp() {

    }

    private void displayFlightRoutePlannerMenu() {
        boolean loop = true;
        while (loop) {
            System.out.println("*** Flight Route Planner ***");
            System.out.println("1: Create Flight Route");
            System.out.println("2: View All Flight Routes");
            System.out.println("3: Delete Flight Route");
            System.out.println("4: Exit");

            final int option = this.scanner.nextInt();

            switch (option) {
                case 1:
                    this.displayCreateFlightRouteMenu();
                case 4:
                default:
                    loop = false;
                    break;
            }
        }
    }

    private void displayCreateFlightRouteMenu() {

    }
}
