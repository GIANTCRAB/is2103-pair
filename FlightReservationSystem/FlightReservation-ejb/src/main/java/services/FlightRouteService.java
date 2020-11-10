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

    public boolean canAddFlights(FlightRoute flightRoute) {
        return flightRoute.getEnabled();
    }

    public FlightRoute findById(FlightRouteId id) throws InvalidEntityIdException {
        final FlightRoute managedFlightRoute = this.em.find(FlightRoute.class, id);

        if (managedFlightRoute == null) {
            throw new InvalidEntityIdException();
        }

        return managedFlightRoute;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FlightRoute create(Airport origin, Airport destination) throws InvalidConstraintException, FlightRouteAlreadyExistException, InvalidEntityIdException {
        FlightRoute flightRoute = findFlightRouteByOriginDest(origin, destination);
        if (flightRoute == null) {
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

            final List<FlightRoute> originFlightRoutes = origin.getOriginFlightRoutes();
            originFlightRoutes.add(flightRoute);
            origin.setOriginFlightRoutes(originFlightRoutes);
            this.em.persist(origin);

            final List<FlightRoute> destFlightRoutes = destination.getDestFlightRoutes();
            destFlightRoutes.add(flightRoute);
            destination.setDestFlightRoutes(destFlightRoutes);
            this.em.persist(destination);
            this.em.flush();

            return flightRoute;
        } else {
            throw new FlightRouteAlreadyExistException();
        }
    }

    public List<FlightRoute> getFlightRoutes() {
        final TypedQuery<FlightRoute> searchQuery = this.em.createQuery("select fr from FlightRoute fr ORDER BY fr.flightRouteId.originId", FlightRoute.class);
        List<FlightRoute> flightRoutes = searchQuery.getResultList();

        flightRoutes.forEach(flightRoute -> {
            flightRoute.getOrigin();
            flightRoute.getDest();
        });
        return flightRoutes;
    }

    public FlightRoute findFlightRouteByOriginDest(Airport origin, Airport destination) throws InvalidEntityIdException {
        final FlightRouteId flightRouteId = new FlightRouteId(origin.getIataCode(), destination.getIataCode());
        final FlightRoute flightRoute = em.find(FlightRoute.class, flightRouteId);

        if (flightRoute == null) {
            throw new InvalidEntityIdException();
        }

        return flightRoute;
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
