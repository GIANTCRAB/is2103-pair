package controllers;

import entities.EmployeeRole;
import entities.Employee;
import entities.Flight;
import entities.FlightRoute;
import entities.Airport;
import entities.AircraftConfiguration;
import exceptions.IncorrectCredentialsException;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;

import java.util.List;
import java.util.Set;

import services.AuthService;
import services.FlightService;
import services.FlightRouteService;
import services.AircraftConfigurationService;

import javax.ejb.Stateful;
import javax.inject.Inject;

import services.AirportService;


@Stateful
public class FlightSessionBean implements FlightBeanRemote {
    private Employee loggedInEmployee;
    @Inject
    FlightService flightService;
    @Inject
    FlightRouteService flightRouteService;
    @Inject
    AircraftConfigurationService aircraftConfigurationService;
    @Inject
    AirportService airportService;
    @Inject
    AuthService authService;

    private final EmployeeRole PERMISSION_REQUIRED = EmployeeRole.SCHEDULE_MANAGER;

    @Override
    public Employee login(String username, String password) throws IncorrectCredentialsException, InvalidEntityIdException {
        final Employee employee = this.authService.employeeLogin(username, password);

        if (employee.getEmployeeRole().equals(PERMISSION_REQUIRED)) {
            this.loggedInEmployee = employee;
            return employee;
        } else {
            throw new InvalidEntityIdException();
        }
    }

    @Override
    public Flight create(String flightCode, String origin, String destination, Long aircraftConfigurationId) throws InvalidConstraintException, InvalidEntityIdException, NotAuthenticatedException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }
        final Airport originAirport = this.airportService.findAirportByCode(origin);
        final Airport destinationAirport = this.airportService.findAirportByCode(destination);
        FlightRoute flightRoute = this.flightRouteService.findFlightRouteByOriginDest(originAirport, destinationAirport);
        AircraftConfiguration aircraftConfiguration = this.aircraftConfigurationService.getAircraftConfigurationById(aircraftConfigurationId);

        if (aircraftConfiguration == null) {
            throw new InvalidEntityIdException();
        }

        return this.flightService.create(flightCode, flightRoute, aircraftConfiguration);

    }

    @Override
    public List<Flight> getFlights() throws NotAuthenticatedException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }

        return this.flightService.getFlights();
    }

    @Override
    public Set<List<Flight>> getReturnFlights(Flight flight) throws NotAuthenticatedException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }

        return this.flightService.getReturnFlights(this.flightService.findById(flight.getFlightId()));
    }

    @Override
    public Flight getFlightByFlightCode(String flightCode) throws NotAuthenticatedException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }

        return this.flightService.getFlightByFlightCode(flightCode);
    }

    @Override
    public void updateFlightRoute(String flightCode, String newOrigin, String newDestination) throws NotAuthenticatedException, InvalidEntityIdException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }

        final Airport originAirport = this.airportService.findAirportByCode(newOrigin);
        final Airport destinationAirport = this.airportService.findAirportByCode(newDestination);
        FlightRoute flightRoute = this.flightRouteService.findFlightRouteByOriginDest(originAirport, destinationAirport);

        if (flightRoute != null) {
            this.flightService.updateFlightRoute(flightCode, flightRoute);
        }
    }

    @Override
    public void updateAircraftConfiguration(String flightCode, Long aircraftConfigurationId) throws NotAuthenticatedException, InvalidEntityIdException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }

        final AircraftConfiguration aircraftConfiguration = this.aircraftConfigurationService.getAircraftConfigurationById(aircraftConfigurationId);

        if (aircraftConfiguration != null) {
            this.flightService.updateAircraftConfiguration(flightCode, aircraftConfiguration);
        } else {
            throw new InvalidEntityIdException();
        }
    }
    
    @Override
    public String deleteFlight(String flightCode) throws NotAuthenticatedException {
        
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }
        
        Flight flight = this.flightService.getFlightByFlightCode(flightCode);
        String msg;
        
        if (flight.getFlightSchedules().isEmpty()) {
            this.flightService.delete(flight);
            msg = "Flight deleted successfully.";
        } else {
            this.flightService.disable(flight);
            msg = "Flight is in use, will be disabled instead.";
        }
        return msg;
    }
}
