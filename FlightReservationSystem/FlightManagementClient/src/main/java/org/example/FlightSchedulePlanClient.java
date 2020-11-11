package org.example;

import controllers.FareBeanRemote;
import controllers.FlightBeanRemote;
import controllers.AircraftConfigurationBeanRemote;
import controllers.FlightSchedulePlanBeanRemote;
import entities.*;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.sql.Date;
import java.sql.Time;
import java.math.BigDecimal;

@RequiredArgsConstructor
public class FlightSchedulePlanClient implements SystemClient {
    @NonNull
    private final Scanner scanner;
    @NonNull
    private final Employee authenticatedEmployee;
    @NonNull
    private final FareBeanRemote fareBeanRemote;
    @NonNull
    private final FlightBeanRemote flightBeanRemote;
    @NonNull
    private final AircraftConfigurationBeanRemote aircraftConfigurationBeanRemote;
    @NonNull
    private final FlightSchedulePlanBeanRemote flightSchedulePlanBeanRemote;
    
    @Override
    public void runApp() {
        this.displayFlightMenu();
    }

    private void displayFlightMenu() {
        boolean loop = true;
        while (loop) {
            System.out.println("*** Flight Schedule Plan Menu ***");
            System.out.println("1: Create Flight Schedule Plan");
            System.out.println("2: View All Flight Schedule Plans");
            System.out.println("3: View Flight Schedule Plan Details");
            System.out.println("4: Update Flight Schedule Plan");
            System.out.println("5: Delete Flight Schedule Plan");
            System.out.println("6: Exit\n");

            final int option = this.scanner.nextInt();

            switch (option) {
                case 1:
                    this.displayCreateFlightSchedulePlanMenu();
                    break;
                case 2:
                    this.displayViewAllFlightSchedulePlanMenu();
                    break;
                case 3:
                    this.displayViewFlightSchedulePlanDetailsMenu();
                    break;
                case 4:
                    //this.displayUpdateFlightSchedulePlanMenu();
                    break;
                case 5:
                    this.displayDeleteFlightSchedulePlanMenu();
                    break;
                default:
                    System.out.println("Exiting...");
                    loop = false;
                    break;
            }
        }
    }
    
    private void displayCreateFlightSchedulePlanMenu() {
        System.out.println("*** Create Flight Schedule Plan ***");
        System.out.println("Enter flight code:");
        final String flightCode = scanner.next();
        
        try {  
            displayCreateFlightSchedule(flightCode);
            
            Flight returnFlight = this.flightBeanRemote.getDirectReturnFlightByFlightCode(flightCode);
            if (returnFlight != null) {
                System.out.println("A return flight exists. Create a flight schedule plan for return flight?");
                System.out.println("1: Yes, 2: No");
                final int option = scanner.nextInt();
                if (option == 1) {
                    System.out.println("Creating flight schedule plan for return flight...");
                    displayCreateFlightSchedule(returnFlight.getFlightCode());
                }         
            }
        } catch (InvalidConstraintException e) {
            e.printStackTrace();
        } catch (NotAuthenticatedException e) {
            e.printStackTrace();
        } 
    }
    
