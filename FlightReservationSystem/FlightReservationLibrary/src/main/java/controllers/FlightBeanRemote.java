package controllers;

import entities.Employee;
import entities.Flight;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;

import javax.ejb.Remote;
import java.util.List;
import java.util.Set;

@Remote
public interface FlightBeanRemote {

    Flight create(Employee employee, String flightCode, String origin, String destination, Long aircraftConfigurationId) throws InvalidConstraintException, InvalidEntityIdException, NotAuthenticatedException;

    List<Flight> getFlights(Employee employee) throws NotAuthenticatedException;
    
    Set<List<Flight>> getReturnFlights (Employee employee, Flight flight) throws NotAuthenticatedException;

    Flight getFlightByFlightCode(Employee employee, String flightCode) throws NotAuthenticatedException;
    
    void updateFlightRoute(Employee employee, String flightCode, String newOrigin, String newDestination) throws NotAuthenticatedException, InvalidEntityIdException;
    
    void updateAircraftConfiguration(Employee employee, String flightCode, Long aircraftConfigurationId) throws NotAuthenticatedException, InvalidEntityIdException;
}