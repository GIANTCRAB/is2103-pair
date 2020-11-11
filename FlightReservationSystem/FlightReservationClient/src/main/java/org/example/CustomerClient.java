package org.example;

import controllers.CustomerBeanRemote;
import controllers.VisitorBeanRemote;
import entities.*;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;
import lombok.NonNull;
import pojo.Passenger;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CustomerClient extends ReservationClient {
    @NonNull
    private final Customer customer;

    public CustomerClient(@NonNull Scanner scanner,
                          @NonNull VisitorBeanRemote visitorBeanRemote,
                          @NonNull CustomerBeanRemote customerBeanRemote,
                          @NonNull Customer customer) {
        super(visitorBeanRemote, customerBeanRemote, scanner);
        this.customer = customer;
    }

    @Override
    public void runApp() {
        this.displayCustomerMenu();
    }

    private void displayCustomerMenu() {
        boolean loop = true;

        while (loop) {
            System.out.println("*** Customer Flight Reservation Client ***");
            System.out.println("1: Search Flight");
            System.out.println("2: Reserve Flight");
            System.out.println("3: View My Reservations");
            System.out.println("4: Logout");
            final int option = this.scanner.nextInt();

            switch (option) {
                case 1:
                    this.displayFlightSearchMenu();
                    break;
                case 2:
                    this.displayCreateReservationMenu();
                    break;
                case 3:
                    this.displayViewReservationsMenu();
                    break;
                case 4:
                default:
                    this.displayLogoutMenu();
                    loop = false;
                    break;
            }
        }
    }

    private void displayCreateReservationMenu() {
        System.out.println("*** Create Flight Reservation ***");
        System.out.println("Enter flight schedule ID: ");
        final Long flightScheduleId = this.scanner.nextLong();
        final FlightSchedule flightSchedule = new FlightSchedule();
        flightSchedule.setFlightScheduleId(flightScheduleId);
        System.out.println("Enter cabin class type: ");
        final CabinClassType cabinClassType = CabinClassType.valueOf(this.scanner.next());
        System.out.println("Enter number of passengers: ");
        final int numberOfPassengers = this.scanner.nextInt();

        final List<Passenger> passengers = new ArrayList<>(numberOfPassengers);

        for (int i = 0; i < numberOfPassengers; i++) {
            System.out.println("Enter passenger details for passenger no. " + (i + 1));
            final Passenger passenger = new Passenger();
            System.out.println("Enter passenger first name: ");
            passenger.setFirstName(this.scanner.next());
            System.out.println("Enter passenger last name: ");
            passenger.setLastName(this.scanner.next());
            System.out.println("Enter passenger passport number: ");
            passenger.setPassportNumber(this.scanner.next());
            System.out.println("Enter passenger seat number: ");
            passenger.setSeatNumber(this.scanner.nextInt());
            System.out.println("==================");
            passengers.add(passenger);
        }
        System.out.println("Enter your credit card details to check out: ");
        final String creditCardNumber = this.scanner.next();

        try {
            this.customerBeanRemote.reserveFlightForPassengers(creditCardNumber, flightSchedule, cabinClassType, passengers);
        } catch (NotAuthenticatedException e) {
            System.out.println("You are not logged in as customer.");
        } catch (InvalidEntityIdException e) {
            System.out.println(e.getMessage());
        } catch (InvalidConstraintException e) {
            e.printStackTrace();
        }
    }

    private void displayViewReservationsMenu() {
        try {
            System.out.println("==== Displaying personal reservations ====");

            final List<FlightReservation> flightReservations = this.customerBeanRemote.getFlightReservations();

            flightReservations.forEach(flightReservation -> {
                System.out.println("ID: " + flightReservation.getFlightReservationId() + " FROM " + flightReservation.getFlightSchedule().getFlight().getFlightRoute().getOrigin().getIataCode() + " -> " + flightReservation.getFlightSchedule().getFlight().getFlightRoute().getDest().getIataCode());
            });

            System.out.println("***View specific details***");
            System.out.println("Enter flight reservation ID or type 0 to exit view.");

            final int option = this.scanner.nextInt();
            if (option > 0 && option < flightReservations.size()) {
                this.displayViewSpecificReservation(flightReservations.get(option));
            }
        } catch (NotAuthenticatedException e) {
            System.out.println("Invalid customer details. Please re-authenticate.");
        }
    }

    private void displayViewSpecificReservation(FlightReservation flightReservation) {
        try {
            final FlightReservationPayment flightReservationPayment = this.customerBeanRemote.getFlightReservationDetails(flightReservation.getFlightReservationPayment());

            System.out.println("======================================================");
            System.out.println("*****All associated flight reservations*****");
            flightReservationPayment.getFlightReservations().forEach(fr -> {
                System.out.println("ID: " + fr.getFlightReservationId());
                System.out.println("Passenger: " + fr.getPassengerFirstName() + " " + fr.getPassengerLastName() + " (" + fr.getPassengerPassportNo() + ")");
                System.out.println("Seat number: " + fr.getSeatNumber());
                System.out.println("Cost: " + fr.getReservationCost());
                System.out.println("Cabin class: " + fr.getCabinClassType());
            });
            System.out.println("******************************************************");
            System.out.println("Total Cost: " + flightReservationPayment.getTotalCost());
            System.out.println("======================================================");
        } catch (NotAuthenticatedException e) {
            System.out.println("Invalid customer details. Please re-authenticate.");
        } catch (InvalidEntityIdException e) {
            System.out.println("Invalid flight reservation ID.");
        }
    }

    private void displayLogoutMenu() {
        try {
            this.customerBeanRemote.logout();
            System.out.println("Logged out successfully!");
        } catch (NotAuthenticatedException e) {
            System.out.println("Invalid customer details. Please re-authenticate.");
        }
    }
}
