package org.example;

import entities.*;

import controllers.SalesManagerBeanRemote;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;

import java.util.Scanner;
import java.util.List;
import java.util.ListIterator;

@RequiredArgsConstructor
public class SalesManagerClient implements SystemClient {
    @NonNull
    private final Scanner scanner;
    @NonNull
    private final Employee authenticatedEmployee;
    @NonNull
    private final SalesManagerBeanRemote salesManagerBeanRemote;

    @Override
    public void runApp() {
        this.displaySalesManagerMenu();
    }
    
    private void displaySalesManagerMenu() {
        boolean loop = true;
        while (loop) {
            System.out.println("*** Sales Manager Menu ***");
            System.out.println("1: View Seats Inventory");
            System.out.println("2: View Flight Reservations");
            System.out.println("3: Exit\n");

            final int option = this.scanner.nextInt();

            switch (option) {
                case 1:
                    this.displayViewSeatsInventoryMenu();
                    break;
                case 2:
                    this.displayViewFlightReservationsMenu();
                    break;
                default:
                    System.out.println("Exiting...");
                    loop = false;
                    break;
            }
        }
    }
    
    private void displayViewSeatsInventoryMenu() {
        System.out.println("*** View Seats Inventory ***");
        System.out.println("Enter flight number: ");
        String flightCode = scanner.next();
        
        try {
            List<FlightSchedule> flightSchedules = this.salesManagerBeanRemote.getFlightSchedulesByFlightCode(flightCode);
            ListIterator<FlightSchedule> iterateSchedules = flightSchedules.listIterator();
            
            while (iterateSchedules.hasNext()) {
                FlightSchedule flightSchedule = iterateSchedules.next();
                System.out.println((iterateSchedules.nextIndex()) + ". " + flightSchedule.getFlight().getFlightCode() + 
                        " Departure date/time: " + flightSchedule.getDepartureDateTime() +
                        " Estimated duration: " + flightSchedule.getEstimatedDuration());
            }
            System.out.println("-----------------------------------");
            
            System.out.println("Enter the index of the flight schedule you would like to select: ");
            final int index = scanner.nextInt();
            FlightSchedule selectedFlightSchedule = flightSchedules.get(index-1);
            List<CabinClass> cabinClasses = selectedFlightSchedule.getFlight().getAircraftConfiguration().getCabinClasses();
            
            for (CabinClass cabinClass : cabinClasses) {
                System.out.println("Viewing seat inventory for cabin class: " + cabinClass.getCabinClassId().getCabinClassType().toString());
                int noOfSeatsReserved = salesManagerBeanRemote.getNoOfSeatsReservedForCabinClass(selectedFlightSchedule, cabinClass.getCabinClassId().getCabinClassType());
                System.out.println("Total no. of seats: " + cabinClass.getMaxCapacity());
                System.out.println("No. of seats reserved: " + noOfSeatsReserved);
                System.out.println("No. of seats remaining: " + (cabinClass.getMaxCapacity() - noOfSeatsReserved));
                System.out.println("-----------------------------------");
            }
            
            int totalNoOfSeats = selectedFlightSchedule.getFlight().getAircraftConfiguration().getTotalCabinClassCapacity();
            int totalNoOfSeatsReserved = selectedFlightSchedule.getFlightReservations().size();
            System.out.println("Total no. of seats for flight schedule: " + totalNoOfSeats);
            System.out.println("Total no. of seats reserved for flight schedule: " + totalNoOfSeatsReserved);
            System.out.println("Total no. of seats remaining for flight schedule: " + (totalNoOfSeats - totalNoOfSeatsReserved) + "\n");
        } catch (InvalidEntityIdException e) {
            System.out.println(e.getMessage());
        } catch (NotAuthenticatedException e) {
            System.out.println("You do not have permission to do this!");
        }
    }
    
    private void displayViewFlightReservationsMenu() {
        System.out.println("*** View Seats Inventory ***");
        System.out.println("Enter flight number: ");
        String flightCode = scanner.next();
        
        try {
            List<FlightSchedule> flightSchedules = this.salesManagerBeanRemote.getFlightSchedulesByFlightCode(flightCode);
            ListIterator<FlightSchedule> iterateSchedules = flightSchedules.listIterator();
            
            while (iterateSchedules.hasNext()) {
                FlightSchedule flightSchedule = iterateSchedules.next();
                System.out.println((iterateSchedules.nextIndex()) + ". " + flightSchedule.getFlight().getFlightCode() + 
                        " Departure date/time: " + flightSchedule.getDepartureDateTime() +
                        " Estimated duration: " + flightSchedule.getEstimatedDuration());
            }
            System.out.println("-----------------------------------");
            
            System.out.println("Enter the index of the flight schedule you would like to select: ");
            final int index = scanner.nextInt();
            FlightSchedule selectedFlightSchedule = flightSchedules.get(index-1);
            List<FlightReservation> flightReservations = this.salesManagerBeanRemote.getFlightReservations(selectedFlightSchedule);
            
            for (FlightReservation flightReservation : flightReservations) {
                System.out.println("Seat number: " + flightReservation.getSeatNumber());
                System.out.println("Passenger name: " + flightReservation.getPassengerFirstName() + " " + flightReservation.getPassengerLastName());
                Fare fare = this.salesManagerBeanRemote.getFareForFlightReservation(flightReservation);
                System.out.println("Fare basis code: " + fare.getFareBasisCode());
                System.out.println("-----------------------------------");
            }
            
        } catch (InvalidEntityIdException e) {
            System.out.println(e.getMessage());
        } catch (NotAuthenticatedException e) {
            System.out.println("You do not have permission to do this!");
        }
    }
}
