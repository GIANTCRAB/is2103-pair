package org.example;

import controllers.FlightBeanRemote;
import controllers.FlightRouteBeanRemote;
import entities.*;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class FlightClient implements SystemClient {
    @NonNull
    private final Scanner scanner;
    @NonNull
    private final Employee authenticatedEmployee;
    @NonNull
    private final FlightBeanRemote flightBeanRemote;
    @NonNull
    private final FlightRouteBeanRemote flightRouteBeanRemote;

    @Override
    public void runApp() {
        this.displayFlightMenu();
    }

    private void displayFlightMenu() {
        boolean loop = true;
        while (loop) {
            System.out.println("*** Flight Menu ***");
            System.out.println("1: Create Flight");
            System.out.println("2: View All Flights");
            System.out.println("3: View Flight Details");
            System.out.println("4: Update Flight");
            System.out.println("5: Delete Flight");
            System.out.println("6: Exit\n");

            final int option = this.scanner.nextInt();

            switch (option) {
                case 1:
                    this.displayCreateFlightMenu();
                    break;
                case 2:
                    this.displayViewAllFlightsMenu();
                    break;
                case 3:
                    this.displayViewFlightDetailsMenu();
                    break;
                case 4:
                    //this.displayUpdateFlightMenu();
                    break;
                case 5:
                    //this.displayDeleteFlightMenu();
                    break;
                default:
                    System.out.println("Exiting...");
                    loop = false;
                    break;
            }
        }
    }

    private void displayCreateFlightMenu() {
        System.out.println("*** Create Flight ***");
        System.out.println("Enter flight code: ");
        final String flightCode = scanner.next();
        System.out.println("Enter IATA code of origin airport: ");
        final String origin = scanner.next();
        System.out.println("Enter IATA code of destination airport: ");
        final String dest = scanner.next();
        System.out.println("Enter aircraft configuration ID: ");
        final Long aircraftConfigurationId = scanner.nextLong();
        System.out.println("Checking for flight route...");

        try {
            if (flightRouteBeanRemote.checkFlightRoute(origin, dest)) {

                Flight flight = flightBeanRemote.create(this.authenticatedEmployee, flightCode, origin, dest, aircraftConfigurationId);
                System.out.println("Flight " + flightCode + " successfully created!");

                if (flightRouteBeanRemote.checkFlightRoute(dest, origin)) {
                    System.out.println("A return flight route exists. Create return flight?\n1:Yes, 2:No");
                    final int createReturnFlight = scanner.nextInt();
                    if (createReturnFlight == 1) {
                        System.out.println("Enter return flight code: ");
                        final String returnFlightCode = scanner.next();
                        Flight returnFlight = flightBeanRemote.create(this.authenticatedEmployee, returnFlightCode, dest, origin, aircraftConfigurationId);
                        flightBeanRemote.addReturnFlight(this.authenticatedEmployee, flightCode, returnFlightCode);
                        System.out.println("Return flight " + returnFlightCode + " successfully created!");
                    }
                }
            } else {
                System.out.println("Flight route does not exist!");
            }

        } catch (InvalidConstraintException e) {
            this.displayConstraintErrorMessage(e);
        } catch (InvalidEntityIdException e) {
            e.printStackTrace();
        } catch (NotAuthenticatedException e) {
            e.printStackTrace();
        }
    }

    private void displayViewAllFlightsMenu() {
        System.out.println("*** View All Flights ***");

        try {
            final List<Flight> flightList = this.flightBeanRemote.getFlights(this.authenticatedEmployee);
            for (Flight flight : flightList) {
                System.out.println("Main flight: " + flight.getFlightCode() + flight.getFlightRoute().getOrigin().getIataCode() + " -> " + flight.getFlightRoute().getDest().getIataCode());

                if (flight.getReturnFlight() != null) {
                    Flight returnFlight = flight.getReturnFlight();
                    System.out.println("Return flight: " + returnFlight.getFlightCode() + returnFlight.getFlightRoute().getOrigin().getIataCode() + " -> " + returnFlight.getFlightRoute().getDest().getIataCode());
                }
            }
        } catch (NotAuthenticatedException e) {
            System.out.println("You do not have permission to do this!");
        }
    }

    private void displayViewFlightDetailsMenu() {
        System.out.println("*** View Aircraft Configuration Details ***");
        System.out.println("Enter the flight number of the flight details you would like to view:");
        String flightCode = scanner.next();

        try {
            final Flight flight = this.flightBeanRemote.getFlightByFlightCode(this.authenticatedEmployee, flightCode);
            List<CabinClass> cabinClasses = flight.getAircraftConfiguration().getCabinClasses();
            String availableCabinClasses = cabinClasses.stream()
                    .map(c -> c.getCabinClassId().getCabinClassType().name())
                    .collect(Collectors.joining(", "));

            System.out.println("Viewing details for flight: " + flightCode + "\n");
            System.out.println("-----------------");
            System.out.println("Flight route: " + flight.getFlightRoute().getOrigin().getIataCode() + " -> " + flight.getFlightRoute().getDest().getIataCode());
            System.out.println("Aircraft configuration: " + flight.getAircraftConfiguration().getAircraftConfigurationName());
            System.out.println("Available cabin classes: " + availableCabinClasses + "\n");
        } catch (NotAuthenticatedException e) {
            System.out.println("You do not have permission to do this!");
        }
    }

    private void displayConstraintErrorMessage(InvalidConstraintException invalidConstraintException) {
        System.out.println("There were some validation errors!");
        System.out.println(invalidConstraintException.toString());
    }
}
