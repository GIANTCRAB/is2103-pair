package services;

import entities.*;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import lombok.NonNull;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;

@LocalBean
@Stateless
public class FlightReservationPaymentService {
    @PersistenceContext(unitName = "frs")
    private EntityManager em;

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    public FlightReservationPayment findById(Long id) throws InvalidEntityIdException {
        final FlightReservationPayment flightReservationPayment = this.em.find(FlightReservationPayment.class, id);

        if (flightReservationPayment == null) {
            throw new InvalidEntityIdException();
        }

        return flightReservationPayment;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FlightReservationPayment create(@NonNull String creditCardNumber, Customer customer, Partner partner) throws InvalidConstraintException {
        final FlightReservationPayment flightReservationPayment = new FlightReservationPayment();
        flightReservationPayment.setCreditCardNumber(creditCardNumber);
        flightReservationPayment.setCustomer(customer);
        flightReservationPayment.setPartner(partner);
        Set<ConstraintViolation<FlightReservationPayment>> violations = this.validator.validate(flightReservationPayment);
        // There are invalid data
        if (!violations.isEmpty()) {
            throw new InvalidConstraintException(violations.toString());
        }
        this.em.persist(flightReservationPayment);
        this.em.flush();

        return flightReservationPayment;
    }

    public FlightReservationPayment create(@NonNull String creditCardNumber, Customer customer) throws InvalidConstraintException {
        return this.create(creditCardNumber, customer, null);
    }

    public FlightReservationPayment create(@NonNull String creditCardNumber, Partner partner) throws InvalidConstraintException {
        return this.create(creditCardNumber, null, partner);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void associateFlightReservations(List<FlightReservation> flightReservations, FlightReservationPayment flightReservationPayment) {
        final List<FlightReservation> existingFlightReservations = flightReservationPayment.getFlightReservations();
        flightReservations.forEach(flightReservation -> {
            flightReservation.setFlightReservationPayment(flightReservationPayment);
            this.em.persist(flightReservation);
            existingFlightReservations.add(flightReservation);
        });
        this.em.persist(flightReservationPayment);
    }
}
