package org.example;

import controllers.CustomerBeanRemote;
import controllers.VisitorBeanRemote;
import entities.Customer;
import entities.FlightReservation;
import entities.FlightReservationPayment;
import exceptions.InvalidEntityIdException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Scanner;

@RequiredArgsConstructor
public class CustomerClient implements SystemClient {
    @NonNull
    private final Scanner scanner;
    @NonNull
    private final VisitorBeanRemote visitorBeanRemote;
    @NonNull
    private final CustomerBeanRemote customerBeanRemote;
    @NonNull
    private final Customer customer;

    @Override
    public void runApp() {
        this.displayCustomerMenu();
    }

    private void displayCustomerMenu() {
        boolean loop = true;

        while (loop) {
            System.out.println("*** Customer Flight Reservation Client ***");
            System.out.println("1: Search Flight");
            System.out.println("2: View My Reservations");
            System.out.println("3: Logout");
            final int option = this.scanner.nextInt();

            switch (option) {
                case 1:
                    //TODO: implement flight search, flight reservation
                    break;
                case 2:
                    this.displayViewReservationsMenu();
                    break;
                case 3:
                default:
                    loop = false;
                    break;
            }
        }
    }

    private void displayViewReservationsMenu() {
        try {
            final List<FlightReservation> flightReservations = this.customerBeanRemote.getFlightReservations(this.customer);

            flightReservations.forEach(flightReservation -> {
                System.out.println("ID: " + flightReservation.getFlightReservationId() + " FROM " + flightReservation.getFlightSchedule().getFlight().getFlightRoute().getOrigin().getIataCode() + " -> " + flightReservation.getFlightSchedule().getFlight().getFlightRoute().getDest().getIataCode());
            });

            System.out.println("***View specific details***");
            System.out.println("Enter flight reservation ID or type 0 to exit view.");

            final int option = this.scanner.nextInt();
            if (option > 0 && option < flightReservations.size()) {
                this.displayViewSpecificReservation(flightReservations.get(option));
            }
        } catch (InvalidEntityIdException e) {
            System.out.println("Invalid customer details. Please re-authenticate.");
        }
    }

    private void displayViewSpecificReservation(FlightReservation flightReservation) {
        try {
            final FlightReservationPayment flightReservationPayment = this.customerBeanRemote.getFlightReservationDetails(this.customer, flightReservation.getFlightReservationPayment());

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
        } catch (InvalidEntityIdException e) {
            System.out.println("Invalid customer details. Please re-authenticate.");
        }
    }
}
