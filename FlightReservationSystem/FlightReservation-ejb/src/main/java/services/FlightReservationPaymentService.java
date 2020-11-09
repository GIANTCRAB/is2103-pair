package services;

import entities.*;
import lombok.NonNull;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@LocalBean
@Stateless
public class FlightReservationPaymentService {
    @PersistenceContext(unitName = "frs")
    private EntityManager em;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FlightReservationPayment create(@NonNull String creditCardNumber, Customer customer, Partner partner) {
        final FlightReservationPayment flightReservationPayment = new FlightReservationPayment();
        flightReservationPayment.setCreditCardNumber(creditCardNumber);
        flightReservationPayment.setCustomer(customer);
        flightReservationPayment.setPartner(partner);
        this.em.persist(flightReservationPayment);
        this.em.flush();

        return flightReservationPayment;
    }

    public FlightReservationPayment create(@NonNull String creditCardNumber, Customer customer) {
        return this.create(creditCardNumber, customer, null);
    }

    public FlightReservationPayment create(@NonNull String creditCardNumber, Partner partner) {
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
