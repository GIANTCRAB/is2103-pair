package services;

import entities.Airport;
import entities.FlightRoute;
import exceptions.InvalidConstraintException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;

@LocalBean
@Stateless
public class FlightRouteService {
    @PersistenceContext(unitName = "frs")
    private EntityManager em;

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    public FlightRoute create(Airport origin, Airport destination) throws InvalidConstraintException {
        final FlightRoute flightRoute = new FlightRoute();
        flightRoute.setOrigin(origin);
        flightRoute.setDest(destination);
        Set<ConstraintViolation<FlightRoute>> violations = this.validator.validate(flightRoute);
        // There are invalid data
        if (!violations.isEmpty()) {
            throw new InvalidConstraintException(violations.toString());
        }
        this.em.persist(flightRoute);
        this.em.flush();

        return flightRoute;
    }

    public List<FlightRoute> getFlightRoutes() {
        final TypedQuery<FlightRoute> searchQuery = this.em.createQuery("select fr from FlightRoute fr", FlightRoute.class);

        return searchQuery.getResultList();
    }
}
