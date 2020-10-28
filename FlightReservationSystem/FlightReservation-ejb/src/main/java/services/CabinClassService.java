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

@LocalBean
@Stateless
public class CabinClassService {
    @PersistenceContext(unitName = "frs")
    private EntityManager em;

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CabinClass create(CabinClassType cabinClassType,
                             Integer noOfAisles,
                             Integer noOfRows,
                             String seatConfiguration,
                             AircraftConfiguration aircraftConfiguration) throws InvalidConstraintException {
        final CabinClass cabinClass = new CabinClass();
        cabinClass.setAircraftConfiguration(aircraftConfiguration);
        cabinClass.setCabinClassId(new CabinClassId(cabinClassType, aircraftConfiguration.getAircraftConfigurationId()));
        cabinClass.setNoOfAisles(noOfAisles);
        cabinClass.setNoOfRows(noOfRows);
        cabinClass.setSeatConfiguration(seatConfiguration);

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
                cabinClass.getNoOfAisles(),
                cabinClass.getNoOfRows(),
                cabinClass.getSeatConfiguration(),
                aircraftConfiguration);
    }
}
