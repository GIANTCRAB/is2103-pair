package controllers;

import entities.*;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import lombok.NonNull;
import pojo.Passenger;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface CustomerBeanRemote {
    List<FlightReservation> reserveFlightForPassengers(@NonNull Customer customer,
                                                       String creditCard,
                                                       @NonNull FlightSchedule flightSchedule,
                                                       @NonNull CabinClassType cabinClassType,
                                                       @NonNull List<Passenger> passengers) throws InvalidEntityIdException, InvalidConstraintException;

    List<FlightReservation> getFlightReservations(@NonNull Customer customer) throws InvalidEntityIdException;

    FlightReservationPayment getFlightReservationDetails(@NonNull Customer customer, FlightReservationPayment flightReservationPayment) throws InvalidEntityIdException;
}
