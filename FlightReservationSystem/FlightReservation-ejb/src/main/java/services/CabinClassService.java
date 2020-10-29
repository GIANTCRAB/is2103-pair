package services;

import entities.*;
import exceptions.InvalidConstraintException;

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
        cabinClass.setSeatConfiguration(seatConfiguration);
        cabinClass.setMaxCapacity(this.calculateMaxCapacity(noOfRows, seatConfiguration));

        Set<ConstraintViolation<CabinClass>> violations = this.validator.validate(cabinClass);
        // There are invalid data
        if (!violations.isEmpty()) {
            throw new InvalidConstraintException(violations.toString());
        }
        this.em.persist(cabinClass);
        this.em.flush();

        return cabinClass;
    }

    public CabinClass create(CabinClass cabinClass, AircraftConfiguration aircraftConfiguration) throws InvalidConstraintException {
        return this.create(cabinClass.getTemporaryCabinClassType(),
                cabinClass.getNoOfRows(),
                cabinClass.getSeatConfiguration(),
                aircraftConfiguration);
    }
    
    private int calculateNoOfAisles(String seatConfiguration) throws InvalidConstraintException {
        int count = (int) seatConfiguration.chars().filter(ch -> ch == '-').count();
        return count;
    }
    
    private int calculateMaxCapacity(int noOfRows, String seatConfiguration) throws InvalidConstraintException {
        Pattern pattern = Pattern.compile("([0-9][0-9]?)-([0-9][0-9]?)(-([0-9][0-9]?))?");
        Matcher matcher = pattern.matcher(seatConfiguration);
        
        // This is a little inefficient
        int maxCapacity = 0;
        if (matcher.matches()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                maxCapacity += Integer.parseInt(matcher.group(i));
            }
        } else {
            throw new InvalidConstraintException("Invalid seat configuration!");
        }
        
        return maxCapacity;
    }
}
