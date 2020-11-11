package services;

import entities.*;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import java.util.List;
import java.util.Set;
import java.math.BigDecimal;
import lombok.NonNull;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@LocalBean
@Stateless
public class FareService {
    @PersistenceContext(unitName = "frs")
    private EntityManager em;
    
    @Inject
    CabinClassService cabinClassService;
    
    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();
    
    public Fare create(String fareBasisCode, BigDecimal fareAmount, CabinClass cabinClass, FlightSchedulePlan flightSchedulePlan) throws InvalidConstraintException {
        Fare fare = new Fare();
        fare.setFareBasisCode(fareBasisCode);
        fare.setFareAmount(fareAmount);
        fare.setCabinClass(cabinClass);
        fare.setFlightSchedulePlan(flightSchedulePlan);
        
        Set<ConstraintViolation<Fare>> violations = this.validator.validate(fare);
        if (!violations.isEmpty()) {
            throw new InvalidConstraintException(violations.toString());
        }
        
        em.persist(fare);
        
        CabinClass managedCabinClass = em.find(CabinClass.class, cabinClass.getCabinClassId());
        managedCabinClass.getFares().add(fare);
        FlightSchedulePlan managedFlightSchedulePlan = em.find(FlightSchedulePlan.class, flightSchedulePlan.getFlightSchedulePlanId());
        managedFlightSchedulePlan.getFares().add(fare);
  
        return fare;
    }
    
    public Fare findByScheduleAndCabinClass(@NonNull FlightSchedule flightSchedule, @NonNull CabinClassType cabinClassType) throws InvalidEntityIdException {
        final AircraftConfiguration aircraftConfiguration = flightSchedule.getFlight().getAircraftConfiguration();
        final CabinClassId cabinClassId = new CabinClassId();
        cabinClassId.setAircraftConfigurationId(aircraftConfiguration.getAircraftConfigurationId());
        cabinClassId.setCabinClassType(cabinClassType);
        final CabinClass cabinClass = cabinClassService.findById(cabinClassId);
        final FlightSchedulePlan flightSchedulePlan = flightSchedule.getFlightSchedulePlan();

        TypedQuery<Fare> query = this.em.createQuery("SELECT f FROM Fare f WHERE f.cabinClass.cabinClassId = ?1 AND f.flightSchedulePlan.flightSchedulePlanId = ?2", Fare.class)
                .setParameter(1, cabinClass.getCabinClassId())
                .setParameter(2, flightSchedulePlan.getFlightSchedulePlanId());

        return query.getSingleResult();
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(Fare fare) {
        Fare managedFare = em.find(Fare.class, fare.getFareId());
        CabinClass managedCabinClass = em.find(CabinClass.class, fare.getCabinClass().getCabinClassId());
        
        managedCabinClass.getFares().remove(managedFare);
        em.remove(managedFare);
        em.flush();
    }
    
    public void updateFares(List<Fare> fares) {
        for (Fare fare:fares) {
            em.merge(fare);
        }
        em.flush();
    }
}
