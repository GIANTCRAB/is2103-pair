package org.example;

import controllers.FareBeanRemote;
import controllers.FlightBeanRemote;
import controllers.FlightSchedulePlanBeanRemote;
import entities.*;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import exceptions.EntityIsDisabledException;
import exceptions.EntityInUseException;
import exceptions.EntityAlreadyExistException;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.ListIterator;
import java.util.stream.Collectors;
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
                    this.displayUpdateFlightSchedulePlanMenu();
                    break;
                case 5:
                    this.displayDeleteFlightSchedulePlanMenu();
                    break;
                default:
                    this.displayLogoutMenu();
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
            FlightSchedulePlan newFlightSchedulePlan = createFlightSchedulePlan(flightCode);
            
            Flight returnFlight = this.flightBeanRemote.getDirectReturnFlightByFlightCode(flightCode);
            if (returnFlight != null) {
                System.out.println("A return flight exists. Create a flight schedule plan for return flight?");
                System.out.println("1: Yes, 2: No");
                final int option = scanner.nextInt();
                if (option == 1) {
                    System.out.println("Creating flight schedule plan for return flight...");
                    System.out.println("Enter return flight number: ");
                    String returnFlightCode = scanner.next();
                    displayCreateReturnFlightSchedulePlan(newFlightSchedulePlan, returnFlightCode);
                }         
            }
        } catch (InvalidConstraintException e) {
            displayConstraintErrorMessage(e);
        } catch (NotAuthenticatedException e) {
            System.out.println("You are not allowed to do this!");
        } catch (InvalidEntityIdException e) {
            System.out.println(e.getMessage());
        } catch (EntityIsDisabledException e) {
            System.out.println(e.getMessage());
        } catch(EntityAlreadyExistException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private FlightSchedulePlan createFlightSchedulePlan(String flightCode) throws InvalidConstraintException, NotAuthenticatedException, EntityIsDisabledException, InvalidEntityIdException, EntityAlreadyExistException {
        System.out.println("Enter flight schedule plan type:");
        System.out.println("(1: Single, 2: Multiple, 3: Recurrent (n days), 4: Recurrent (weekly)");
        final int option = scanner.nextInt();
        FlightSchedulePlan flightSchedulePlan = null;
        
        List<FlightSchedule> flightSchedules = new ArrayList<>();
        
        switch (option) {
            case 1: {
                System.out.println("Enter departure date in YYYY-MM-DD:");
                Date departureDate = Date.valueOf(scanner.next());
                System.out.println("Enter departure time in hh:mm");
                Time departureTime = Time.valueOf(scanner.next() + ":00");
                System.out.println("Enter estimated flight duration in minutes:");
                Long estimatedDuration = scanner.nextLong();

                flightSchedules.add(this.flightSchedulePlanBeanRemote.createFlightSchedule(flightCode, departureDate, departureTime, estimatedDuration));
                flightSchedulePlan = this.flightSchedulePlanBeanRemote.create(FlightSchedulePlanType.SINGLE, flightSchedules);
                this.displayEnterFareForCabinClass(flightCode, flightSchedulePlan);

                System.out.println("Flight schedule plan created successfully!");
                break;
            }
            case 2: {
                System.out.println("Enter the number of flight schedules to be created:");
                final int noOfSchedules = scanner.nextInt();

                for (int i = 0; i < noOfSchedules; i++) {
                    System.out.println("Enter departure date in YYYY-MM-DD:");
                    Date departureDate = Date.valueOf(scanner.next());
                    System.out.println("Enter departure time in hh:mm");
                    Time departureTime = Time.valueOf(scanner.next() + ":00");
                    System.out.println("Enter estimated flight duration in minutes:");
                    Long estimatedDuration = scanner.nextLong();
                    flightSchedules.add(this.flightSchedulePlanBeanRemote.createFlightSchedule(flightCode, departureDate, departureTime, estimatedDuration));
                }

                flightSchedulePlan = this.flightSchedulePlanBeanRemote.create(FlightSchedulePlanType.MULTIPLE, flightSchedules);
                this.displayEnterFareForCabinClass(flightCode, flightSchedulePlan);

                System.out.println("Flight schedule plan created successfully!");
                break;
            }
            case 3: {
                System.out.println("Enter the frequency of the flight schedule in days: ");
                System.out.println("(Minimum n = 1, maximum n = 6)");
                final int nDays = scanner.nextInt();

                System.out.println("Enter the first departure date in YYYY-MM-DD:");
                Date departureDate = Date.valueOf(scanner.next());
                System.out.println("Enter departure time in hh:mm");
                Time departureTime = Time.valueOf(scanner.next() + ":00");
                System.out.println("Enter estimated flight duration in minutes:");
                Long estimatedDuration = scanner.nextLong();
                System.out.println("Enter the end date for the schedule plan in YYYY-MM-DD:");
                Date endDate = Date.valueOf(scanner.next());


                final Flight flight = this.flightBeanRemote.getFlightByFlightCode(flightCode);
                if (flight != null) {
                    flightSchedulePlan = this.flightSchedulePlanBeanRemote.createRecurrentFlightSchedule(FlightSchedulePlanType.RECURRENT_N_DAYS,
                            flight,
                            departureDate,
                            departureTime,
                            estimatedDuration,
                            endDate,
                            nDays);
                    this.displayEnterFareForCabinClass(flightCode, flightSchedulePlan);

                    System.out.println("Flight schedule plan created successfully!");
                } else {
                    System.out.println("Flight with " + flightCode + " could not be found!");
                }
                break;
            }
            case 4: {                    
                System.out.println("Enter the first departure date in YYYY-MM-DD:");
                Date departureDate = Date.valueOf(scanner.next());
                System.out.println("Enter departure time in hh:mm");
                Time departureTime = Time.valueOf(scanner.next() + ":00");
                System.out.println("Enter estimated flight duration in minutes:");
                Long estimatedDuration = scanner.nextLong();
                System.out.println("Enter the end date for the schedule plan in YYYY-MM-DD:");
                Date endDate = Date.valueOf(scanner.next());

                final Flight flight = this.flightBeanRemote.getFlightByFlightCode(flightCode);
                if (flight != null) {
                    flightSchedulePlan = this.flightSchedulePlanBeanRemote.createRecurrentFlightSchedule(FlightSchedulePlanType.RECURRENT_WEEKLY,
                            flight,
                            departureDate,
                            departureTime,
                            estimatedDuration,
                            endDate,
                            null);
                    this.displayEnterFareForCabinClass(flightCode, flightSchedulePlan);

                    System.out.println("Flight schedule plan created successfully!");
                } else {
                    System.out.println("Flight with " + flightCode + " could not be found!");
                }

                break;
            }
            default:
                break;
        }
        return flightSchedulePlan;
    }
    
    private void displayCreateReturnFlightSchedulePlan(FlightSchedulePlan flightSchedulePlan, String flightCode) throws InvalidConstraintException, NotAuthenticatedException, InvalidEntityIdException, EntityIsDisabledException, EntityAlreadyExistException {
        List<FlightSchedule> flightSchedules = flightSchedulePlan.getFlightSchedules();
        FlightSchedulePlanType flightSchedulePlanType = flightSchedulePlan.getFlightSchedulePlanType();
        List<FlightSchedule> returnFlightSchedules = new ArrayList<>();
        
        switch (flightSchedulePlanType) {
            case SINGLE: {
                // Assume that return flight schedules don't happen the next day
                System.out.println("Enter layover duration in minutes: ");
                Long layoverDuration = scanner.nextLong();
                
                Date departureDate = flightSchedules.get(0).getDate();
                Time departureTime = Time.valueOf(flightSchedules.get(0).getTime().toLocalTime().plusMinutes(layoverDuration));
                Long estimatedDuration = flightSchedules.get(0).getEstimatedDuration();

                returnFlightSchedules.add(this.flightSchedulePlanBeanRemote.createFlightSchedule(flightCode, departureDate, departureTime, estimatedDuration));
                FlightSchedulePlan returnFlightSchedulePlan = this.flightSchedulePlanBeanRemote.create(flightSchedulePlanType, returnFlightSchedules);
                this.displayEnterFareForCabinClass(flightCode, flightSchedulePlan);
                
                System.out.println("Return flight schedule plan created successfully!");
                break;
            }

            case MULTIPLE:
            case RECURRENT_N_DAYS:
            case RECURRENT_WEEKLY: {
                System.out.println("Enter layover duration in minutes: ");
                Long layoverDuration = scanner.nextLong();
                
                int noOfSchedules = flightSchedules.size();
                
                for (int i = 0; i < noOfSchedules; i++) {
                    Date departureDate = flightSchedules.get(i).getDate();
                    Time departureTime = Time.valueOf(flightSchedules.get(i).getTime().toLocalTime().plusMinutes(layoverDuration));
                    Long estimatedDuration = flightSchedules.get(i).getEstimatedDuration();
                    returnFlightSchedules.add(this.flightSchedulePlanBeanRemote.createFlightSchedule(flightCode, departureDate, departureTime, estimatedDuration));
                }

                flightSchedulePlan = this.flightSchedulePlanBeanRemote.create(flightSchedulePlanType, flightSchedules);
                this.displayEnterFareForCabinClass(flightCode, flightSchedulePlan);

                System.out.println("Flight schedule plan created successfully!");
                break;
            }
            default:
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
            System.out.println("You do not have permission to do this!");
        } catch (InvalidConstraintException | InvalidEntityIdException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void displayViewAllFlightSchedulePlanMenu() {
        System.out.println("*** View All Flight Schedule Plans ***");

        try {
            final List<FlightSchedulePlan> flightSchedulePlanList = this.flightSchedulePlanBeanRemote.getFlightSchedulePlans();
            for (FlightSchedulePlan flightSchedulePlan : flightSchedulePlanList) {
                System.out.println("Flight schedule plan: " + flightSchedulePlan.getFlightSchedulePlanId() + " Type: " + flightSchedulePlan.getFlightSchedulePlanType().toString());
                System.out.println("\tFlight number: " + flightSchedulePlan.getFlightSchedules().get(0).getFlight().getFlightCode());
                // Not sure why getDepartureDateTime() returns null
                flightSchedulePlan.getFlightSchedules().forEach(flightSchedule -> System.out.println("\tDeparture date: " + flightSchedule.getDate() + " Departure time: " + flightSchedule.getTime()));
            }
                // Print return flight schedule plan
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
            System.out.println(e.getMessage());
        }
    }
    
    private void printFlightSchedulePlanDetails(FlightSchedulePlan flightSchedulePlan) {
        System.out.println("Viewing details for flight schedule plan: " + flightSchedulePlan.getFlightSchedulePlanId());
        System.out.println("--------------------------------------------");
        System.out.println("Flight schedule plan type: " + flightSchedulePlan.getFlightSchedulePlanType().toString());
        
        List<Fare> fares = flightSchedulePlan.getFares();
        String availableFares = fares.stream()
                    .map(fare -> fare.getCabinClass().getCabinClassId().getCabinClassType().name() + ": $" + fare.getFareAmount().toString())
                    .collect(Collectors.joining(", "));
        System.out.println("Available fares: " + availableFares);
        
        List<FlightSchedule> flightSchedules = flightSchedulePlan.getFlightSchedules();
        Flight flight = flightSchedules.get(0).getFlight();
        System.out.println("Flight number: " + flight.getFlightCode());
        System.out.println("Origin airport: " + flight.getFlightRoute().getOrigin().getIataCode());
        System.out.println("Destination airport: " + flight.getFlightRoute().getDest().getIataCode());
        System.out.println("------------------------------------");
        
        for(FlightSchedule flightSchedule : flightSchedules) {
            System.out.println("Departure date/time: " + flightSchedule.getDepartureDateTime());
            System.out.println("Estimated flight duration: " + flightSchedule.getEstimatedDuration() + " minutes");
            System.out.println("Estimated arrival date/time: " + flightSchedule.getArrivalDateTime());
            System.out.println("------------------------------------");
        }
    }
    
    private void displayUpdateFlightSchedulePlanMenu() {
        System.out.println("*** Update Flight Schedule Plan ***");
        System.out.println("Enter the ID of the flight schedule plan you would like to update:");
        Long flightSchedulePlanId = scanner.nextLong();
        
        try {
            FlightSchedulePlan flightSchedulePlan = this.flightSchedulePlanBeanRemote.getFlightSchedulePlanById(flightSchedulePlanId);
            this.printFlightSchedulePlanDetails(flightSchedulePlan);

            System.out.println("What details would you like to update?");
            System.out.println("1. Fare(s)");
            System.out.println("2. Add Flight Schedule");
            System.out.println("3. Delete Flight Schedule");
            System.out.println("4. Update Flight Schedule Details");
            final int option = scanner.nextInt();
            switch(option) {
                case 1:
                    displayUpdateFares(flightSchedulePlan);
                    break;
                case 2:
                    displayAddFlightSchedule(flightSchedulePlan);
                    break;
                case 3:
                    displayDeleteFlightSchedule(flightSchedulePlan);
                    break;
                case 4:
                    displayUpdateFlightScheduleDetails(flightSchedulePlan);
                    break;
            }
        } catch (NotAuthenticatedException e) {
            System.out.println("You do not have permission to do this!");
        } catch (InvalidEntityIdException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void displayUpdateFares(FlightSchedulePlan flightSchedulePlan) {
        System.out.println("--- Update Fares For Flight Schedule Plan ---");
        ListIterator<Fare> iterateFares = flightSchedulePlan.getFares().listIterator();
        while (iterateFares.hasNext()) {
            Fare fare = iterateFares.next();
            System.out.println((iterateFares.nextIndex()) + ". " + fare.getCabinClass().getCabinClassId().getCabinClassType().name() + 
                    ": $" + fare.getFareAmount().toString());
        }
        List<Fare> updatedFares = new ArrayList<>();
        boolean update = true;
        while (update) {
            System.out.println("Enter the index of the fare you would like to update: ");
            int index = scanner.nextInt();
            System.out.println("Enter the new fare amount: ");
            BigDecimal newFareAmount = scanner.nextBigDecimal();
            Fare updatedFare = flightSchedulePlan.getFares().get(index-1);
            updatedFare.setFareAmount(newFareAmount);
            updatedFares.add(updatedFare);
            System.out.println("Update another fare? (1: Yes, 2: No)");
            int option = scanner.nextInt();
            if (option == 2) {
                update = false;
            }
        }
        try {
            this.flightSchedulePlanBeanRemote.updateFares(updatedFares);
            System.out.println("Fare(s) updated successfully!\n");
        } catch (NotAuthenticatedException e) {
            System.out.println("You do not have permission to do this!");
        }
    }
    
    private void displayAddFlightSchedule(FlightSchedulePlan flightSchedulePlan) {
        System.out.println("--- Add Flight Schedule ---");
        String flightCode = flightSchedulePlan.getFlightSchedules().get(0).getFlight().getFlightCode();
        try {
            Flight flight = this.flightBeanRemote.getFlightByFlightCode(flightCode);

            if (!flight.getEnabled()) {
                System.out.println("Selected flight is disabled.");
            } else {
                List<FlightSchedule> newFlightSchedules = new ArrayList<>();
                boolean add = true; 

                while(add) {
                    System.out.println("Enter departure date in YYYY-MM-DD:");
                    Date departureDate = Date.valueOf(scanner.next());
                    System.out.println("Enter departure time in hh:mm");
                    Time departureTime = Time.valueOf(scanner.next() + ":00");
                    System.out.println("Enter estimated flight duration in minutes:");
                    Long estimatedDuration = scanner.nextLong();
                    FlightSchedule flightSchedule = this.flightSchedulePlanBeanRemote.createFlightSchedule(flightCode, departureDate, departureTime, estimatedDuration);
                    newFlightSchedules.add(flightSchedule);
                    System.out.println("Add another flight schedule? (1: Yes, 2: No)");
                    int option = scanner.nextInt();
                    if (option == 2) {
                        add = false;
                    }
                }
                this.flightSchedulePlanBeanRemote.addFlightSchedules(flightSchedulePlan, newFlightSchedules);
                System.out.println("Flight schedule(s) added successfully!\n");
            }
        } catch (NotAuthenticatedException e) {
            System.out.println("You do not have permission to do this!");
        } catch (InvalidConstraintException e) {
            displayConstraintErrorMessage(e);
        } catch (EntityIsDisabledException e) {
            System.out.println(e.getMessage());
        } catch (InvalidEntityIdException e) {
            System.out.println(e.getMessage());
        } catch (EntityAlreadyExistException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void displayDeleteFlightSchedule(FlightSchedulePlan flightSchedulePlan) {
        System.out.println("--- Delete Flight Schedule ---");
        ListIterator<FlightSchedule> iterateSchedules = flightSchedulePlan.getFlightSchedules().listIterator();
        while (iterateSchedules.hasNext()) {
            FlightSchedule flightSchedule = iterateSchedules.next();
            System.out.println((iterateSchedules.nextIndex()) + ". " + flightSchedule.getFlight().getFlightCode() + 
                    " Departure date/time: " + flightSchedule.getDepartureDateTime() +
                    " Estimated duration: " + flightSchedule.getEstimatedDuration());
        }
        
        System.out.println("Enter the index of the flight schedule you would like to delete: ");
        int index = scanner.nextInt();
        FlightSchedule flightSchedule = flightSchedulePlan.getFlightSchedules().get(index-1);
        try {
            this.flightSchedulePlanBeanRemote.deleteFlightSchedule(flightSchedulePlan, flightSchedule);
        } catch (NotAuthenticatedException e) {
            System.out.println("You do not have permission to do this!");
        } catch (EntityInUseException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void displayUpdateFlightScheduleDetails(FlightSchedulePlan flightSchedulePlan) {
        System.out.println("--- Update Flight Schedule Details ---");
        ListIterator<FlightSchedule> iterateSchedules = flightSchedulePlan.getFlightSchedules().listIterator();
        while (iterateSchedules.hasNext()) {
            FlightSchedule flightSchedule = iterateSchedules.next();
            System.out.println((iterateSchedules.nextIndex()) + ". " + flightSchedule.getFlight().getFlightCode() + 
                    " Departure date/time: " + flightSchedule.getDepartureDateTime() +
                    " Estimated duration: " + flightSchedule.getEstimatedDuration());
        }
        
        List<FlightSchedule> updatedFlightSchedules = new ArrayList<>();
        boolean update = true;
        while (update) {
            System.out.println("Enter the index of the flight schedule you would like to update: ");
            int index = scanner.nextInt();
            System.out.println("Enter the new departure date in YYYY-MM-DD: ");
            Date newDate = Date.valueOf(scanner.next());
            System.out.println("Enter the new departure time in hh:mm: ");
            Time newTime = Time.valueOf(scanner.next() + ":00");
            System.out.println("Enter the new estimated duration: ");
            Long newDuration = scanner.nextLong();
            
            FlightSchedule updatedFlightSchedule = flightSchedulePlan.getFlightSchedules().get(index-1);
            updatedFlightSchedule.setDate(newDate);
            updatedFlightSchedule.setTime(newTime);
            updatedFlightSchedule.setEstimatedDuration(newDuration);
            updatedFlightSchedules.add(updatedFlightSchedule);

            System.out.println("Update another flight schedule? (1: Yes, 2: No)");
            int option = scanner.nextInt();
            if (option == 2) {
                update = false;
            }
        }
        try {
            this.flightSchedulePlanBeanRemote.updateFlightSchedules(updatedFlightSchedules);
            System.out.println("Flight schedule(s) updated successfully!\n");
        } catch (NotAuthenticatedException e) {
            System.out.println("You do not have permission to do this!");
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
            System.out.println(e.getMessage());
        }
    }
    
    private void displayConstraintErrorMessage(InvalidConstraintException invalidConstraintException) {
        System.out.println("There were some validation errors!");
        System.out.println(invalidConstraintException.toString());
    }

    private void displayLogoutMenu() {
        try {
            this.fareBeanRemote.logout();
            this.flightBeanRemote.logout();
            this.flightSchedulePlanBeanRemote.logout();
            System.out.println("You have logged out successfully.");
        } catch (NotAuthenticatedException e) {
            System.out.println("You are not logged in.");
        }
    }
}
