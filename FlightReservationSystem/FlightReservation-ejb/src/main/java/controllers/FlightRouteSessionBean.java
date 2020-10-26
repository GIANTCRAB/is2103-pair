package controllers;

import entities.Airport;
import entities.Employee;
import entities.EmployeeRole;
import entities.FlightRoute;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;
import services.AirportService;
import services.AuthService;
import services.FlightRouteService;

import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.List;

@Stateful
public class FlightRouteSessionBean implements FlightRouteBeanRemote {
    @Inject
    AuthService authService;
    @Inject
    AirportService airportService;
    @Inject
    FlightRouteService flightRouteService;

    private final EmployeeRole PERMISSION_REQUIRED = EmployeeRole.ROUTE_PLANNER;

    @Override
    public FlightRoute create(Employee employee, String origin, String destination) throws InvalidConstraintException, InvalidEntityIdException, NotAuthenticatedException {
        this.authService.checkPermission(employee, this.PERMISSION_REQUIRED);

        final Airport originAirport = this.airportService.findAirportByCode(origin);
        final Airport destinationAirport = this.airportService.findAirportByCode(destination);

        return this.flightRouteService.create(originAirport, destinationAirport);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FlightRoute createRoundTrip(Employee employee, String origin, String destination) throws InvalidConstraintException, InvalidEntityIdException, NotAuthenticatedException {
        this.authService.checkPermission(employee, this.PERMISSION_REQUIRED);

        final Airport originAirport = this.airportService.findAirportByCode(origin);
        final Airport destinationAirport = this.airportService.findAirportByCode(destination);

        final FlightRoute flightRoute = this.flightRouteService.create(originAirport, destinationAirport);
        this.flightRouteService.create(destinationAirport, originAirport);

        return flightRoute;
    }

    @Override
    public List<FlightRoute> getFlightRoutes(Employee employee) throws NotAuthenticatedException {
        this.authService.checkPermission(employee, this.PERMISSION_REQUIRED);

        return this.flightRouteService.getFlightRoutes();
    }
}
