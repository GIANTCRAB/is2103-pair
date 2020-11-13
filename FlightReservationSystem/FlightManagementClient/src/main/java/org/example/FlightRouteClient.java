package org.example;

import controllers.FlightRouteBeanRemote;
import entities.Employee;
import entities.FlightRoute;
import entities.FlightRouteId;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;
import exceptions.EntityAlreadyExistException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
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
        this.displayFlightRoutePlannerMenu();
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
                    break;
                case 2:
                    this.displayFlightRouteMenu();
                    break;
                case 3:
                    this.displayDeleteFlightRouteMenu();
                    break;
                case 4:
                default:
                    this.displayLogoutMenu();
                    loop = false;
                    break;
            }
        }
    }

    private void displayCreateFlightRouteMenu() {
        System.out.println("*** Create Flight Route ***");
        System.out.println("Enter origin airport code:");
        final String originCode = this.scanner.next();
        System.out.println("Enter destination airport code:");
        final String destinationCode = this.scanner.next();
        System.out.println("Type 1 if you want to create a round trip, else, type 2:");
        final int roundTrip = this.scanner.nextInt();

        try {
            if (roundTrip == 1) {
                this.flightRouteBeanRemote.createRoundTrip(originCode, destinationCode);
                System.out.println("Flight route with return successfully created!");
            } else {
                this.flightRouteBeanRemote.create(originCode, destinationCode);
                System.out.println("Flight route successfully created!");
            }
        } catch (InvalidConstraintException e) {
            this.displayConstraintErrorMessage(e);
        } catch (InvalidEntityIdException e) {
            System.out.println(e.getMessage());
        } catch (NotAuthenticatedException e) {
            System.out.println("You do not have permission to do this!");
        } catch (EntityAlreadyExistException e) {
            System.out.println(e.getMessage());
        }
    }

    private void displayFlightRouteMenu() {
        System.out.println("*** View All Flight Routes ***");

        try {
            final List<FlightRoute> flightRouteList = this.flightRouteBeanRemote.getFlightRoutes();
            for (FlightRoute flightRoute : flightRouteList) {
                System.out.println(flightRoute.getOrigin().getIataCode() + " -> " + flightRoute.getDest().getIataCode());
            }
        } catch (NotAuthenticatedException e) {
            System.out.println("You do not have permission to do this!");
        } catch (InvalidEntityIdException e) {
            System.out.println(e.getMessage());
        }
    }

    private void displayDeleteFlightRouteMenu() {
        System.out.println("*** Delete Flight Route***");
        System.out.println("Enter origin airport code:");
        final String originCode = this.scanner.next();
        System.out.println("Enter destination airport code:");
        final String destinationCode = this.scanner.next();

        final FlightRouteId flightRouteId = new FlightRouteId();
        flightRouteId.setOriginId(originCode);
        flightRouteId.setDestId(destinationCode);
        final FlightRoute flightRoute = new FlightRoute();
        flightRoute.setFlightRouteId(flightRouteId);

        try {
            this.flightRouteBeanRemote.deleteFlightRoute(flightRoute);
            System.out.println("Route deleted/disabled successfully.");
        } catch (InvalidEntityIdException e) {
            System.out.println("Flight route does not exist.");
        } catch (NotAuthenticatedException e) {
            System.out.println("You do not have permission to do this!");
        }
    }

    private void displayConstraintErrorMessage(InvalidConstraintException invalidConstraintException) {
        System.out.println("There were some validation errors!");
        System.out.println(invalidConstraintException.toString());
    }

    private void displayLogoutMenu() {
        try {
            this.flightRouteBeanRemote.logout();
            System.out.println("You have logged out successfully.");
        } catch (NotAuthenticatedException e) {
            System.out.println("You are not logged in.");
        }
    }
}
