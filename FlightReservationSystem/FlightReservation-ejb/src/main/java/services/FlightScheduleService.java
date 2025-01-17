package services;

import entities.*;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
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

    public FlightSchedule findById(Long id) throws InvalidEntityIdException {
        final FlightSchedule flightSchedule = this.em.find(FlightSchedule.class, id);

        if (flightSchedule == null) {
            throw new InvalidEntityIdException("Flight Schedule could not be found.");
        }

        return flightSchedule;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FlightSchedule create(@NonNull Flight flight,
                                 @NonNull FlightSchedulePlan flightSchedulePlan,
                                 @NonNull Date departureDate,
                                 @NonNull Time departureTime,
                                 @NonNull Long estimatedDuration) throws InvalidConstraintException {
        final FlightSchedule flightSchedule = new FlightSchedule();
        flightSchedule.setFlight(flight);
        flightSchedule.setFlightSchedulePlan(flightSchedulePlan);
        flightSchedule.setDepartureDateTime(departureDate, departureTime);
        flightSchedule.setEstimatedDuration(estimatedDuration);

        Set<ConstraintViolation<FlightSchedule>> violations = this.validator.validate(flightSchedule);
        if (!violations.isEmpty()) {
            throw new InvalidConstraintException(violations.toString());
        }

        em.persist(flightSchedule);
        em.flush();

        final List<FlightSchedule> flightFlightSchedules = flight.getFlightSchedules();
        flightFlightSchedules.add(flightSchedule);
        flight.setFlightSchedules(flightFlightSchedules);
        this.em.merge(flight);
        this.em.flush();

        final List<FlightSchedule> flightSchedulePlanFlightSchedules = flightSchedulePlan.getFlightSchedules();
        flightSchedulePlanFlightSchedules.add(flightSchedule);
        flightSchedulePlan.setFlightSchedules(flightSchedulePlanFlightSchedules);
        this.em.merge(flightSchedulePlan);

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

    public void updateFlightSchedules(List<FlightSchedule> flightSchedules) {
        flightSchedules.forEach(flightSchedule -> em.merge(flightSchedule));
        em.flush();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteFlightSchedule(FlightSchedule flightSchedule) {
        FlightSchedule managedFlightSchedule = em.find(FlightSchedule.class, flightSchedule.getFlightScheduleId());
        Flight flight = em.find(Flight.class, managedFlightSchedule.getFlight().getFlightId());

        flight.getFlightSchedules().remove(managedFlightSchedule);

        em.remove(managedFlightSchedule);
        em.flush();
    }

    public List<FlightSchedule> getFlightSchedulesByFlightAndDate(String flightCode, Date startDate, Date endDate) {
        TypedQuery<FlightSchedule> searchQuery = em.createQuery("SELECT fs FROM FlightSchedule fs WHERE fs.flight.flightCode =:inFlightCode AND fs.date >= :date1 AND fs.date <= :date2"
                + " ORDER BY fs.date", FlightSchedule.class)
                .setParameter("inFlightCode", flightCode)
                .setParameter("date1", startDate)
                .setParameter("date2", endDate);

        List<FlightSchedule> flightSchedules = searchQuery.getResultList();
        flightSchedules.forEach(f -> {
            f.getFlight();
        });
        return flightSchedules;
    }

    /**
     * Basic flight search that takes into account the flight, departure date and passengers
     *
     * @param flight
     * @param departureDate
     * @param passengerCount
     * @return
     */
    public List<FlightSchedule> searchFlightSchedules(Flight flight, Date departureDate, Integer passengerCount) {
        // Cabin Class getMaxCapacity
        // FlightSchedule
        // FlightReservations (each reservation is 1 seat)
        // 3 days before, 3 days after
        final LocalDate localDate = departureDate.toLocalDate();
        final Date threeDaysBefore = Date.valueOf(localDate.minusDays(3));
        final Date threeDaysAfter = Date.valueOf(localDate.plusDays(3));

        final TypedQuery<FlightSchedule> query = this.em.createQuery("SELECT fs FROM FlightSchedule fs WHERE fs.flight.flightId = ?1 AND fs.date >= ?2 AND fs.date <= ?3 ORDER BY fs.date", FlightSchedule.class)
                .setParameter(1, flight.getFlightId())
                .setParameter(2, threeDaysBefore)
                .setParameter(3, threeDaysAfter);

        return this.searchFlightSchedules(query, passengerCount);
    }

    public List<FlightSchedule> searchFlightSchedules(Flight flight, Date departureDate, Integer passengerCount, CabinClassType cabinClassType) {
        final LocalDate localDate = departureDate.toLocalDate();
        final Date threeDaysBefore = Date.valueOf(localDate.minusDays(3));
        final Date threeDaysAfter = Date.valueOf(localDate.plusDays(3));

        final TypedQuery<FlightSchedule> query = this.em.createQuery("SELECT fs FROM FlightSchedule fs WHERE fs.flight.flightId = ?1 AND fs.date >= ?2 AND fs.date <= ?3 AND EXISTS (SELECT fare FROM Fare fare WHERE fare.flightSchedulePlan.flightSchedulePlanId = fs.flightSchedulePlan.flightSchedulePlanId AND fare.cabinClass.cabinClassId.cabinClassType = ?4) ORDER BY fs.date", FlightSchedule.class)
                .setParameter(1, flight.getFlightId())
                .setParameter(2, threeDaysBefore)
                .setParameter(3, threeDaysAfter)
                .setParameter(4, cabinClassType);

        return this.searchFlightSchedules(query, passengerCount);
    }

    public List<FlightSchedule> searchFlightSchedules(TypedQuery<FlightSchedule> typedQuery, Integer passengerCount) {
        final List<FlightSchedule> countFilteredFlightSchedules = new ArrayList<>();
        final List<FlightSchedule> flightSchedules = typedQuery.getResultList();
        flightSchedules.forEach(flightSchedule -> {
            final Integer seatsTaken = flightSchedule.getFlightReservations().size() + passengerCount;
            // Get seats left
            if (flightSchedule.getFlight().getAircraftConfiguration().getTotalCabinClassCapacity() >= seatsTaken) {
                // Load flight schedule data
                flightSchedule.getFlight().getFlightRoute().getOrigin().getIataCode();
                flightSchedule.getFlight().getFlightRoute().getDest().getIataCode();
                flightSchedule.getFlight().getAircraftConfiguration().getCabinClasses().forEach(cabinClass -> {
                    cabinClass.getCabinClassId();
                });
                countFilteredFlightSchedules.add(flightSchedule);
            }
        });

        return countFilteredFlightSchedules;
    }
}
