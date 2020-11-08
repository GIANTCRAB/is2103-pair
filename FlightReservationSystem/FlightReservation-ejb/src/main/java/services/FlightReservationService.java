package services;

import entities.CustomerPayment;
import entities.Fare;
import entities.FlightReservation;
import lombok.NonNull;
import pojo.Passenger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@LocalBean
@Stateless
public class FlightReservationService {
    @PersistenceContext(unitName = "frs")
    private EntityManager em;

    public FlightReservation create(@NonNull Fare fare, @NonNull Passenger passenger, CustomerPayment customerPayment) {
        final FlightReservation flightReservation = new FlightReservation();
        flightReservation.setFare(fare);
        flightReservation.setPassengerFirstName(passenger.getFirstName());
        flightReservation.setPassengerLastName(passenger.getLastName());
        flightReservation.setPassengerPassportNo(passenger.getPassportNumber());
        flightReservation.setSeatNumber(passenger.getSeatNumber());
        flightReservation.setCustomerPayment(customerPayment);
        this.em.persist(flightReservation);
        this.em.flush();

        final List<FlightReservation> flightReservationList = fare.getFlightReservations();
        flightReservationList.add(flightReservation);
        fare.setFlightReservations(flightReservationList);
        this.em.persist(fare);

        return flightReservation;
    }

    public FlightReservation create(@NonNull Fare fare, @NonNull Passenger passenger) {
        return this.create(fare, passenger, null);
    }
}
