package controllers;

import entities.Employee;
import entities.Flight;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface FlightBeanRemote {

    Flight create(Employee employee, String flightCode, String origin, String destination, Long aircraftConfigurationId) throws InvalidConstraintException, InvalidEntityIdException, NotAuthenticatedException;

    boolean addReturnFlight(Employee employee, String mainFlightCode, String returnFlightCode) throws NotAuthenticatedException;

    boolean checkFlightRoute(Employee employee, String origin, String destination) throws InvalidEntityIdException, NotAuthenticatedException;

    List<Flight> getFlights(Employee employee) throws NotAuthenticatedException;

    Flight getFlightByFlightCode(Employee employee, String flightCode) throws NotAuthenticatedException;
}
