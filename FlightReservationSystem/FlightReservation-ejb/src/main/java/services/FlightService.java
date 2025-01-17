package services;

import entities.*;
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
import javax.persistence.NoResultException;
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

    public Flight findById(Long id) throws InvalidEntityIdException {
        final Flight flight = this.em.find(Flight.class, id);

        if (flight == null) {
            throw new InvalidEntityIdException("Flight could not be found!");
        }

        return flight;
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
        TypedQuery<Flight> query = this.em.createQuery("SELECT f FROM Flight f WHERE f.flightCode = :inFlightCode", Flight.class)
                .setParameter("inFlightCode", flightCode);
        try {
            Flight flight = query.getSingleResult();
            flight.getFlightRoute();
            flight.getAircraftConfiguration();
            flight.getAircraftConfiguration().getCabinClasses().size();
            return flight;
        } catch (NoResultException e) {
            return null;
        }
    }


    public Flight getFlightByOriginDest(String origin, String destination) {
        TypedQuery<Flight> searchQuery = em.createQuery("SELECT f FROM Flight f WHERE f.flightRoute.flightRouteId.originId =?1 AND f.flightRoute.flightRouteId.destId =?2", Flight.class)
                .setParameter(1, origin)
                .setParameter(2, destination);
        try {
            return searchQuery.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Flight getFlightByOriginDestAndAircraftConfiguration(String origin, String destination, Long aircraftConfigurationId) {
        TypedQuery<Flight> searchQuery = em.createQuery("SELECT f FROM Flight f WHERE f.flightRoute.flightRouteId.originId =?1 AND f.flightRoute.flightRouteId.destId =?2"
                + " AND f.aircraftConfiguration.aircraftConfigurationId =?3", Flight.class)
                .setParameter(1, origin)
                .setParameter(2, destination)
                .setParameter(3, aircraftConfigurationId)
                .setMaxResults(1);
        try {
            return searchQuery.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Flight> getFlightByOriginDest(Airport origin, Airport destination) throws NoResultException {
        TypedQuery<Flight> searchQuery = em.createQuery("SELECT f FROM Flight f WHERE f.flightRoute.flightRouteId.originId =?1 AND f.flightRoute.flightRouteId.destId =?2", Flight.class)
                .setParameter(1, origin.getIataCode())
                .setParameter(2, destination.getIataCode());
        return searchQuery.getResultList();
    }

    public Set<List<Flight>> getPossibleFlights(@NonNull Airport origin, @NonNull Airport destination) {
        final Set<Flight> markedRoutes = new HashSet<>();
        final Set<List<Flight>> correctFlightPaths = new HashSet<>();

        final List<Flight> startingRoutes = this.getFlightsForOrigin(origin);
        Set<List<Flight>> routePaths = new HashSet<>();
        for (Flight startingFlight : startingRoutes) {
            final List<Flight> routePath = new LinkedList<>();
            routePath.add(startingFlight);
            markedRoutes.add(startingFlight); // mark starting route as visited as well
            routePaths.add(routePath);
        }

        for (int i = 0; i < 2; i++) {
            for (List<Flight> flightPath : routePaths) {
                final Set<List<Flight>> deepUnmarkedRoutes = this.getUnmarkedRoutes(markedRoutes, flightPath);
                routePaths = Stream.of(routePaths, deepUnmarkedRoutes)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet());
            }
        }

        // Now filter all routes and make sure they will eventually lead to the destination
        for (List<Flight> flightPath : routePaths) {
            final List<Flight> trueFlightPath = new LinkedList<>();
            for (Flight flightForPath : flightPath) {
                trueFlightPath.add(flightForPath);
                if (flightForPath.getFlightRoute().getDest().equals(destination)) {
                    correctFlightPaths.add(trueFlightPath);
                    break;
                }
            }
        }

        return correctFlightPaths;
    }

    public Set<List<Flight>> getReturnFlights(@NonNull Flight flight) {
        final Airport returnDestinationAirport = flight.getFlightRoute().getOrigin();
        final Airport returnOriginAirport = flight.getFlightRoute().getDest();

        return this.getPossibleFlights(returnOriginAirport, returnDestinationAirport);
    }

    private Set<List<Flight>> getUnmarkedRoutes(Set<Flight> markedRoutes, List<Flight> existingFlightRoute) {
        // Retrieve last flight node's flight route destination as the starting point
        final Airport origin = existingFlightRoute.get(existingFlightRoute.size() - 1).getFlightRoute().getDest();
        final Set<List<Flight>> allFlightRoutes = new HashSet<>();
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

        final List<Flight> flightList = flightTypedQuery.getResultList();
        // Force entities to be loaded
        flightList.forEach(flight -> {
            flight.getFlightRoute().getOrigin().getIataCode();
            flight.getFlightRoute().getDest().getIataCode();
        });

        return flightList;
    }

    public void updateFlightRoute(Flight flight, FlightRoute flightRoute) {
        FlightRoute oldFlightRoute = this.em.find(FlightRoute.class, flight.getFlightRoute().getFlightRouteId());
        FlightRoute newFlightRoute = this.em.find(FlightRoute.class, flightRoute.getFlightRouteId());

        flight.setFlightRoute(flightRoute);
        em.merge(flight);
        oldFlightRoute.getFlights().remove(flight);
        newFlightRoute.getFlights().add(flight);
        em.flush();
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
        AircraftConfiguration aircraftConfiguration = em.find(AircraftConfiguration.class, flight.getAircraftConfiguration().getAircraftConfigurationId());
        FlightRoute flightRoute = em.find(FlightRoute.class, flight.getFlightRoute().getFlightRouteId());

        aircraftConfiguration.getFlights().remove(flight);
        flightRoute.getFlights().remove(flight);
        em.remove(flight);
        em.flush();
    }

    public void disable(Flight flight) {
        Flight managedFlight = em.find(Flight.class, flight.getFlightId());
        flight.setEnabled(false);
    }
}
