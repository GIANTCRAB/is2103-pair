package services;

import entities.Flight;
import entities.FlightRoute;
import entities.AircraftConfiguration;
import exceptions.InvalidConstraintException;
import java.util.List;
import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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
    
    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Flight create(String flightCode, FlightRoute flightRoute, AircraftConfiguration aircraftConfiguration) throws InvalidConstraintException {
        Flight flight = new Flight();
        flight.setFlightCode(flightCode);
        flight.setFlightRoute(flightRoute);
        flight.setAircraftConfiguration(aircraftConfiguration);
        
        Set<ConstraintViolation<FlightRoute>> violations = this.validator.validate(flight);
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
        returnFlight.setMainFlightRoute(mainFlight);
    }
    
    public List<FlightRoute> getFlights() {
        TypedQuery<Flight> searchQuery = this.em.createQuery("SELECT f FROM Flight f WHERE f.returnFlight IS NOT NULL ORDER BY f.flightCode", FlightRoute.class);
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
        
        return (Flight)query.getSingleResult();        
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
