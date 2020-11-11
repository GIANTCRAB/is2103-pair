package services;

import entities.Airport;
import entities.Flight;
import entities.FlightRoute;
import entities.AircraftConfiguration;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@LocalBean
@Stateless
public class FlightService {
    @PersistenceContext(unitName = "frs")
    private EntityManager em;
    @Inject
    FlightRouteService flightRouteService;

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    public Flight findById(Long id) {
        return this.em.find(Flight.class, id);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Flight create(String flightCode, FlightRoute flightRoute, AircraftConfiguration aircraftConfiguration) throws InvalidConstraintException, InvalidEntityIdException {
        // Some flight routes are disabled, can't add new flights but they still exists
        if (!this.flightRouteService.canAddFlights(flightRoute)) {
            throw new InvalidEntityIdException();
        }

        Flight flight = new Flight();
        flight.setFlightCode(flightCode);
        flight.setFlightRoute(flightRoute);
        flight.setAircraftConfiguration(aircraftConfiguration);

        Set<ConstraintViolation<Flight>> violations = this.validator.validate(flight);
        if (!violations.isEmpty()) {
            throw new InvalidConstraintException(violations.toString());
        }

        em.persist(flight);

        flightRoute.getFlights().add(flight);
        aircraftConfiguration.getFlights().add(flight);

        em.flush();

        return flight;
    }

    public List<Flight> getFlights() {
        TypedQuery<Flight> searchQuery = this.em.createQuery("SELECT f FROM Flight f ORDER BY f.flightCode", Flight.class);
        List<Flight> flights = searchQuery.getResultList();

        flights.forEach(flight -> {
            flight.getFlightRoute();
            flight.getAircraftConfiguration();
        });

        return flights;
    }

    public Flight getFlightByFlightCode(String flightCode) {
        Query query = this.em.createQuery("SELECT f FROM Flight f WHERE f.flightCode = :inFlightCode")
                .setParameter("inFlightCode", flightCode);
        Flight flight = (Flight) query.getSingleResult();
        flight.getFlightRoute();
        flight.getAircraftConfiguration();
        flight.getAircraftConfiguration().getCabinClasses().size();

        return flight;
    }

    public Set<List<Flight>> getReturnFlights(@NonNull Flight flight) {
        final Airport returnDestinationAirport = flight.getFlightRoute().getOrigin();
        final Airport returnOriginAirport = flight.getFlightRoute().getDest();

        final HashSet<Flight> markedRoutes = new HashSet<>();
        final Set<List<Flight>> correctFlightPaths = new HashSet<>();

        final List<Flight> startingRoutes = this.getFlightsForOrigin(returnOriginAirport);
        List<List<Flight>> routePaths = new ArrayList<>();
        for (Flight startingFlight : startingRoutes) {
            final List<Flight> routePath = new LinkedList<>();
            routePath.add(startingFlight);
            markedRoutes.add(startingFlight); // mark starting route as visited as well
            routePaths.add(routePath);
        }

        for (int i = 0; i < 2; i++) {
            for (List<Flight> flightPath : routePaths) {
                final List<List<Flight>> deepUnmarkedRoutes = this.getUnmarkedRoutes(markedRoutes, flightPath);
                routePaths = Stream.of(routePaths, deepUnmarkedRoutes)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
            }
        }

        // Now filter all routes and make sure they will eventually lead to the destination
        for (List<Flight> flightPath : routePaths) {
            final List<Flight> trueFlightPath = new LinkedList<>();
            for (Flight flightForPath : flightPath) {
                trueFlightPath.add(flightForPath);
                if (flightForPath.getFlightRoute().getDest().equals(returnDestinationAirport)) {
                    correctFlightPaths.add(trueFlightPath);
                    break;
                }
            }
        }

        return correctFlightPaths;
    }

    private List<List<Flight>> getUnmarkedRoutes(HashSet<Flight> markedRoutes, List<Flight> existingFlightRoute) {
        // Retrieve last flight node's flight route destination as the starting point
        final Airport origin = existingFlightRoute.get(existingFlightRoute.size() - 1).getFlightRoute().getDest();
        final List<List<Flight>> allFlightRoutes = new ArrayList<>();
        final List<Flight> flightRoutes = this.getFlightsForOrigin(origin);

        for (Flight flightRoute : flightRoutes) {
            if (!markedRoutes.contains(flightRoute)) {
                // mark the route as visited
                markedRoutes.add(flightRoute);
                final List<Flight> existingFlightRouteCopy = new LinkedList<>(existingFlightRoute);
                existingFlightRouteCopy.add(flightRoute);
                allFlightRoutes.add(existingFlightRouteCopy);
            }
        }

        return allFlightRoutes;
    }

    public List<Flight> getFlightsForOrigin(@NonNull Airport origin) {
        TypedQuery<Flight> flightTypedQuery = this.em.createQuery("SELECT f FROM Flight f WHERE f.flightRoute.origin.iataCode = ?1", Flight.class)
                .setParameter(1, origin.getIataCode());
        /**
         final List<Flight> flightList = new ArrayList<>();
         origin.getOriginFlightRoutes().forEach(flightRoute -> flightList.addAll(flightRoute.getFlights()));

         return flightList;**/

        final List<Flight> flightList = flightTypedQuery.getResultList();
        // Force entities to be loaded
        flightList.forEach(flight -> {
            flight.getFlightRoute().getOrigin().getIataCode();
            flight.getFlightRoute().getDest().getIataCode();
        });

        return flightList;
    }

    public void updateFlightRoute(String flightCode, FlightRoute flightRoute) {
        Flight flight = this.getFlightByFlightCode(flightCode);
        FlightRoute oldFlightRoute = this.em.find(FlightRoute.class, flight.getFlightRoute().getFlightRouteId());
        FlightRoute newFlightRoute = this.em.find(FlightRoute.class, flightRoute.getFlightRouteId());

        flight.setFlightRoute(flightRoute);
        oldFlightRoute.getFlights().remove(flight);
        newFlightRoute.getFlights().add(flight);
    }

    public void updateAircraftConfiguration(String flightCode, AircraftConfiguration aircraftConfiguration) {
        Flight flight = this.getFlightByFlightCode(flightCode);
        AircraftConfiguration oldAircraftConfiguration = this.em.find(AircraftConfiguration.class, flight.getAircraftConfiguration().getAircraftConfigurationId());
        AircraftConfiguration newAircraftConfiguration = this.em.find(AircraftConfiguration.class, aircraftConfiguration.getAircraftConfigurationId());

        flight.setAircraftConfiguration(newAircraftConfiguration);
        oldAircraftConfiguration.getFlights().remove(flight);
        newAircraftConfiguration.getFlights().add(flight);
    }

    public void delete(Flight flight) {
        this.em.remove(flight);
    }
    
    public void disable(Flight flight) {
        Flight managedFlight = this.em.find(Flight.class, flight.getFlightId());
        flight.setEnabled(false);
    }
}
