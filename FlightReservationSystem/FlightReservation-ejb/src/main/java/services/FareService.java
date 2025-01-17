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
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
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
        em.flush();

        final List<Fare> fares = cabinClass.getFares();
        fares.add(fare);
        cabinClass.setFares(fares);
        em.merge(cabinClass);
        final List<Fare> flightSchedulePlanFares = flightSchedulePlan.getFares();
        flightSchedulePlanFares.add(fare);
        flightSchedulePlan.setFares(flightSchedulePlanFares);
        em.merge(flightSchedulePlan);

        return fare;
    }

    /**
     * Used during search of flight
     *
     * @param flightSchedule
     * @param cabinClass
     * @return
     * @throws InvalidEntityIdException
     */
    public Fare findByScheduleAndCabinClass(@NonNull FlightSchedule flightSchedule, @NonNull CabinClass cabinClass, Boolean highestOnly) throws InvalidEntityIdException {
        final FlightSchedulePlan flightSchedulePlan = flightSchedule.getFlightSchedulePlan();

        final TypedQuery<Fare> query;
        if (highestOnly != null && highestOnly) {
            query = this.em.createQuery("SELECT f FROM Fare f WHERE f.cabinClass.cabinClassId = ?1 AND f.flightSchedulePlan.flightSchedulePlanId = ?2 ORDER BY f.fareAmount DESC", Fare.class)
                    .setParameter(1, cabinClass.getCabinClassId())
                    .setParameter(2, flightSchedulePlan.getFlightSchedulePlanId())
                    .setMaxResults(1);

        } else {
            query = this.em.createQuery("SELECT f FROM Fare f WHERE f.cabinClass.cabinClassId = ?1 AND f.flightSchedulePlan.flightSchedulePlanId = ?2 ORDER BY f.fareAmount ASC", Fare.class)
                    .setParameter(1, cabinClass.getCabinClassId())
                    .setParameter(2, flightSchedulePlan.getFlightSchedulePlanId())
                    .setMaxResults(1);

        }
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new InvalidEntityIdException("Fare does not exists.");
        }
    }

    public Fare findByScheduleAndCabinClass(@NonNull FlightSchedule flightSchedule, @NonNull CabinClass cabinClass) throws InvalidEntityIdException {
        return this.findByScheduleAndCabinClass(flightSchedule, cabinClass, null);
    }

    /**
     * Used during reservation of a flight
     *
     * @param flightReservation
     * @return
     * @throws InvalidEntityIdException
     */
    public Fare findByFlightReservation(@NonNull FlightReservation flightReservation) throws InvalidEntityIdException {
        final FlightReservationPayment flightReservationPayment = flightReservation.getFlightReservationPayment();
        if (flightReservationPayment == null) {
            throw new InvalidEntityIdException("Invalid flight reservation payment in flight reservation");
        }

        Fare fare;

        if (flightReservationPayment.getCustomer() != null) {
            // Get lowest fare
            fare = this.em.createQuery("SELECT f FROM Fare f WHERE f.cabinClass.cabinClassId.cabinClassType = ?1 AND f.flightSchedulePlan.flightSchedulePlanId = ?2 ORDER BY f.fareAmount ASC", Fare.class)
                    .setParameter(1, flightReservation.getCabinClassType())
                    .setParameter(2, flightReservation.getFlightSchedule().getFlightSchedulePlan().getFlightSchedulePlanId())
                    .setMaxResults(1)
                    .getSingleResult();
        } else {
            // Get the highest fare
            fare = this.em.createQuery("SELECT f FROM Fare f WHERE f.cabinClass.cabinClassId.cabinClassType = ?1 AND f.flightSchedulePlan.flightSchedulePlanId = ?2 ORDER BY f.fareAmount DESC", Fare.class)
                    .setParameter(1, flightReservation.getCabinClassType())
                    .setParameter(2, flightReservation.getFlightSchedule().getFlightSchedulePlan().getFlightSchedulePlanId())
                    .setMaxResults(1)
                    .getSingleResult();
        }

        return fare;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(Fare fare) {
        Fare managedFare = em.find(Fare.class, fare.getFareId());
        CabinClass cabinClass = em.find(CabinClass.class, fare.getCabinClass().getCabinClassId());
        cabinClass.getFares().remove(managedFare);
        
        em.remove(managedFare);
        em.flush();
    }

    public void updateFares(List<Fare> fares) {
        for (Fare fare:fares) {
            Fare managedFare = em.find(Fare.class, fare.getFareId());
            managedFare.setFareAmount(fare.getFareAmount());
        }
    }
}
