package controllers;

import entities.Employee;
import entities.FlightRoute;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface FlightRouteBeanRemote {
    FlightRoute create(Employee employee, String origin, String destination) throws InvalidConstraintException, InvalidEntityIdException, NotAuthenticatedException;

    FlightRoute createRoundTrip(Employee employee, String origin, String destination) throws InvalidConstraintException, InvalidEntityIdException, NotAuthenticatedException;

    List<FlightRoute> getFlightRoutes(Employee employee) throws NotAuthenticatedException;

    void deleteFlightRoute(Employee employee, FlightRoute flightRoute) throws InvalidEntityIdException, NotAuthenticatedException;
}
