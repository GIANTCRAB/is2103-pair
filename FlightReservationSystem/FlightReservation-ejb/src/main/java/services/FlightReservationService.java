package services;

import entities.*;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import lombok.NonNull;
import pojo.Passenger;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@LocalBean
@Stateless
public class FlightReservationService {
    @PersistenceContext(unitName = "frs")
    EntityManager em;
    @Inject
    CabinClassService cabinClassService;

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FlightReservation create(@NonNull FlightSchedule flightSchedule, @NonNull Passenger passenger, FlightReservationPayment flightReservationPayment) throws InvalidConstraintException {
        final FlightReservation flightReservation = new FlightReservation();
        flightReservation.setFlightSchedule(flightSchedule);
        flightReservation.setPassengerFirstName(passenger.getFirstName());
        flightReservation.setPassengerLastName(passenger.getLastName());
        flightReservation.setPassengerPassportNo(passenger.getPassportNumber());
        flightReservation.setSeatNumber(passenger.getSeatNumber());
        flightReservation.setFlightReservationPayment(flightReservationPayment);
        // TODO: test if this works
        final Fare fare = this.getFlightReservationFare(flightReservation);
        flightReservation.setReservationCost(fare.getFareAmount());
        Set<ConstraintViolation<FlightReservation>> violations = this.validator.validate(flightReservation);
        // There are invalid data
        if (!violations.isEmpty()) {
            throw new InvalidConstraintException(violations.toString());
        }
        this.em.persist(flightReservation);
        this.em.flush();

        final List<FlightReservation> flightReservationList = flightSchedule.getFlightReservations();
        flightReservationList.add(flightReservation);
        flightSchedule.setFlightReservations(flightReservationList);
        this.em.persist(flightSchedule);

        return flightReservation;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FlightReservation create(@NonNull  FlightSchedule flightSchedule, @NonNull Passenger passenger) throws InvalidConstraintException {
        return this.create(flightSchedule, passenger, null);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<FlightReservation> create(@NonNull  FlightSchedule flightSchedule, @NonNull List<Passenger> passengers) throws InvalidConstraintException {
        final List<FlightReservation> flightReservations = new ArrayList<>();
        for (Passenger passenger : passengers) {
            flightReservations.add(this.create(flightSchedule, passenger));
        }
        return flightReservations;
    }

    /**
     * Retrieve Flight Reservations and order them by the cabinClassType
     *
     * @param flightSchedule
     * @return
     */
    public List<FlightReservation> getFlightReservations(@NonNull FlightSchedule flightSchedule) {
        TypedQuery<FlightReservation> query = this.em.createQuery("SELECT fr FROM FlightReservation fr WHERE fr.flightSchedule.flightScheduleId = ?1 ORDER BY fr.cabinClassType", FlightReservation.class)
                .setParameter(1, flightSchedule.getFlightScheduleId());

        return query.getResultList();
    }

    /**
     * Retrieve flight reservation information about a specific customer
     *
     * @param customer
     * @return
     */
    public List<FlightReservation> getFlightReservations(@NonNull Customer customer) {
        TypedQuery<FlightReservation> query = this.em.createQuery("SELECT fr FROM FlightReservation fr WHERE fr.flightReservationPayment.customer IS NOT NULL and fr.flightReservationPayment.customer.customerId = ?1", FlightReservation.class)
                .setParameter(1, customer.getCustomerId());

        return this.loadFlightReservationsRelationships(query.getResultList());
    }

    /**
     * Retrieve flight reservation information about a specific partner
     *
     * @param partner
     * @return
     */
    public List<FlightReservation> getFlightReservations(@NonNull Partner partner) {
        TypedQuery<FlightReservation> query = this.em.createQuery("SELECT fr FROM FlightReservation fr WHERE fr.flightReservationPayment.partner IS NOT NULL and fr.flightReservationPayment.partner.partnerId = ?1", FlightReservation.class)
                .setParameter(1, partner.getPartnerId());

        return this.loadFlightReservationsRelationships(query.getResultList());
    }

    // Load the flight schedule, flight, route and airport
    private List<FlightReservation> loadFlightReservationsRelationships(List<FlightReservation> flightReservations) {
        flightReservations.forEach(flightReservation -> {
            flightReservation.getFlightSchedule().getFlightSchedulePlan();
            flightReservation.getFlightSchedule().getFlight().getFlightRoute().getOrigin().getIataCode();
            flightReservation.getFlightSchedule().getFlight().getFlightRoute().getDest().getIataCode();
        });

        return flightReservations;
    }

    //TODO: test this
    public Fare getFlightReservationFare(@NonNull FlightReservation flightReservation){
        return this.em.createQuery("SELECT f FROM Fare f WHERE f.cabinClass.cabinClassId.cabinClassType = ?1 AND f.flightSchedulePlan.flightSchedulePlanId = ?2", Fare.class)
                .setParameter(1, flightReservation.getCabinClassType())
                .setParameter(2, flightReservation.getFlightSchedule().getFlightSchedulePlan().getFlightSchedulePlanId()).getSingleResult();
    }
}
