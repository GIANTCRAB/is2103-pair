package controllers;

import entities.Airport;
import entities.Customer;
import entities.Flight;
import exceptions.IncorrectCredentialsException;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import lombok.NonNull;
import services.AuthService;
import services.CustomerService;

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
    public List<Flight> searchFlight(@NonNull Airport departureAirport,
                                     @NonNull Airport destinationAirport,
                                     @NonNull Date departureDate,
                                     Date returnDate,
                                     @NonNull Integer passengerCount) throws InvalidConstraintException, InvalidEntityIdException {
        return null;
    }
}
