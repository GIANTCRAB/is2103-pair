package org.example;

import controllers.CustomerBeanRemote;
import controllers.VisitorBeanRemote;
import entities.Airport;
import entities.CabinClassType;
import entities.Customer;
import entities.FlightSchedule;
import exceptions.IncorrectCredentialsException;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import lombok.*;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

@RequiredArgsConstructor
@AllArgsConstructor
public class ReservationClient implements SystemClient {
    @NonNull
    protected final VisitorBeanRemote visitorBeanRemote;
    @NonNull
    protected final CustomerBeanRemote customerBeanRemote;
    protected Scanner scanner;

    @Override
    public void runApp() {
        this.scanner = new Scanner(System.in);
        this.scanner.useDelimiter("\n");
        this.displayVisitorMenu();
        this.scanner.close();
    }

    private void displayVisitorMenu() {
        boolean loop = true;

        while (loop) {
            System.out.println("*** Flight Reservation Client ***");
            System.out.println("1: Registration");
            System.out.println("2: Customer Login");
            System.out.println("3: Search Flight");
            System.out.println("4: Exit");
            final int option = this.scanner.nextInt();

            switch (option) {
                case 1:
                    displayRegistrationMenu();
                    break;
                case 2:
                    displayLoginMenu();
                    break;
                case 3:
                    displayFlightSearchMenu();
                    break;
                case 4:
                default:
                    loop = false;
                    break;
            }
        }
    }

    private void displayRegistrationMenu() {
        boolean loop = true;

        while (loop) {
            System.out.println("Enter First Name:");
            final String firstName = this.scanner.next();
            System.out.println("Enter Last Name:");
            final String lastName = this.scanner.next();
            System.out.println("Enter Email:");
            final String email = this.scanner.next();
            System.out.println("Enter Password:");
            final String password = this.scanner.next();
            System.out.println("Enter Phone Number:");
            final String phoneNumber = this.scanner.next();
            System.out.println("Enter Address:");
            final String address = this.scanner.next();

            try {
                final Customer customer = this.visitorBeanRemote.register(firstName, lastName, email, password, phoneNumber, address);
                System.out.println("Successfully registered as " + customer.getFirstName() + " (ID: " + customer.getCustomerId() + ")");
                loop = false;
            } catch (InvalidConstraintException e) {
                this.displayConstraintErrorMessage(e);
            }
        }

    }

    private void displayLoginMenu() {
        boolean loop = true;

        while (loop) {
            System.out.println("Enter Email:");
            final String email = this.scanner.next();
            System.out.println("Enter Password:");
            final String password = this.scanner.next();

            try {
                final Customer customer = this.customerBeanRemote.login(email, password);
                System.out.println("Logged in as " + customer.getFirstName() + " (ID: " + customer.getCustomerId() + ")");
                final CustomerClient customerClient = new CustomerClient(scanner, visitorBeanRemote, customerBeanRemote, customer);
                customerClient.runApp();
                loop = false;
            } catch (IncorrectCredentialsException e) {
                System.out.println("Invalid login details!");
            }
        }
    }

    // TODO: implement all search
    protected void displayFlightSearchMenu() {
        System.out.println("**** Flight Search ****");
        System.out.println("Enter departure airport: ");
        final String departureAirportCode = this.scanner.next();
        final Airport departureAirport = new Airport();
        departureAirport.setIataCode(departureAirportCode);
        System.out.println("Enter destination airport: ");
        final String destinationAirportCode = this.scanner.next();
        final Airport destinationAirport = new Airport();
        destinationAirport.setIataCode(destinationAirportCode);
        System.out.println("Enter departure date: (yyyy-mm-dd)");
        final Date departureDate = Date.valueOf(this.scanner.next());
        System.out.println("Enter passenger count: ");
        final Integer passengerCount = this.scanner.nextInt();
        System.out.println("Any preference for cabin class? Type 1 if yes.");
        final int cabinClassPref = this.scanner.nextInt();
        CabinClassType cabinClassType = null;
        if (cabinClassPref == 1) {
            System.out.println("What's your preference? (" + Arrays.toString(CabinClassType.values()) + ")");
            cabinClassType = CabinClassType.valueOf(this.scanner.next());
        }
        System.out.println("=======================");

        try {
            Set<List<FlightSchedule>> possibleFlightScheduleList = this.visitorBeanRemote.searchFlight(departureAirport, destinationAirport, departureDate, passengerCount, null, cabinClassType);
            for (List<FlightSchedule> flightScheduleList : possibleFlightScheduleList) {
                System.out.println("========== Possible schedule route ==========");
                flightScheduleList.forEach(flightSchedule -> {
                    System.out.println("Flight Schedule ID: " + flightSchedule.getFlightScheduleId());
                    System.out.println("Departure Airport: " + flightSchedule.getFlight().getFlightRoute().getOrigin().getIataCode());
                    System.out.println("Departure DateTime: " + flightSchedule.getDepartureDateTime().toString());
                    System.out.println("Arrival Airport: " + flightSchedule.getFlight().getFlightRoute().getDest().getIataCode());
                    System.out.println("Estimated Arrival: " + flightSchedule.getArrivalDateTime().toString());
                    flightSchedule.getFlightSchedulePlan().getFares().forEach(fare -> {
                        System.out.println("Cabin Class Type: " + fare.getCabinClass().getCabinClassId().getCabinClassType());
                        System.out.println("Fare Amount: " + fare.getFareAmount());
                    });
                    System.out.println("=======================");
                });
                System.out.println("=================================================");
            }
        } catch (InvalidEntityIdException e) {
            System.out.println("Invalid airport codes.");
        }
    }

    private void displayConstraintErrorMessage(InvalidConstraintException invalidConstraintException) {
        System.out.println("There were some validation errors!");
        System.out.println(invalidConstraintException.toString());
    }
}
