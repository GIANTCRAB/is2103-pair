package services;

import entities.Airport;
import entities.FlightRoute;
import entities.FlightRouteId;
import exceptions.FlightRouteAlreadyExistException;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
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

    @Inject
    FlightService flightService;

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FlightRoute create(Airport origin, Airport destination) throws InvalidConstraintException, FlightRouteAlreadyExistException {
        FlightRoute flightRoute = findFlightRouteByOriginDest(origin, destination);
        if(flightRoute == null) {
            flightRoute = new FlightRoute();
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

        } else {
            throw new FlightRouteAlreadyExistException();
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void associateReturnFlightRoute(FlightRoute mainFlightRoute, FlightRoute returnFlightRoute) {
        mainFlightRoute.setReturnFlightRoute(returnFlightRoute);
        returnFlightRoute.setMainFlightRoute(mainFlightRoute);
    }

    public List<FlightRoute> getFlightRoutes() {
        // I don't know why this keeps retrieving all routes even if it's a return flight route
        final TypedQuery<FlightRoute> searchQuery = this.em.createQuery("select fr from FlightRoute fr WHERE fr.returnFlightRoute IS NOT NULL ORDER BY fr.flightRouteId.originId", FlightRoute.class);
        List<FlightRoute> flightRoutes = searchQuery.getResultList();

        flightRoutes.forEach(flightRoute -> {
            flightRoute.getOrigin();
            flightRoute.getDest();
            flightRoute.getReturnFlightRoute();
        });
        return flightRoutes;
    }

    public FlightRoute findFlightRouteByOriginDest(Airport origin, Airport destination) {
        FlightRouteId flightRouteId = new FlightRouteId(origin.getIataCode(), destination.getIataCode());
        FlightRoute flightRoute = em.find(FlightRoute.class, flightRouteId);
        return flightRoute;
    }

    public FlightRoute retrieveManagedEntity(FlightRoute flightRoute) throws InvalidEntityIdException {
        final FlightRoute managedFlightRoute = this.em.find(FlightRoute.class, flightRoute.getFlightRouteId());

        if (managedFlightRoute == null) {
            throw new InvalidEntityIdException();
        }

        return managedFlightRoute;
    }

    public void delete(FlightRoute flightRoute) {
        // If flight route has flights, then disable it instead of deleting it
        if (flightRoute.getFlights().size() > 0) {
            flightRoute.setEnabled(false);
            this.em.persist(flightRoute);
        } else {
            this.em.remove(flightRoute);
        }
    }
}
