package services;

import entities.Flight;
import entities.FlightRoute;
import entities.AircraftConfiguration;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;

import java.util.List;
import java.util.Set;

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

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void associateReturnFlight(Flight mainFlight, Flight returnFlight) {
        mainFlight.setReturnFlight(returnFlight);
        returnFlight.setMainFlight(mainFlight);
    }

    public List<Flight> getFlights() {
        TypedQuery<Flight> searchQuery = this.em.createQuery("SELECT f FROM Flight f WHERE f.returnFlight IS NOT NULL ORDER BY f.flightCode", Flight.class);
        List<Flight> flights = searchQuery.getResultList();

        flights.forEach(flight -> {
            flight.getFlightRoute();
            flight.getAircraftConfiguration();
        });

        return flights;
    }

    public Flight getFlightByFlightCode(String flightCode) {
        Query query = this.em.createQuery("SELECT f FROM Flight f WHERE f.flightCode :=inFlightCode")
                .setParameter("inFlightCode", flightCode);
        Flight flight = (Flight) query.getSingleResult();
        flight.getFlightRoute();
        flight.getAircraftConfiguration();
        flight.getAircraftConfiguration().getCabinClasses().size();

        return flight;
    }

    // Not done yet
    public void update(Flight flight) {
        this.em.merge(flight);
        this.em.flush();
    }

    // Do this after FlightSchedule is settled
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(Flight flight) {
        this.em.remove(flight);
    }
}
