package org.example;

import controllers.CustomerBeanRemote;
import controllers.VisitorBeanRemote;
import entities.Customer;
import entities.FlightReservation;
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
                System.out.println("ID: " + flightReservation.getFlightReservationId() + " FROM " + flightReservation.getFare().getFlightSchedule().getFlight().getFlightRoute().getOrigin().getIataCode() + " -> " + flightReservation.getFare().getFlightSchedule().getFlight().getFlightRoute().getDest().getIataCode());
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

    }
}
