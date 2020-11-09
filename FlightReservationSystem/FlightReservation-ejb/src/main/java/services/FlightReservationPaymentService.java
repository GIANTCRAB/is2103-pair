package services;

import entities.Customer;
import entities.FlightReservation;
import entities.FlightReservationPayment;
import entities.Partner;
import lombok.NonNull;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless
public class FlightReservationPaymentService {
    @PersistenceContext(unitName = "frs")
    private EntityManager em;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FlightReservationPayment create(@NonNull FlightReservation flightReservation, @NonNull String creditCardNumber, Customer customer, Partner partner) {
        final FlightReservationPayment flightReservationPayment = new FlightReservationPayment();
        flightReservationPayment.setFlightReservation(flightReservation);
        flightReservationPayment.setCreditCardNumber(creditCardNumber);
        flightReservationPayment.setCustomer(customer);
        flightReservationPayment.setPartner(partner);
        this.em.persist(flightReservationPayment);
        this.em.flush();

        return flightReservationPayment;
    }

    public FlightReservationPayment create(@NonNull FlightReservation flightReservation, @NonNull String creditCardNumber, Customer customer) {
        return this.create(flightReservation, creditCardNumber, customer, null);
    }

    public FlightReservationPayment create(@NonNull FlightReservation flightReservation, @NonNull String creditCardNumber, Partner partner) {
        return this.create(flightReservation, creditCardNumber, null, partner);
    }
}
