package controllers;

import entities.Employee;
import entities.Flight;
import exceptions.IncorrectCredentialsException;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;

import javax.ejb.Remote;
import java.util.List;
import java.util.Set;

@Remote
public interface FlightBeanRemote {
    Employee login(String username, String password) throws IncorrectCredentialsException, InvalidEntityIdException;

    Flight create(String flightCode, String origin, String destination, Long aircraftConfigurationId) throws InvalidConstraintException, InvalidEntityIdException, NotAuthenticatedException;

    List<Flight> getFlights() throws NotAuthenticatedException;
    
    Set<List<Flight>> getReturnFlights (Flight flight) throws NotAuthenticatedException;

    Flight getFlightByFlightCode(String flightCode) throws NotAuthenticatedException;
    
    void updateFlightRoute(String flightCode, String newOrigin, String newDestination) throws NotAuthenticatedException, InvalidEntityIdException;
    
    void updateAircraftConfiguration(String flightCode, Long aircraftConfigurationId) throws NotAuthenticatedException, InvalidEntityIdException;

    String deleteFlight(String flightCode);

    void updateAircraftConfiguration(String flightCode, Long aircraftConfigurationId) throws NotAuthenticatedException, InvalidEntityIdException;

}
