package controllers;

import entities.CabinClassType;
import entities.Employee;
import entities.Fare;
import entities.Flight;
import entities.FlightReservation;
import entities.FlightSchedule;
import exceptions.IncorrectCredentialsException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;
import lombok.NonNull;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface SalesManagerBeanRemote {
    Employee login(String username, String password) throws IncorrectCredentialsException, InvalidEntityIdException;

    List<FlightSchedule> getFlightSchedulesByFlightCode(@NonNull String flightCode) throws NotAuthenticatedException, InvalidEntityIdException;

    FlightSchedule getFlightScheduleDetails(@NonNull FlightSchedule flightSchedule) throws NotAuthenticatedException, InvalidEntityIdException;

    List<FlightReservation> getFlightReservations(@NonNull FlightSchedule flightSchedule) throws NotAuthenticatedException;
    
    int getNoOfSeatsReservedForCabinClass(FlightSchedule flightSchedule,  CabinClassType cabinClassType) throws NotAuthenticatedException;
    
    Fare getFareForFlightReservation(@NonNull FlightReservation flightReservation) throws NotAuthenticatedException, InvalidEntityIdException;
    
}
