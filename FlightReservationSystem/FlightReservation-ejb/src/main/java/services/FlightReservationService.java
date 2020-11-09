package services;

import entities.*;
import lombok.NonNull;
import pojo.Passenger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
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
        TypedQuery<FlightReservation> query = this.em.createQuery("SELECT fr FROM FlightReservation fr WHERE fr.customerPayment.customer.customerId = ?1", FlightReservation.class)
                .setParameter(1, customer.getCustomerId());

        final List<FlightReservation> flightReservations = query.getResultList();

        // Load the flight schedule, flight, route and airport
        flightReservations.forEach(flightReservation -> {
            flightReservation.getFare().getFlightSchedule().getFlight().getFlightRoute().getOrigin().getIataCode();
            flightReservation.getFare().getFlightSchedule().getFlight().getFlightRoute().getDest().getIataCode();
        });

        return flightReservations;
    }
}
