package services;

import entities.*;
import exceptions.InvalidEntityIdException;
import lombok.NonNull;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@LocalBean
@Stateless
public class FareService {
    @PersistenceContext(unitName = "frs")
    private EntityManager em;
    @Inject
    CabinClassService cabinClassService;

    public Fare findByScheduleAndCabinClass(@NonNull FlightSchedule flightSchedule, @NonNull CabinClassType cabinClassType) throws InvalidEntityIdException {
        final AircraftConfiguration aircraftConfiguration = flightSchedule.getFlight().getAircraftConfiguration();
        final CabinClassId cabinClassId = new CabinClassId();
        cabinClassId.setAircraftConfigurationId(aircraftConfiguration.getAircraftConfigurationId());
        cabinClassId.setCabinClassType(cabinClassType);
        final CabinClass cabinClass = cabinClassService.findById(cabinClassId);
        final FlightSchedulePlan flightSchedulePlan = flightSchedule.getFlightSchedulePlan();

        TypedQuery<Fare> query = this.em.createQuery("SELECT f FROM Fare f WHERE f.cabinClass.cabinClassId = ?1 AND f.flightSchedulePlan.flightSchedulePlanId = ?2", Fare.class)
                .setParameter(1, cabinClass.getCabinClassId())
                .setParameter(2, flightSchedulePlan.getFlightSchedulePlanId());

        return query.getSingleResult();
    }
}
