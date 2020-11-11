package org.example;

import lombok.NonNull;
import webservices.HolidayReservationService.*;

import java.util.List;
import java.util.Scanner;

public class PartnerManagerClient extends PartnerEmployeeClient {

    public PartnerManagerClient(@NonNull Scanner scanner, @NonNull HolidayReservationServiceBean holidayReservationServiceBean, @NonNull Partner partner) {
        super(scanner, holidayReservationServiceBean, partner);
    }

    //TODO: complete all methods
    public void runApp() {

    }

    private void displayViewPartnerFlightReservationsMenu() {
        try {
            final List<FlightReservation> flightReservationList = this.holidayReservationServiceBean.getFlightReservations();
            for (FlightReservation flightReservation : flightReservationList) {
                System.out.println("ID: " + flightReservation.getFlightReservationId() + " FROM " + flightReservation.getFlightSchedule().getFlight().getFlightRoute().getOrigin().getIataCode() + " -> " + flightReservation.getFlightSchedule().getFlight().getFlightRoute().getDest().getIataCode());
            }

            System.out.println("***View specific details***");
            System.out.println("Enter flight reservation ID or type 0 to exit view.");
        } catch (InvalidEntityIdException_Exception e) {
            System.out.println("Partner ID invalid!");
        } catch (NotAuthenticatedException_Exception e) {
            System.out.println("Not logged in!");
        }
    }
}
