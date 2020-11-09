package org.example;

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
import java.util.Scanner;
import java.sql.Date;
import java.sql.Time;

@RequiredArgsConstructor
public class FlightSchedulePlanClient implements SystemClient {
    @NonNull
    private final Scanner scanner;
    @NonNull
    private final Employee authenticatedEmployee;
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
                    //this.displayViewFlightSchedulePlanDetailsMenu();
                    break;
                case 4:
                    //this.displayUpdateFlightSchedulePlanFlightMenu();
                    break;
                case 5:
                    //this.displayDeleteFlightSchedulePlanMenu();
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
        // Include check for return flight
        
        System.out.println("Enter flight schedule plan type:");
        System.out.println("(1: Single, 2: Multiple, 3: Recurrent (n days), 4: Recurrent (weekly)");
        final int option = scanner.nextInt();
        
        List<FlightSchedule> flightSchedules = new ArrayList<>();
        
        try {
            switch (option) {
                case 1:
                    FlightSchedulePlan flightSchedulePlan = this.flightSchedulePlanBeanRemote.create(this.authenticatedEmployee, FlightSchedulePlanType.SINGLE);
                    
                    System.out.println("Enter departure date in YYYY-MM-DD:");
                    Date departureDate = Date.valueOf(scanner.next());
                    System.out.println("Enter departure time in hh:mm");
                    Date departureTime = Time.valueOf(scanner.next() + ":00");
                    System.out.println("Enter estimated flight duration in minutes:");
                    Long estimatedDuration = scanner.nextLong();
                    
                    flightSchedule.add(this.flightSchedulePlanBeanRemote.createFlightSchedule(this.authenticatedEmployee, flightCode, departureDate, departureTime, estimatedDuration));
                    this.flightSchedulePlanBeanRemote.associateFlightSchedules(this.authenticatedEmployee, flightSchedulePlan, flightSchedules);
                    this.displayEnterFareForCabinClass(flightCode, flightSchedules);
                    
                    System.out.println("Flight schedule plan created successfully!");
                    break;
                    
                case 2:
                    FlightSchedulePlan flightSchedulePlan = this.flightSchedulePlanBeanRemote.create(this.authenticatedEmployee, FlightSchedulePlanType.MULTIPLE;
                    System.out.println("Enter the number of flight schedules to be created:");
                    final int noOfSchedules = scanner.nextInt();
                    
                    for (i = 0; i < noOfSchedules; i++) {
                        System.out.println("Enter departure date in YYYY-MM-DD:");
                        Date departureDate = Date.valueOf(scanner.next());
                        System.out.println("Enter departure time in hh:mm");
                        Date departureTime = Time.valueOf(scanner.next() + ":00");
                        System.out.println("Enter estimated flight duration in minutes:");
                        Long estimatedDuration = scanner.nextLong();
                        flightSchedules.add(this.flightSchedulePlanBeanRemote.createFlightSchedule(this.authenticatedEmployee, flightCode, departureDate, departureTime, estimatedDuration));
                    }
                    
                    this.flightSchedulePlanBeanRemote.associateFlightSchedules(this.authenticatedEmployee, flightSchedulePlan, flightSchedules);
                    this.displayEnterFareForCabinClass(flightCode, flightSchedules);
                    
                    System.out.println("Flight schedule plan created successfully!");
                    break;
                
                case 3:
                    FlightSchedulePlan flightSchedulePlan = this.flightSchedulePlanBeanRemote.create(this.authenticatedEmployee, FlightSchedulePlanType.RECURRENT_N_DAYS;
                    System.out.println("Enter the frequency of the flight schedule in days: ");
                    System.out.println("(Minimum n = 1, maximum n = 6)");
                    final int nDays = scanner.nextInt();
                    
                    System.out.println("Enter the first departure date in YYYY-MM-DD:");
                    final Date departureDate = Date.valueOf(scanner.next());
                    System.out.println("Enter departure time in hh:mm");
                    final Date departureTime = Time.valueOf(scanner.next() + ":00");
                    System.out.println("Enter estimated flight duration in minutes:");
                    final Long estimatedDuration = scanner.nextLong();
                    System.out.println("Enter the end date for the schedule plan in YYYY-MM-DD:");
                    final Date endDate = Date.valueOf(scanner.next());
                    
                    
                    flightSchedules = this.flightSchedulePlanBeanRemote.createRecurrentFlightSchedule(this.authenticatedEmployee, flightCode, departureDate, departureTime, estimatedDuration, endDate, nDays);
                    this.flightSchedulePlanBeanRemote.associateFlightSchedules(this.authenticatedEmployee, flightSchedulePlan, flightSchedules);
                    this.displayEnterFareForCabinClass(flightCode, flightSchedules);
                    
                    System.out.println("Flight schedule plan created successfully!");
                    break;
                
                case 4:
                    FlightSchedulePlan flightSchedulePlan = this.flightSchedulePlanBeanRemote.create(this.authenticatedEmployee, FlightSchedulePlanType.RECURRENT_WEEKLY);                    
                    System.out.println("Enter the first departure date in YYYY-MM-DD:");
                    final Date departureDate = Date.valueOf(scanner.next());
                    System.out.println("Enter departure time in hh:mm");
                    final Date departureTime = Time.valueOf(scanner.next() + ":00");
                    System.out.println("Enter estimated flight duration in minutes:");
                    final Long estimatedDuration = scanner.nextLong();
                    System.out.println("Enter the end date for the schedule plan in YYYY-MM-DD:");
                    final Date endDate = Date.valueOf(scanner.next());
                    
                    
                    flightSchedules = this.flightSchedulePlanBeanRemote.createRecurrentFlightSchedule(this.authenticatedEmployee, flightCode, departureDate, departureTime, estimatedDuration, endDate, 7);
                    this.flightSchedulePlanBeanRemote.associateFlightSchedules(this.authenticatedEmployee, flightSchedulePlan, flightSchedules);
                    this.displayEnterFareForCabinClass(flightCode, flightSchedules);
                    
                    System.out.println("Flight schedule plan created successfully!");
                    break;
            }
        } catch (InvalidConstraintException e) {
            this.displayConstraintErrorMessage(e);
        } catch (InvalidEntityIdException e) {
            e.printStackTrace();
        } catch (NotAuthenticatedException e) {
            e.printStackTrace();
        } 
    }
    
    private void displayEnterFareForCabinClass(String flightCode, List<FlightSchedule> flightSchedules) {
        final Flight flight = this.flightBeanRemote.getFlightByFlightCode(this.authenticatedEmployee, flightCode);
        List<CabinClass> cabinClasses = flight.getAircraftConfiguration().getCabinClasses();
        
        System.out.println("--- Entering fares for cabin classes ---");
        for (CabinClass cabinClass : cabinClasses) {
            System.out.println("Cabin class: " + cabinClass.getCabinClassId().getCabinClassType().name());
            System.out.println("Enter the number of fares: ");
            int noOfFares = scanner.nextInt();
            for (i = 0; i < noOfFares; i++) { 
                System.out.println("Enter fare basis code: ");
                String fareBasisCode = scanner.next();
                System.out.println("Enter fare amount: ");
                BigDecimal fareAmount = scanner.nextBigDecimal();
                // Create new fare
                // Associate fare with cabin class
                // Associate fare with flightSchedules
        }
    }
}
    
    private void displayViewAllFlightSchedulePlanMenu() {
        System.out.println("*** View All Flight Schedule Plans ***");

        try {
            final List<FlightSchedulePlan> flightSchedulePlanList = this.flightSchedulePlanBeanRemote.getFlightSchedulePlans(this.authenticatedEmployee);
            for (FlightSchedulePlan flightSchedulePlan : flightSchedulePlanList) {
                System.out.println("Flight Schedule Plan: " + flightSchedulePlan.getFlightSchedulePlanId() + " , Type: " + flightSchedulePlan.getFlightSchedulePlanType().toString());
                flightSchedulePlan.getFlightSchedules().forEach(f -> {
                    System.out.println("\tFlight number: " + f.getFlight().getFlightCode() + " , Departure date/time: " + f.getDepartureDateTime());
                })
                // Print return flight schedule plan
            }
        } catch (NotAuthenticatedException e) {
            System.out.println("You do not have permission to do this!");
        }
    }
