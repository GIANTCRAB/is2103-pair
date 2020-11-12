package controllers;

import entities.Employee;
import entities.FlightRoute;
import exceptions.*;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface FlightRouteBeanRemote {
    Employee login(String username, String password) throws IncorrectCredentialsException, InvalidEntityIdException;

    boolean checkFlightRoute(String origin, String destination) throws InvalidEntityIdException, NotAuthenticatedException;

    FlightRoute create(String origin, String destination) throws InvalidConstraintException, InvalidEntityIdException, NotAuthenticatedException, EntityAlreadyExistException;

    FlightRoute createRoundTrip(String origin, String destination) throws InvalidConstraintException, InvalidEntityIdException, NotAuthenticatedException, EntityAlreadyExistException;

    List<FlightRoute> getFlightRoutes() throws NotAuthenticatedException, InvalidEntityIdException;

    void deleteFlightRoute(FlightRoute flightRoute) throws InvalidEntityIdException, NotAuthenticatedException;
}