    private void displayCreateFlightSchedule(String flightCode) throws InvalidConstraintException, NotAuthenticatedException {
        System.out.println("Enter flight schedule plan type:");
        System.out.println("(1: Single, 2: Multiple, 3: Recurrent (n days), 4: Recurrent (weekly)");
        final int option = scanner.nextInt();
        
        List<FlightSchedule> flightSchedules = new ArrayList<>();
        
        switch (option) {
            case 1:
                System.out.println("Enter departure date in YYYY-MM-DD:");
                Date departureDate = Date.valueOf(scanner.next());
                System.out.println("Enter departure time in hh:mm");
                Time departureTime = Time.valueOf(scanner.next() + ":00");
                System.out.println("Enter estimated flight duration in minutes:");
                Long estimatedDuration = scanner.nextLong();

                flightSchedules.add(this.flightSchedulePlanBeanRemote.createFlightSchedule(flightCode, departureDate, departureTime, estimatedDuration));
                FlightSchedulePlan flightSchedulePlan = this.flightSchedulePlanBeanRemote.create(FlightSchedulePlanType.SINGLE, flightSchedules);
                this.displayEnterFareForCabinClass(flightCode, flightSchedulePlan);

                System.out.println("Flight schedule plan created successfully!");
                break;

            case 2:
                System.out.println("Enter the number of flight schedules to be created:");
                final int noOfSchedules = scanner.nextInt();

                for (int i = 0; i < noOfSchedules; i++) {
                    System.out.println("Enter departure date in YYYY-MM-DD:");
                    departureDate = Date.valueOf(scanner.next());
                    System.out.println("Enter departure time in hh:mm");
                    departureTime = Time.valueOf(scanner.next() + ":00");
                    System.out.println("Enter estimated flight duration in minutes:");
                    estimatedDuration = scanner.nextLong();
                    flightSchedules.add(this.flightSchedulePlanBeanRemote.createFlightSchedule(flightCode, departureDate, departureTime, estimatedDuration));
                }

                flightSchedulePlan = this.flightSchedulePlanBeanRemote.create(FlightSchedulePlanType.MULTIPLE, flightSchedules);
                this.displayEnterFareForCabinClass(flightCode, flightSchedulePlan);

                System.out.println("Flight schedule plan created successfully!");
                break;

            case 3:
                System.out.println("Enter the frequency of the flight schedule in days: ");
                System.out.println("(Minimum n = 1, maximum n = 6)");
                final int nDays = scanner.nextInt();

                System.out.println("Enter the first departure date in YYYY-MM-DD:");
                departureDate = Date.valueOf(scanner.next());
                System.out.println("Enter departure time in hh:mm");
                departureTime = Time.valueOf(scanner.next() + ":00");
                System.out.println("Enter estimated flight duration in minutes:");
                estimatedDuration = scanner.nextLong();
                System.out.println("Enter the end date for the schedule plan in YYYY-MM-DD:");
                Date endDate = Date.valueOf(scanner.next());


                flightSchedules = this.flightSchedulePlanBeanRemote.createRecurrentFlightSchedule(flightCode, departureDate, departureTime, estimatedDuration, endDate, nDays);
                flightSchedulePlan = this.flightSchedulePlanBeanRemote.create(FlightSchedulePlanType.RECURRENT_N_DAYS, flightSchedules);
                this.displayEnterFareForCabinClass(flightCode, flightSchedulePlan);

                System.out.println("Flight schedule plan created successfully!");
                break;

            case 4:                    
                System.out.println("Enter the first departure date in YYYY-MM-DD:");
                departureDate = Date.valueOf(scanner.next());
                System.out.println("Enter departure time in hh:mm");
                departureTime = Time.valueOf(scanner.next() + ":00");
                System.out.println("Enter estimated flight duration in minutes:");
                estimatedDuration = scanner.nextLong();
                System.out.println("Enter the end date for the schedule plan in YYYY-MM-DD:");
                endDate = Date.valueOf(scanner.next());


                flightSchedules = this.flightSchedulePlanBeanRemote.createRecurrentFlightSchedule(flightCode, departureDate, departureTime, estimatedDuration, endDate, 7);
                flightSchedulePlan = this.flightSchedulePlanBeanRemote.create(FlightSchedulePlanType.RECURRENT_WEEKLY, flightSchedules);
                this.displayEnterFareForCabinClass(flightCode, flightSchedulePlan);

                System.out.println("Flight schedule plan created successfully!");
                break;
        }
    }
    
    private void displayEnterFareForCabinClass(String flightCode, FlightSchedulePlan flightSchedulePlan) {
        try {    
            final Flight flight = this.flightBeanRemote.getFlightByFlightCode(flightCode);
            List<CabinClass> cabinClasses = flight.getAircraftConfiguration().getCabinClasses();

            System.out.println("--- Entering fares for cabin classes ---");
            for (CabinClass cabinClass : cabinClasses) {
                System.out.println("Cabin class: " + cabinClass.getCabinClassId().getCabinClassType().name());
                System.out.println("Enter the number of fares: ");
                int noOfFares = scanner.nextInt();
                for (int i = 0; i < noOfFares; i++) { 
                    System.out.println("Enter fare basis code: ");
                    String fareBasisCode = scanner.next();
                    System.out.println("Enter fare amount: ");
                    BigDecimal fareAmount = scanner.nextBigDecimal();
                    this.fareBeanRemote.create(fareBasisCode, fareAmount, cabinClass, flightSchedulePlan);
                }
            }
        } catch (NotAuthenticatedException e) {
            e.printStackTrace();
        } catch (InvalidConstraintException e) {
            e.printStackTrace();
        }
    }
    
