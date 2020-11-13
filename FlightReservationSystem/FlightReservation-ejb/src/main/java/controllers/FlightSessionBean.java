package controllers;

import entities.EmployeeRole;
import entities.Employee;
import entities.Flight;
import entities.FlightRoute;
import entities.Airport;
import entities.AircraftConfiguration;

import exceptions.EntityAlreadyExistException;
import exceptions.EntityIsDisabledException;
import exceptions.IncorrectCredentialsException;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;
import java.util.ArrayList;

import java.util.List;
import java.util.Set;

import services.AuthService;
import services.FlightService;
import services.FlightRouteService;
import services.AircraftConfigurationService;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.NoResultException;

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
    public Flight create(String flightCode, String origin, String destination, Long aircraftConfigurationId) throws InvalidConstraintException, InvalidEntityIdException, NotAuthenticatedException, EntityIsDisabledException, EntityAlreadyExistException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }
        final Airport originAirport = this.airportService.findAirportByCode(origin);
        final Airport destinationAirport = this.airportService.findAirportByCode(destination);
        FlightRoute flightRoute = this.flightRouteService.findFlightRouteByOriginDest(originAirport, destinationAirport);
        AircraftConfiguration aircraftConfiguration = this.aircraftConfigurationService.getAircraftConfigurationById(aircraftConfigurationId);

        if (!flightRoute.getEnabled()) {
            throw new EntityIsDisabledException("Selected flight route is disabled.");
        }
        
        if (this.flightService.getFlightByFlightCode(flightCode) != null ) {
            throw new EntityAlreadyExistException("Flight code is already in use.");
        }
        
        if (aircraftConfiguration == null) {
            throw new InvalidEntityIdException("Invalid aircraft configuration ID.");
        }
        return this.flightService.create(flightCode, flightRoute, aircraftConfiguration);

    }

    @Override
    public List<Flight> getFlights() throws NotAuthenticatedException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }

        return sortFlights(this.flightService.getFlights());
    }
    
    private List<Flight> sortFlights(List<Flight> flights) throws NotAuthenticatedException {
        List<Flight> sortedFlights = new ArrayList<>();
        for (Flight flight : flights) {
            Flight returnFlight = getDirectReturnFlightByFlightCode(flight.getFlightCode());
            if(!sortedFlights.contains(flight)) {
                sortedFlights.add(flight);
            }

            if(returnFlight != null && !sortedFlights.contains(returnFlight)) {
                sortedFlights.add(returnFlight);
            }
        }
        return sortedFlights;
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
    public Flight getDirectReturnFlightByFlightCode(String flightCode) throws NotAuthenticatedException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }
        Flight flight = this.flightService.getFlightByFlightCode(flightCode);
        String origin = flight.getFlightRoute().getOrigin().getIataCode();
        String destination = flight.getFlightRoute().getDest().getIataCode();
        AircraftConfiguration aircraftConfiguration = flight.getAircraftConfiguration();
        
        try {
            Flight returnFlight = this.flightService.getFlightByOriginDestAndAircraftConfiguration(destination, origin, aircraftConfiguration.getAircraftConfigurationId());
            return returnFlight;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void updateFlightRoute(String flightCode, String newOrigin, String newDestination) throws NotAuthenticatedException, InvalidEntityIdException, EntityAlreadyExistException, EntityIsDisabledException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }
        
        Flight flight = getFlightByFlightCode(flightCode);
        final Airport originAirport = this.airportService.findAirportByCode(newOrigin);
        final Airport destinationAirport = this.airportService.findAirportByCode(newDestination);
        FlightRoute flightRoute = this.flightRouteService.findFlightRouteByOriginDest(originAirport, destinationAirport);
        
        if (!flightRoute.getEnabled()) {
            throw new EntityIsDisabledException("Selected flight route is disabled.");
        }
        if(flightRoute.getFlights().contains(flight)) {
            throw new EntityAlreadyExistException("There is an existing flight with the chosen flight route.");
        } else {
            this.flightService.updateFlightRoute(flight, flightRoute);
        }
    }

    @Override
    public void updateAircraftConfiguration(String flightCode, String aircraftConfigurationName) throws NotAuthenticatedException, InvalidEntityIdException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }

        final AircraftConfiguration aircraftConfiguration = this.aircraftConfigurationService.getAircraftConfigurationByName(aircraftConfigurationName);

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

    @Override
    public void logout() throws NotAuthenticatedException {
        if (loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }

        loggedInEmployee = null;
    }
}
