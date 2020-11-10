package controllers;

import entities.Airport;
import entities.CabinClassType;
import entities.Customer;
import entities.FlightSchedule;
import exceptions.IncorrectCredentialsException;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import lombok.NonNull;
import services.AirportService;
import services.AuthService;
import services.CustomerService;
import services.FlightService;

import javax.ejb.Stateful;
import javax.inject.Inject;
import java.sql.Date;
import java.util.List;

@Stateful
public class VisitorSessionBean implements VisitorBeanRemote {
    @Inject
    CustomerService customerService;
    @Inject
    AuthService authService;
    @Inject
    AirportService airportService;

    @Override
    public Customer register(String firstName,
                             String lastName,
                             String email,
                             String password,
                             String phoneNumber,
                             String address) throws InvalidConstraintException {
        return this.customerService.create(firstName, lastName, email, password, phoneNumber, address);
    }

    @Override
    public Customer login(String email, String password) throws IncorrectCredentialsException {
        return this.authService.customerLogin(email, password);
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

        if (returnDate != null) {
            // Return date specified
            // Check if departure date is earlier than return date
            if (departureDate.before(returnDate)) {
                // Valid
            }
        }

        return null;
    }
}
