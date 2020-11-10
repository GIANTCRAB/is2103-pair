package services;

import entities.CabinClassType;
import entities.Fare;
import entities.Flight;
import entities.FlightSchedule;
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
    public FlightSchedule create(@NonNull Flight flight, @NonNull Date departureDate, @NonNull Time departureTime, @NonNull Long estimatedDuration, @NonNull List<Fare> fares) throws InvalidConstraintException {
        final FlightSchedule flightSchedule = new FlightSchedule();
        flightSchedule.setFlight(flight);
        flightSchedule.setDepartureDateTime(departureDate, departureTime);
        flightSchedule.setEstimatedDuration(estimatedDuration);
        flightSchedule.setFares(fares);

        Set<ConstraintViolation<FlightSchedule>> violations = this.validator.validate(flightSchedule);
        if (!violations.isEmpty()) {
            throw new InvalidConstraintException(violations.toString());
        }

        em.persist(flightSchedule);
        em.flush();

        return flightSchedule;
    }

    public FlightSchedule create(@NonNull Flight flight, @NonNull Date departureDate, @NonNull Time departureTime, @NonNull Long estimatedDuration) throws InvalidConstraintException {
        return this.create(flight, departureDate, departureTime, estimatedDuration, new ArrayList<>());
    }

    public List<FlightSchedule> getFlightSchedules() {
        TypedQuery<FlightSchedule> searchQuery = em.createQuery("SELECT fs FROM FlightSchedule fs ORDER BY fs.date", FlightSchedule.class);
        List<FlightSchedule> flightSchedules = searchQuery.getResultList();
        flightSchedules.forEach(f -> {
            f.getFlight();
            f.getFares().size();
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
            f.getFares().size();
        });
        return flightSchedules;
    }

    //TODO: implement this
    public List<FlightSchedule> searchFlightSchedules(Date departureDate, CabinClassType cabinClassType) {
        return this.em.createQuery("", FlightSchedule.class).getResultList();
    }
}
