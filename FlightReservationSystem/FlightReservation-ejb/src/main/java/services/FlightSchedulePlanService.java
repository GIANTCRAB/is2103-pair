package services;

import entities.*;
import exceptions.*;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import lombok.NonNull;

@LocalBean
@Stateless
public class FlightSchedulePlanService {
    @PersistenceContext(unitName = "frs")
    private EntityManager em;
    @Inject
    FlightScheduleService flightScheduleService;

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FlightSchedulePlan create(@NonNull FlightSchedulePlanType flightSchedulePlanType, List<FlightSchedule> flightSchedules) throws InvalidConstraintException {
        final FlightSchedulePlan flightSchedulePlan = new FlightSchedulePlan();
        flightSchedulePlan.setFlightSchedulePlanType(flightSchedulePlanType);
        flightSchedulePlan.setFlightSchedules(flightSchedules);

        Set<ConstraintViolation<FlightSchedulePlan>> violations = this.validator.validate(flightSchedulePlan);
        if (!violations.isEmpty()) {
            throw new InvalidConstraintException(violations.toString());
        }

        em.persist(flightSchedulePlan);
        flightSchedules.forEach(f -> {
            f.setFlightSchedulePlan(flightSchedulePlan);
            em.persist(f);
        });
        em.flush();

        return flightSchedulePlan;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FlightSchedulePlan createRecurrentFlightSchedule(@NonNull FlightSchedulePlanType flightSchedulePlanType,
                                                            @NonNull Flight flight,
                                                            @NonNull Date departureDate,
                                                            @NonNull Time departureTime,
                                                            @NonNull Long estimatedDuration,
                                                            @NonNull Date recurrentEndDate,
                                                            Integer nDays) throws InvalidConstraintException, InvalidEntityIdException {
        if (flightSchedulePlanType.equals(FlightSchedulePlanType.RECURRENT_WEEKLY)) {
            nDays = 7;
        }

        if (flightSchedulePlanType.equals(FlightSchedulePlanType.SINGLE) || flightSchedulePlanType.equals(FlightSchedulePlanType.MULTIPLE)) {
            throw new InvalidEntityIdException("Not recurrent schedule type!");
        }

        final List<FlightSchedule> flightSchedules = new ArrayList<>();
        for (LocalDate date = departureDate.toLocalDate(); date.isBefore(recurrentEndDate.toLocalDate()); date = date.plusDays(nDays)) {
            Date sqlDate = Date.valueOf(date);
            flightSchedules.add(this.flightScheduleService.create(flight, sqlDate, departureTime, estimatedDuration));
        }
        return this.create(flightSchedulePlanType, flightSchedules);
    }

    public FlightSchedulePlan createRecurrentFlightSchedule(@NonNull FlightSchedulePlanType flightSchedulePlanType,
                                                            @NonNull Flight flight,
                                                            @NonNull Date departureDate,
                                                            @NonNull Time departureTime,
                                                            @NonNull Long estimatedDuration,
                                                            @NonNull Date recurrentEndDate) throws InvalidConstraintException, InvalidEntityIdException {
        return this.createRecurrentFlightSchedule(flightSchedulePlanType, flight, departureDate, departureTime, estimatedDuration, recurrentEndDate, null);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FlightSchedulePlan associateWithFares(@NonNull FlightSchedulePlan flightSchedulePlan, List<Fare> fares) {
        flightSchedulePlan.setFares(fares);
        em.merge(flightSchedulePlan);
        em.flush();
        return flightSchedulePlan;
    }

    public void addFlightSchedules(FlightSchedulePlan flightSchedulePlan, List<FlightSchedule> flightSchedules) {

        flightSchedules.forEach(flightSchedule -> {
            flightSchedulePlan.getFlightSchedules().add(flightSchedule);
            flightSchedule.setFlightSchedulePlan(flightSchedulePlan);
            em.merge(flightSchedule);
        });

        em.merge(flightSchedulePlan);
        em.flush();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FlightSchedulePlan getFlightSchedulePlanById(Long id) {
        FlightSchedulePlan flightSchedulePlan = em.find(FlightSchedulePlan.class, id);
        flightSchedulePlan.getFlightSchedules();
        flightSchedulePlan.getFares();
        flightSchedulePlan.getFares().forEach(fare -> fare.getCabinClass());

        flightSchedulePlan.getFlightSchedules().forEach(flightSchedule -> {
            flightSchedule.getFlight();
            flightSchedule.getFlight().getFlightRoute();
            flightSchedule.getFlight().getFlightRoute().getOrigin();
            flightSchedule.getFlight().getFlightRoute().getDest();
            flightSchedule.getFlight().getAircraftConfiguration();
            flightSchedule.getFlightReservations().size();
        });
        return flightSchedulePlan;
    }

    public List<FlightSchedulePlan> getFlightSchedulePlans() {
        Query searchQuery = em.createQuery("SELECT fsp from FlightSchedulePlan fsp JOIN fsp.flightSchedules fs JOIN fs.flight f GROUP BY fsp.flightSchedulePlanId ORDER BY f.flightCode ASC, MIN(fs.date) DESC", FlightSchedulePlan.class);
        List<FlightSchedulePlan> flightSchedulePlans = searchQuery.getResultList();
        flightSchedulePlans.forEach(flightSchedulePlan -> {
            flightSchedulePlan.getFlightSchedules().size();
            flightSchedulePlan.getFlightSchedules().forEach(flightSchedule -> flightSchedule.getFlight());
        });
        return flightSchedulePlans;
    }
    
    public List<FlightSchedulePlan> getFlightSchedulePlansByFlightCodeAndDateTime(String flightCode, Date date, Time time) {
        Query searchQuery = em.createQuery("SELECT fsp from FlightSchedulePlan fsp JOIN fsp.flightSchedules fs JOIN fs.flight f WHERE f.flightCode = :inFlightCode AND fs.date >= :inDate AND fs.time >= :inTime GROUP BY fsp.flightSchedulePlanId ORDER BY MIN(fs.time) ASC", FlightSchedulePlan.class)
                .setParameter("inFlightCode", flightCode)
                .setParameter("inDate", date)
                .setParameter("inTime", time);
        
        List<FlightSchedulePlan> flightSchedulePlans = searchQuery.getResultList();
        flightSchedulePlans.forEach(flightSchedulePlan -> {
            flightSchedulePlan.getFlightSchedules().size();
            flightSchedulePlan.getFlightSchedules().forEach(flightSchedule -> flightSchedule.getFlight());
        });
        return flightSchedulePlans;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteFlightSchedulePlan(FlightSchedulePlan flightSchedulePlan) {
        em.remove(flightSchedulePlan);
        em.flush();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void disableFlightSchedulePlan(FlightSchedulePlan flightSchedulePlan) {
        FlightSchedulePlan managedFlightSchedulePlan = em.find(FlightSchedulePlan.class, flightSchedulePlan.getFlightSchedulePlanId());
        managedFlightSchedulePlan.setEnabled(false);
    }
}
