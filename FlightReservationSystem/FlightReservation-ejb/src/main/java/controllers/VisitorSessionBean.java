package controllers;

import entities.*;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import lombok.NonNull;
import services.*;

import javax.ejb.Stateful;
import javax.inject.Inject;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Stateful
public class VisitorSessionBean implements VisitorBeanRemote {
    @Inject
    CustomerService customerService;
    @Inject
    AirportService airportService;
    @Inject
    FlightRouteService flightRouteService;
    @Inject
    FlightScheduleService flightScheduleService;

    @Override
    public Customer register(String firstName,
                             String lastName,
                             String email,
                             String password,
                             String phoneNumber,
                             String address) throws InvalidConstraintException {
        return this.customerService.create(firstName, lastName, email, password, phoneNumber, address);
    }

    //TODO: implement this
    @Override
    public List<FlightSchedule> searchFlight(@NonNull Airport departureAirport,
                                             @NonNull Airport destinationAirport,
                                             @NonNull Date departureDate,
                                             Date returnDate,
                                             @NonNull Integer passengerCount,
                                             Boolean directOnly,
                                             CabinClassType cabinClassType) throws InvalidConstraintException, InvalidEntityIdException {
        final Airport managedDepartureAirport = this.airportService.findAirportByCode(departureAirport.getIataCode());
        final Airport managedDestinationAirport = this.airportService.findAirportByCode(destinationAirport.getIataCode());
        final FlightRoute flightRoute = this.flightRouteService.findFlightRouteByOriginDest(managedDepartureAirport, managedDestinationAirport);
        final List<FlightSchedule> searchResult = new ArrayList<>();

        if (returnDate != null) {
            // Return date specified
            // Check if departure date is earlier than return date
            if (departureDate.before(returnDate)) {
                // Valid
            }
        } else {
            if (directOnly != null && directOnly) {

            } else {
                if (cabinClassType != null) {

                } else {
                    // Basic search only
                    searchResult.addAll(this.flightScheduleService.searchFlightSchedules(flightRoute, departureDate, passengerCount));
                }
            }
        }

        return searchResult;
    }
}
