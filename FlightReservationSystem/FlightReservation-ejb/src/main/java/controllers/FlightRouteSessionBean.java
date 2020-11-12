package controllers;

import entities.Airport;
import entities.Employee;
import entities.EmployeeRole;
import entities.FlightRoute;
import exceptions.*;
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
    private Employee loggedInEmployee = null;
    @Inject
    AuthService authService;
    @Inject
    AirportService airportService;
    @Inject
    FlightRouteService flightRouteService;

    private final EmployeeRole PERMISSION_REQUIRED = EmployeeRole.ROUTE_PLANNER;

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
    public boolean checkFlightRoute(String origin, String destination) throws InvalidEntityIdException, NotAuthenticatedException {
        final Airport originAirport = this.airportService.findAirportByCode(origin);
        final Airport destinationAirport = this.airportService.findAirportByCode(destination);

        return (flightRouteService.findFlightRouteByOriginDest(originAirport, destinationAirport) != null);
    }

    @Override
    public FlightRoute create(String origin, String destination) throws InvalidConstraintException, InvalidEntityIdException, NotAuthenticatedException, EntityAlreadyExistException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }

        final Airport originAirport = this.airportService.findAirportByCode(origin);
        final Airport destinationAirport = this.airportService.findAirportByCode(destination);

        return this.flightRouteService.create(originAirport, destinationAirport);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FlightRoute createRoundTrip(String origin, String destination) throws InvalidConstraintException, InvalidEntityIdException, NotAuthenticatedException, EntityAlreadyExistException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }

        final Airport originAirport = this.airportService.findAirportByCode(origin);
        final Airport destinationAirport = this.airportService.findAirportByCode(destination);

        final FlightRoute mainFlightRoute = this.flightRouteService.create(originAirport, destinationAirport);
        final FlightRoute returnFlightRoute = this.flightRouteService.create(destinationAirport, originAirport);

        return mainFlightRoute;
    }

    @Override
    public List<FlightRoute> getFlightRoutes() throws NotAuthenticatedException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }

        return this.flightRouteService.getFlightRoutes();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteFlightRoute(FlightRoute flightRoute) throws InvalidEntityIdException, NotAuthenticatedException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }

        final FlightRoute managedFlightRoute = this.flightRouteService.findById(flightRoute.getFlightRouteId());
        this.flightRouteService.delete(managedFlightRoute);
    }
}
