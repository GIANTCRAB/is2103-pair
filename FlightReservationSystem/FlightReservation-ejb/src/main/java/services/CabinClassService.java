package services;

import entities.*;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;

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
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@LocalBean
@Stateless
public class CabinClassService {
    @PersistenceContext(unitName = "frs")
    private EntityManager em;

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    public CabinClass findById(CabinClassId cabinClassId) throws InvalidEntityIdException {
        final CabinClass cabinClass = this.em.find(CabinClass.class, cabinClassId);

        if (cabinClass == null) {
            throw new InvalidEntityIdException("Cabin Class could not be found.");
        }

        return cabinClass;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CabinClass create(CabinClassType cabinClassType,
                             Integer noOfRows,
                             String seatConfiguration,
                             AircraftConfiguration aircraftConfiguration) throws InvalidConstraintException {
        final CabinClass cabinClass = new CabinClass();
        cabinClass.setAircraftConfiguration(aircraftConfiguration);
        cabinClass.setCabinClassId(new CabinClassId(cabinClassType, aircraftConfiguration.getAircraftConfigurationId()));
        cabinClass.setNoOfAisles(this.calculateNoOfAisles(seatConfiguration));
        cabinClass.setNoOfRows(noOfRows);
        cabinClass.setNoOfCols(this.calculateNoOfCols(seatConfiguration));
        cabinClass.setSeatConfiguration(seatConfiguration);
        cabinClass.setMaxCapacity(noOfRows * calculateNoOfCols(seatConfiguration));

        Set<ConstraintViolation<CabinClass>> violations = this.validator.validate(cabinClass);
        // There are invalid data
        if (!violations.isEmpty()) {
            throw new InvalidConstraintException(violations.toString());
        }
        this.em.persist(cabinClass);
        this.em.flush();

        return cabinClass;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CabinClass create(CabinClass cabinClass, AircraftConfiguration aircraftConfiguration) throws InvalidConstraintException {
        return this.create(cabinClass.getTemporaryCabinClassType(),
                cabinClass.getNoOfRows(),
                cabinClass.getSeatConfiguration(),
                aircraftConfiguration);
    }

    private int calculateNoOfAisles(String seatConfiguration) {
        int count = (int) seatConfiguration.chars().filter(ch -> ch == '-').count();
        return count;
    }

    private int calculateNoOfCols(String seatConfiguration) throws InvalidConstraintException {
        Pattern pattern = Pattern.compile("(?<col1>[0-9][0-9]?)-(?<col2>[0-9][0-9]?)(-(?<col3>[0-9][0-9]?))?");
        Matcher matcher = pattern.matcher(seatConfiguration);

        int noOfCols;
        if (matcher.matches()) {
            noOfCols = Integer.parseInt(matcher.group("col1")) + Integer.parseInt(matcher.group("col2"));
            if (matcher.group("col3") != null) {
                noOfCols += Integer.parseInt(matcher.group("col3"));
            }
        } else {
            throw new InvalidConstraintException("Invalid seat configuration!");
        }

        return noOfCols;
    }
    
    public int getSeatsReservedForCabinClass(FlightSchedule flightSchedule, CabinClassType cabinClassType) {
        int noOfSeatsTaken = flightSchedule.getFlightReservations().stream().
                filter(r -> r.getCabinClassType() == cabinClassType).mapToInt(i -> 1).sum();
        return noOfSeatsTaken;
    }
}
