package org.example;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import webservices.HolidayReservationService.*;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

@RequiredArgsConstructor
public class PartnerEmployeeClient implements SystemClient {
    @NonNull
    final Scanner scanner;
    @NonNull
    final HolidayReservationServiceBean holidayReservationServiceBean;
    @NonNull
    final Partner partner;

    public void runApp() {

    }

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
        System.out.println("Is this a round-trip? If yes, type 1.");
        final int roundTripPref = this.scanner.nextInt();
        Date returnDate = null;
        if (roundTripPref == 1) {
            System.out.println("Enter date of return: (yyyy-mm-dd)");
            returnDate = Date.valueOf(this.scanner.next());
        }
        System.out.println("Enter passenger count: ");
        final Integer passengerCount = this.scanner.nextInt();
        System.out.println("Any preference for cabin class? Type 1 if yes.");
        final int cabinClassPref = this.scanner.nextInt();
        CabinClassType cabinClassType = null;
        if (cabinClassPref == 1) {
            System.out.println("What's your preference? (" + Arrays.toString(CabinClassType.values()) + ")");
            cabinClassType = CabinClassType.valueOf(this.scanner.next());
        }
        System.out.println("If you prefer directly flights only, type 1.");
        final int directFlightPref = this.scanner.nextInt();
        boolean directOnly = false;
        if (directFlightPref == 1) {
            directOnly = true;
        }
        System.out.println("=======================");
    }
}
