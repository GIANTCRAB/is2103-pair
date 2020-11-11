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
        em.persist(flightSchedulePlan);
        return flightSchedulePlan;
    }

    public FlightSchedulePlan getFlightSchedulePlanById(Long id) {
        FlightSchedulePlan flightSchedulePlan = em.find(FlightSchedulePlan.class, id);
        flightSchedulePlan.getFlightSchedules();
        flightSchedulePlan.getFlightSchedules().forEach(f -> f.getFlight());
        return flightSchedulePlan;
    }

    public List<FlightSchedulePlan> getFlightSchedulePlans() {
        TypedQuery<FlightSchedulePlan> searchQuery = em.createQuery("SELECT fsp FROM FlightSchedule fsp JOIN fsp.flightSchedules fs ORDER BY fs.flight.flightCode ASC, fs.flight.date DESC", FlightSchedulePlan.class);
        List<FlightSchedulePlan> flightSchedulePlans = searchQuery.getResultList();
        flightSchedulePlans.forEach(f -> f.getFlightSchedules().size());
        return searchQuery.getResultList();
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteFlightSchedulePlan(FlightSchedulePlan flightSchedulePlan) {
        flightSchedulePlan.getFlightSchedules().clear();
        em.remove(flightSchedulePlan);
        em.flush();
    }
    
    public void disableFlightSchedulePlan(FlightSchedulePlan flightSchedulePlan) {
        FlightSchedulePlan managedFlightSchedulePlan = em.find(FlightSchedulePlan.class, flightSchedulePlan.getFlightSchedulePlanId());
        managedFlightSchedulePlan.setEnabled(false);
    }
}
