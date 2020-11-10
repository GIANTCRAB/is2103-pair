package controllers;

import entities.Airport;
import entities.Customer;
import entities.Flight;
import exceptions.IncorrectCredentialsException;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import lombok.NonNull;

import javax.ejb.Remote;
import java.sql.Date;
import java.util.List;

@Remote
public interface VisitorBeanRemote {
    Customer register(String firstName,
                      String lastName,
                      String email,
                      String password,
                      String phoneNumber,
                      String address) throws InvalidConstraintException;

    Customer login(String email, String password) throws IncorrectCredentialsException;

    List<Flight> searchFlight(@NonNull Airport departureAirport,
                              @NonNull Airport destinationAirport,
                              @NonNull Date departureDate,
                              Date returnDate,
                              @NonNull Integer passengerCount) throws InvalidConstraintException, InvalidEntityIdException;
}