    private void displayViewAllFlightSchedulePlanMenu() {
        System.out.println("*** View All Flight Schedule Plans ***");

        try {
            final List<FlightSchedulePlan> flightSchedulePlanList = this.flightSchedulePlanBeanRemote.getFlightSchedulePlans();
            for (FlightSchedulePlan flightSchedulePlan : flightSchedulePlanList) {
                System.out.println("Flight Schedule Plan: " + flightSchedulePlan.getFlightSchedulePlanId() + ", Type: " + flightSchedulePlan.getFlightSchedulePlanType().toString());
                
                // Not sure why this gives NullPointerException
//                flightSchedulePlan.getFlightSchedules().forEach(flightSchedule -> {
//                    System.out.println("\tFlight number: " + flightSchedule.getFlight().getFlightCode() + " , Departure date/time: " + flightSchedule.getDepartureDateTime());
//                });
                // Print return flight schedule plan
            }
        } catch (NotAuthenticatedException e) {
            System.out.println("You do not have permission to do this!");
        } 
    }
    
    private void displayViewFlightSchedulePlanDetailsMenu() {
        System.out.println("*** View Flight Schedule Plan Details ***");
        System.out.println("Enter the ID of the flight schedule plan you would like to view:");
        Long flightSchedulePlanId = scanner.nextLong();
        
        try {
            FlightSchedulePlan flightSchedulePlan = this.flightSchedulePlanBeanRemote.getFlightSchedulePlanById(flightSchedulePlanId);
            this.printFlightSchedulePlanDetails(flightSchedulePlan);
        } catch (NotAuthenticatedException e) {
            System.out.println("You do not have permission to do this!");
        } catch (InvalidEntityIdException e) {
            System.out.println("Invalid flight schedule plan ID.");
        }
    }
    
    private void printFlightSchedulePlanDetails(FlightSchedulePlan flightSchedulePlan) {
        System.out.println("Viewing details for flight schedule plan: " + flightSchedulePlan.getFlightSchedulePlanId());
        System.out.println("--------------------------------------------");
        System.out.println("Flight schedule plan type: " + flightSchedulePlan.getFlightSchedulePlanType().toString());
        
        List<FlightSchedule> flightSchedules = flightSchedulePlan.getFlightSchedules();
        Flight flight = flightSchedules.get(0).getFlight();
        System.out.println("Flight number: " + flight.getFlightRoute());
        System.out.println("Origin airport: " + flight.getFlightRoute().getOrigin().getIataCode());
        System.out.println("Destination airport: " + flight.getFlightRoute().getDest().getIataCode());
        System.out.println("----------------------");
        
        for(FlightSchedule flightSchedule : flightSchedules) {
            System.out.println("Departure date/time: " + flightSchedule.getDepartureDateTime());
            System.out.println("Estimated arrival date/time: " + flightSchedule.getArrivalDateTime());
            System.out.println("----------------------");
        }
    }
    
    private void displayDeleteFlightSchedulePlanMenu() {
        System.out.println("*** View Flight Schedule Plan Details ***");
        System.out.println("Enter the ID of the flight schedule plan you would like to delete:");
        Long flightSchedulePlanId = scanner.nextLong();
               
        try {
            System.out.println(this.flightSchedulePlanBeanRemote.deleteFlightSchedulePlan(flightSchedulePlanId));
        } catch (NotAuthenticatedException e) {
            System.out.println("You do not have permission to do this!");
        } catch (InvalidEntityIdException e) {
            System.out.println("Invalid flight schedule plan ID.");
        }
    }
}
