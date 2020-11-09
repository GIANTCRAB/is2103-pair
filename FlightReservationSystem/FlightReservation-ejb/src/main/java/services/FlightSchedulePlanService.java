package services;

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
    public FlightSchedulePlan create(@NonNull FlightSchedulePlanType flightSchedulePlanType) throws InvalidConstraintException {
        final FlightSchedulePlan flightSchedulePlan = new FlightSchedulePlan();
        flightSchedulePlan.setFlightSchedulePlanType(flightSchedulePlanType);

        Set<ConstraintViolation<FlightSchedulePlan>> violations = this.validator.validate(flightSchedulePlan);
        if (!violations.isEmpty()) {
            throw new InvalidConstraintException(violations.toString());
        }

        em.persist(flightSchedulePlan);
        em.flush();

        return flightSchedulePlan;
    }
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void associateFlightSchedules(FlightSchedulePlan flightSchedulePlan, List<FlightSchedule> flightSchedules) {
        flightSchedulePlan.setFlightSchedules(flightSchedules);
        flightSchedules.forEach(f -> f.setFlightSchedulePlan(flightSchedulePlan));
    }
    
    public List<FlightSchedulePlan> getFlightSchedulePlans() {
        TypedQuery<FlightSchedulePlan> searchQuery = em.createQuery("SELECT fsp FROM FlightSchedule fsp JOIN fsp.flightSchedules fs ORDER BY fs.flight.flightCode ASC, fs.flight.date DESC", FlightSchedulePlan.class);
        List<FlightSchedulePlan> flightSchedulePlans = searchQuery.getResultList();
        flightSchedulePlans.forEach(f -> f.getFlightSchedule().size());
        return searchQuery.getResultList();
    }
}
