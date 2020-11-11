package services;

import entities.*;
import exceptions.InvalidConstraintException;
import lombok.NonNull;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

@LocalBean
@Stateless
public class FlightScheduleService {
    @PersistenceContext(unitName = "frs")
    private EntityManager em;

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FlightSchedule create(@NonNull Flight flight, @NonNull Date departureDate, @NonNull Time departureTime, @NonNull Long estimatedDuration) throws InvalidConstraintException {
        final FlightSchedule flightSchedule = new FlightSchedule();
        flightSchedule.setFlight(flight);
        flightSchedule.setDepartureDateTime(departureDate, departureTime);
        flightSchedule.setEstimatedDuration(estimatedDuration);

        Set<ConstraintViolation<FlightSchedule>> violations = this.validator.validate(flightSchedule);
        if (!violations.isEmpty()) {
            throw new InvalidConstraintException(violations.toString());
        }

        em.persist(flightSchedule);
        em.flush();

        return flightSchedule;
    }

    public List<FlightSchedule> getFlightSchedules() {
        TypedQuery<FlightSchedule> searchQuery = em.createQuery("SELECT fs FROM FlightSchedule fs ORDER BY fs.date", FlightSchedule.class);
        List<FlightSchedule> flightSchedules = searchQuery.getResultList();
        flightSchedules.forEach(f -> {
            f.getFlight();
        });
        return flightSchedules;
    }

    public List<FlightSchedule> getFlightSchedulesByDate(Date startDate, Date endDate) {
        TypedQuery<FlightSchedule> searchQuery = em.createQuery("SELECT fs FROM FlightSchedule fs WHERE fs.date >= :date1 AND fs.date <= :date2"
                                                                + " ORDER BY fs.date", FlightSchedule.class)
                .setParameter("date1", startDate)
                .setParameter("date2", endDate);

        List<FlightSchedule> flightSchedules = searchQuery.getResultList();
        flightSchedules.forEach(f -> {
            f.getFlight();
        });
        return flightSchedules;
    }

    //TODO: test this
    /**
     * Basic flight search that takes into account the flight route, departure date and passengers
     * @param flightRoute
     * @param departureDate
     * @param passengerCount
     * @return
     */
    public List<FlightSchedule> searchFlightSchedules(FlightRoute flightRoute, Date departureDate, Integer passengerCount) {
        // Cabin Class getMaxCapacity
        // FlightSchedule
        // FlightReservations (each reservation is 1 seat)
        // 3 days before, 3 days after
        final LocalDate localDate = departureDate.toLocalDate();
        final Date threeDaysBefore = Date.valueOf(localDate.minusDays(3));
        final Date threeDaysAfter = Date.valueOf(localDate.plusDays(3));

        final TypedQuery<FlightSchedule> query = this.em.createQuery("SELECT fs FROM FlightSchedule fs WHERE fs.flight.flightRoute.flightRouteId = ?1 AND fs.date >= ?2 AND fs.date <= ?3 ORDER BY fs.date", FlightSchedule.class)
                .setParameter(1, flightRoute.getFlightRouteId())
                .setParameter(2, threeDaysBefore)
                .setParameter(3, threeDaysAfter);

        final List<FlightSchedule> countFilteredFlightSchedules = new ArrayList<>();
        final List<FlightSchedule> flightSchedules = query.getResultList();
        flightSchedules.forEach(flightSchedule -> {
            final Integer seatsTaken = flightSchedule.getFlightReservations().size() + passengerCount;
            if(flightSchedule.getFlight().getAircraftConfiguration().getTotalCabinClassCapacity() >= seatsTaken) {
                // Load flight schedule data
                flightSchedule.getFlight().getFlightRoute().getOrigin().getIataCode();
                flightSchedule.getFlight().getFlightRoute().getDest().getIataCode();
                countFilteredFlightSchedules.add(flightSchedule);
            }
        });

        return countFilteredFlightSchedules;
    }
}
