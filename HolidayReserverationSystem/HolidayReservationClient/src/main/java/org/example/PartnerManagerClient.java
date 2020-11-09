package org.example;

import lombok.NonNull;
import webservices.HolidayReservationService.FlightReservation;
import webservices.HolidayReservationService.HolidayReservationServiceBean;
import webservices.HolidayReservationService.InvalidEntityIdException_Exception;
import webservices.HolidayReservationService.Partner;

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
            final List<FlightReservation> flightReservationList = this.holidayReservationServiceBean.getFlightReservations(this.partner);
            for (FlightReservation flightReservation : flightReservationList) {
                System.out.println("ID: " + flightReservation.getFlightReservationId() + " FROM " + flightReservation.getFare().getFlightSchedule().getFlight().getFlightRoute().getOrigin().getIataCode() + " -> " + flightReservation.getFare().getFlightSchedule().getFlight().getFlightRoute().getDest().getIataCode());
            }

            System.out.println("***View specific details***");
            System.out.println("Enter flight reservation ID or type 0 to exit view.");
        } catch (InvalidEntityIdException_Exception e) {
            System.out.println("Partner ID invalid!");
        }
    }
}
