package org.example;

import controllers.FlightBeanRemote;
import controllers.FlightRouteBeanRemote;
import entities.*;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import exceptions.EntityIsDisabledException;
import exceptions.EntityAlreadyExistException;
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
                    this.displayUpdateFlightMenu();
                    break;
                case 5:
                    this.displayDeleteFlightMenu();
                    break;
                default:
                    this.displayLogoutMenu();
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

                Flight flight = flightBeanRemote.create(flightCode, origin, dest, aircraftConfigurationId);
                System.out.println("Flight " + flightCode + " successfully created!");

                if (flightRouteBeanRemote.checkFlightRoute(dest, origin)) {
                    System.out.println("A return flight route exists. Create return flight?\n1:Yes, 2:No");
                    final int createReturnFlight = scanner.nextInt();
                    if (createReturnFlight == 1) {
                        System.out.println("Enter return flight code: ");
                        final String returnFlightCode = scanner.next();
                        Flight returnFlight = flightBeanRemote.create(returnFlightCode, dest, origin, aircraftConfigurationId);
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
        } catch (EntityIsDisabledException e) {
            System.out.println(e.getMessage());
        } catch (NotAuthenticatedException e) {
            e.printStackTrace();
        } catch (EntityAlreadyExistException e) {
            System.out.println(e.getMessage());
        }
    }

    private void displayViewAllFlightsMenu() {
        System.out.println("*** View All Flights ***");

        try {
            final List<Flight> flightList = this.flightBeanRemote.getFlights();
            for (Flight flight : flightList) {
                System.out.println("Flight: " + flight.getFlightCode() + ", " + flight.getFlightRoute().getOrigin().getIataCode() + " -> " + flight.getFlightRoute().getDest().getIataCode() + ", " + flight.getEnabled());
            }
            System.out.println("\n");
            
        } catch (NotAuthenticatedException e) {
            System.out.println("You do not have permission to do this!");
        }
    }

    private void displayViewFlightDetailsMenu() {
        System.out.println("*** View Aircraft Configuration Details ***");
        System.out.println("Enter the flight number of the flight details you would like to view:");
        String flightCode = scanner.next();

        try {
            final Flight flight = this.flightBeanRemote.getFlightByFlightCode(flightCode);
            printFlightDetails(flight);
        } catch (NotAuthenticatedException e) {
            System.out.println("You do not have permission to do this!");
        }
    }
    
    private void printFlightDetails(Flight flight) {
        List<CabinClass> cabinClasses = flight.getAircraftConfiguration().getCabinClasses();
        String availableCabinClasses = cabinClasses.stream()
                .map(c -> c.getCabinClassId().getCabinClassType().name())
                .collect(Collectors.joining(", "));

        System.out.println("Viewing details for flight: " + flight.getFlightCode() + "\n");
        System.out.println("-----------------------------------");
        System.out.println("Flight route: " + flight.getFlightRoute().getOrigin().getIataCode() + " -> " + flight.getFlightRoute().getDest().getIataCode());
        System.out.println("Aircraft configuration: " + flight.getAircraftConfiguration().getAircraftConfigurationName());
        System.out.println("Available cabin classes: " + availableCabinClasses);
        System.out.println("-----------------------------------");
    }
    
    private void displayUpdateFlightMenu() {
        System.out.println("*** Update Flight Details ***");
        System.out.println("Enter the flight number of the flight details you would like to update:");
        String flightCode = scanner.next();
        try {
            Flight flight = this.flightBeanRemote.getFlightByFlightCode(flightCode);
            printFlightDetails(flight);

            System.out.println("Which details would you like to update?");
            System.out.println("1. Update flight route");
            System.out.println("2. Update aircraft configuration");
            int updateOption = scanner.nextInt();

            if(updateOption == 1) {
                System.out.println("Enter the IATA code for the new origin airport: ");
                String origin = scanner.next();
                System.out.println("Enter the IATA code for the new destination airport: ");
                String dest = scanner.next();
                System.out.println("Checking for flight route...");

                if (flightRouteBeanRemote.checkFlightRoute(origin, dest)) {
                    this.flightBeanRemote.updateFlightRoute(flightCode, origin, dest);
                    System.out.println("Flight updated successfully!");
                } else {
                    System.out.println("Flight route does not exist!");
                }
            } else if (updateOption == 2) {
                System.out.println("Enter the name of the new aircraft configuration: ");
                //Long aircraftConfigurationId = scanner.nextLong();
                String aircraftConfigurationName = scanner.next();
                this.flightBeanRemote.updateAircraftConfiguration(flightCode, aircraftConfigurationName);
                System.out.println("Flight updated successfully!");
            } else {
                System.out.println("Invalid input. Please try again.");
            }
        } catch (EntityAlreadyExistException e) {
            System.out.println(e.getMessage());
        } catch (NotAuthenticatedException e) {
            System.out.println("You do not have permission to do this!");
        } catch (InvalidEntityIdException e) {
            System.out.println(e.getMessage());
        } catch (EntityIsDisabledException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void displayDeleteFlightMenu() {
        System.out.println("*** Delete Flight ***");
        System.out.println("Enter the flight number of the flight you would like to delete:");
        String flightCode = scanner.next();
        
        try { 
            Flight flight = this.flightBeanRemote.getFlightByFlightCode(flightCode);
            printFlightDetails(flight);
            System.out.println("Confirm deletion of flight? (1: Yes, 2: No");
            int option = scanner.nextInt();

            if (option == 1) {
                System.out.println(this.flightBeanRemote.deleteFlight(flightCode));
            }
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
            this.flightBeanRemote.logout();
            this.flightRouteBeanRemote.logout();
            System.out.println("You have logged out successfully.");
        } catch (NotAuthenticatedException e) {
            System.out.println("You are not logged in.");
        }
    }
}
