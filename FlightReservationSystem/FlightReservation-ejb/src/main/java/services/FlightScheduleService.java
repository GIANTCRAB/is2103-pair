package services;

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
}
