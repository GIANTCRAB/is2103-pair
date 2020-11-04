package controllers;

import entities.Flight;
import exceptions.FlightRouteDoesNotExistException;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;

public interface FlightBeanRemote {
    
    public Flight create(Employee employee, String flightCode, String origin, String destination, Long aircraftConfigurationId) throws InvalidConstraintException, InvalidEntityIdException, NotAuthenticatedException, FlightRouteDoesNotExistException;
    
    public Flight createRoundTripFlight(Employee employee, String flightCode, String origin, String destination, Long aircraftConfigurationId) throws InvalidConstraintException, InvalidEntityIdException, NotAuthenticatedException, FlightRouteDoesNotExistException;
    
    public List<Flights> getFlights(Employee employee) throws NotAuthenticatedException;
    
    public Flight getFlightByFlightCode(Employee employee, String flightCode) throws NotAuthenticatedException;
}
