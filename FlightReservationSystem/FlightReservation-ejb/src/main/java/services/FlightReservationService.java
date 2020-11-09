package services;

import entities.*;
import exceptions.InvalidConstraintException;
import lombok.NonNull;
import pojo.Passenger;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@LocalBean
@Stateless
public class FlightReservationService {
    @PersistenceContext(unitName = "frs")
    private EntityManager em;

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FlightReservation create(@NonNull Fare fare, @NonNull Passenger passenger, FlightReservationPayment flightReservationPayment) throws InvalidConstraintException {
        final FlightReservation flightReservation = new FlightReservation();
        flightReservation.setFare(fare);
        flightReservation.setPassengerFirstName(passenger.getFirstName());
        flightReservation.setPassengerLastName(passenger.getLastName());
        flightReservation.setPassengerPassportNo(passenger.getPassportNumber());
        flightReservation.setSeatNumber(passenger.getSeatNumber());
        flightReservation.setFlightReservationPayment(flightReservationPayment);
        Set<ConstraintViolation<FlightReservation>> violations = this.validator.validate(flightReservation);
        // There are invalid data
        if (!violations.isEmpty()) {
            throw new InvalidConstraintException(violations.toString());
        }
        this.em.persist(flightReservation);
        this.em.flush();

        final List<FlightReservation> flightReservationList = fare.getFlightReservations();
        flightReservationList.add(flightReservation);
        fare.setFlightReservations(flightReservationList);
        this.em.persist(fare);

        return flightReservation;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FlightReservation create(@NonNull Fare fare, @NonNull Passenger passenger) throws InvalidConstraintException {
        return this.create(fare, passenger, null);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<FlightReservation> create(@NonNull Fare fare, @NonNull List<Passenger> passengers) throws InvalidConstraintException {
        final List<FlightReservation> flightReservations = new ArrayList<>();
        for (Passenger passenger : passengers) {
            flightReservations.add(this.create(fare, passenger));
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
        TypedQuery<FlightReservation> query = this.em.createQuery("SELECT fr FROM FlightReservation fr WHERE fr.fare.flightSchedule.flightScheduleId = ?1 ORDER BY fr.fare.cabinClass.cabinClassId.cabinClassType", FlightReservation.class)
                .setParameter(1, flightSchedule.getFlightScheduleId());

        final List<FlightReservation> flightReservations = query.getResultList();

        // Load fare data and cabin class
        flightReservations.forEach(flightReservation -> {
            flightReservation.getFare().getFareBasisCode();
            flightReservation.getFare().getCabinClass().getCabinClassId().getCabinClassType();
        });

        return flightReservations;
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
            flightReservation.getFare().getFlightSchedule().getFlight().getFlightRoute().getOrigin().getIataCode();
            flightReservation.getFare().getFlightSchedule().getFlight().getFlightRoute().getDest().getIataCode();
        });

        return flightReservations;
    }
}
