package controllers;

import entities.EmployeeRole;
import entities.Employee;
import entities.Flight;
import entities.FlightRoute;
import entities.Airport;
import entities.AircraftConfiguration;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;

import java.util.List;

import services.AuthService;
import services.FlightService;
import services.FlightRouteService;
import services.AircraftConfigurationService;

import javax.ejb.Stateful;
import javax.inject.Inject;

import services.AirportService;


@Stateful
public class FlightSessionBean implements FlightBeanRemote {
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
    public Flight create(Employee employee, String flightCode, String origin, String destination, Long aircraftConfigurationId) throws InvalidConstraintException, InvalidEntityIdException, NotAuthenticatedException {
        this.authService.checkPermission(employee, this.PERMISSION_REQUIRED);

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
    public boolean addReturnFlight(Employee employee, String mainFlightCode, String returnFlightCode) throws NotAuthenticatedException {
        this.authService.checkPermission(employee, this.PERMISSION_REQUIRED);

        Flight mainFlight = this.flightService.getFlightByFlightCode(mainFlightCode);
        Flight returnFlight = this.flightService.getFlightByFlightCode(returnFlightCode);

        this.flightService.associateReturnFlight(mainFlight, returnFlight);
        return true;
    }

    @Override
    public List<Flight> getFlights(Employee employee) throws NotAuthenticatedException {
        this.authService.checkPermission(employee, this.PERMISSION_REQUIRED);

        return this.flightService.getFlights();
    }

    @Override
    public Flight getFlightByFlightCode(Employee employee, String flightCode) throws NotAuthenticatedException {
        this.authService.checkPermission(employee, this.PERMISSION_REQUIRED);

        return this.flightService.getFlightByFlightCode(flightCode);
    }

}
