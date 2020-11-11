package controllers;

import entities.*;
import exceptions.IncorrectCredentialsException;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;
import lombok.NonNull;
import pojo.Passenger;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface CustomerBeanRemote {
    Customer login(String email, String password) throws IncorrectCredentialsException;

    FlightReservationPayment reserveFlightForPassengers(String creditCard,
                                                        @NonNull FlightSchedule flightSchedule,
                                                        @NonNull CabinClassType cabinClassType,
                                                        @NonNull List<Passenger> passengers) throws NotAuthenticatedException, InvalidEntityIdException, InvalidConstraintException;

    List<FlightReservation> getFlightReservations() throws NotAuthenticatedException;

    FlightReservationPayment getFlightReservationDetails(FlightReservationPayment flightReservationPayment) throws NotAuthenticatedException, InvalidEntityIdException;

    void logout() throws NotAuthenticatedException;
}
