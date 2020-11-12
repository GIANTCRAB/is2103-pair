package controllers;

import entities.*;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import lombok.NonNull;
import services.*;

import javax.ejb.Stateful;
import javax.inject.Inject;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Stateful
public class VisitorSessionBean implements VisitorBeanRemote {
    @Inject
    CustomerService customerService;
    @Inject
    AirportService airportService;
    @Inject
    FlightRouteService flightRouteService;
    @Inject
    FlightService flightService;
    @Inject
    FlightScheduleService flightScheduleService;
    @Inject
    FareService fareService;
    @Inject
    CabinClassService cabinClassService;

    @Override
    public Customer register(String firstName,
                             String lastName,
                             String email,
                             String password,
                             String phoneNumber,
                             String address) throws InvalidConstraintException {
        return this.customerService.create(firstName, lastName, email, password, phoneNumber, address);
    }

    @Override
    public Set<List<FlightSchedule>> searchFlight(@NonNull Airport departureAirport,
                                                  @NonNull Airport destinationAirport,
                                                  @NonNull Date departureDate,
                                                  @NonNull Integer passengerCount,
                                                  Boolean directOnly,
                                                  CabinClassType cabinClassType) throws InvalidEntityIdException {
        final Airport managedDepartureAirport = this.airportService.findAirportByCode(departureAirport.getIataCode());
        final Airport managedDestinationAirport = this.airportService.findAirportByCode(destinationAirport.getIataCode());
        final FlightRoute flightRoute = this.flightRouteService.findFlightRouteByOriginDest(managedDepartureAirport, managedDestinationAirport);
        final Set<List<FlightSchedule>> possibleFlightSchedules = new HashSet<>();

        final Set<List<Flight>> possibleFlights = this.flightService.getPossibleFlights(flightRoute.getOrigin(), flightRoute.getDest());

        if (directOnly != null && directOnly) {
            final List<FlightSchedule> searchResult = new ArrayList<>();
            final List<Flight> simpleFlightPath = this.flightService.getFlightByOriginDest(flightRoute.getOrigin(), flightRoute.getDest());
            if (cabinClassType != null) {
                simpleFlightPath.forEach(simpleFlightPathNode -> searchResult.addAll(this.flightScheduleService.searchFlightSchedules(simpleFlightPathNode, departureDate, passengerCount, cabinClassType)));
            } else {
                simpleFlightPath.forEach(simpleFlightPathNode -> searchResult.addAll(this.flightScheduleService.searchFlightSchedules(simpleFlightPathNode, departureDate, passengerCount)));
            }
            possibleFlightSchedules.add(searchResult);
        } else {
            if (cabinClassType != null) {
                // The flight schedule MUST have the cabin class
                for (List<Flight> possibleFlight : possibleFlights) {
                    final List<FlightSchedule> searchResult = new ArrayList<>();
                    for (Flight flightPathNode : possibleFlight) {
                        searchResult.addAll(this.flightScheduleService.searchFlightSchedules(flightPathNode, departureDate, passengerCount, cabinClassType));
                    }
                    possibleFlightSchedules.add(searchResult);
                }
            } else {
                // Basic search only
                for (List<Flight> possibleFlight : possibleFlights) {
                    final List<FlightSchedule> searchResult = new ArrayList<>();
                    for (Flight flightPathNode : possibleFlight) {
                        searchResult.addAll(this.flightScheduleService.searchFlightSchedules(flightPathNode, departureDate, passengerCount));
                    }
                    possibleFlightSchedules.add(searchResult);
                }
            }
        }

        return possibleFlightSchedules;
    }

    @Override
    public Fare getFlightScheduleFare(@NonNull FlightSchedule flightSchedule, @NonNull CabinClassType cabinClassType) throws InvalidEntityIdException {
        final FlightSchedule managedFlightSchedule = this.flightScheduleService.findById(flightSchedule.getFlightScheduleId());

        final AircraftConfiguration aircraftConfiguration = managedFlightSchedule.getFlight().getAircraftConfiguration();
        final CabinClassId cabinClassId = new CabinClassId();
        cabinClassId.setAircraftConfigurationId(aircraftConfiguration.getAircraftConfigurationId());
        cabinClassId.setCabinClassType(cabinClassType);
        final CabinClass cabinClass = this.cabinClassService.findById(cabinClassId);

        return this.fareService.findByScheduleAndCabinClass(managedFlightSchedule, cabinClass);
    }
}
