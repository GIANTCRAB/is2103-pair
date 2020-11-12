package services;

import entities.Fare;
import exceptions.InvalidConstraintException;
import entities.FlightSchedule;
import entities.FlightSchedulePlan;
import entities.FlightSchedulePlanType;

import java.sql.Date;
import java.sql.Time;
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

import lombok.NonNull;

@LocalBean
@Stateless
public class FlightSchedulePlanService {
    @PersistenceContext(unitName = "frs")
    private EntityManager em;

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FlightSchedulePlan create(@NonNull FlightSchedulePlanType flightSchedulePlanType, List<FlightSchedule> flightSchedules) throws InvalidConstraintException {
        final FlightSchedulePlan flightSchedulePlan = new FlightSchedulePlan();
        flightSchedulePlan.setFlightSchedulePlanType(flightSchedulePlanType);
        flightSchedulePlan.setFlightSchedules(flightSchedules);

        Set<ConstraintViolation<FlightSchedulePlan>> violations = this.validator.validate(flightSchedulePlan);
        if (!violations.isEmpty()) {
            throw new InvalidConstraintException(violations.toString());
        }

        em.persist(flightSchedulePlan);
        flightSchedules.forEach(f -> {
            f.setFlightSchedulePlan(flightSchedulePlan);
            em.persist(f);
        });
        em.flush();

        return flightSchedulePlan;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FlightSchedulePlan associateWithFares(@NonNull FlightSchedulePlan flightSchedulePlan, List<Fare> fares) {
        flightSchedulePlan.setFares(fares);
        em.merge(flightSchedulePlan);
        em.flush();
        return flightSchedulePlan;
    }
    
    public void addFlightSchedules(FlightSchedulePlan flightSchedulePlan, List<FlightSchedule> flightSchedules) {
        
        flightSchedules.forEach(flightSchedule -> {
            flightSchedulePlan.getFlightSchedules().add(flightSchedule);
            flightSchedule.setFlightSchedulePlan(flightSchedulePlan);
            em.merge(flightSchedule);
        });
        
        em.merge(flightSchedulePlan);
        em.flush();
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FlightSchedulePlan getFlightSchedulePlanById(Long id) {
        FlightSchedulePlan flightSchedulePlan = em.find(FlightSchedulePlan.class, id);
        flightSchedulePlan.getFlightSchedules();
        flightSchedulePlan.getFares();
        flightSchedulePlan.getFares().forEach(fare -> fare.getCabinClass());
        
        flightSchedulePlan.getFlightSchedules().forEach(flightSchedule -> {
            flightSchedule.getFlight();
            flightSchedule.getFlight().getFlightRoute();
            flightSchedule.getFlight().getAircraftConfiguration();
            flightSchedule.getFlightReservations().size();
        });
        return flightSchedulePlan;
    }
    
    // Something wrong with this query
    public List<FlightSchedulePlan> getFlightSchedulePlans() {
        Query searchQuery = em.createQuery("SELECT fsp from FlightSchedulePlan fsp JOIN fsp.flightSchedules fs JOIN fs.flight f GROUP BY fsp.flightSchedulePlanId ORDER BY f.flightCode ASC, MIN(fs.date) DESC", FlightSchedulePlan.class);
        List<FlightSchedulePlan> flightSchedulePlans = searchQuery.getResultList();
        flightSchedulePlans.forEach(flightSchedulePlan -> {
            flightSchedulePlan.getFlightSchedules().size();
            flightSchedulePlan.getFlightSchedules().forEach(flightSchedule -> flightSchedule.getFlight());
        });
        return flightSchedulePlans;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteFlightSchedulePlan(FlightSchedulePlan flightSchedulePlan) {
        em.remove(flightSchedulePlan);
        em.flush();
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void disableFlightSchedulePlan(FlightSchedulePlan flightSchedulePlan) {
        FlightSchedulePlan managedFlightSchedulePlan = em.find(FlightSchedulePlan.class, flightSchedulePlan.getFlightSchedulePlanId());
        managedFlightSchedulePlan.setEnabled(false);
    }
}
