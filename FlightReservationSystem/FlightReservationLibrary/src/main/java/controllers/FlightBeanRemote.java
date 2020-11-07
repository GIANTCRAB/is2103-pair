package controllers;

import entities.Employee;
import entities.Flight;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;
import java.util.List;

public interface FlightBeanRemote {
    
    public Flight create(Employee employee, String flightCode, String origin, String destination, Long aircraftConfigurationId) throws InvalidConstraintException, InvalidEntityIdException, NotAuthenticatedException;
    
    public boolean addReturnFlight(Employee employee, String mainFlightCode, String returnFlightCode) throws NotAuthenticatedException;
    
    public boolean checkFlightRoute(Employee employee, String origin, String destination) throws InvalidEntityIdException, NotAuthenticatedException;
    
    public List<Flight> getFlights(Employee employee) throws NotAuthenticatedException;
    
    public Flight getFlightByFlightCode(Employee employee, String flightCode) throws NotAuthenticatedException;
}
