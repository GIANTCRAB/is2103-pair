package services;

import entities.AircraftType;
import exceptions.InvalidEntityIdException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless
public class AircraftTypeService {
    @PersistenceContext(unitName = "frs")
    private EntityManager em;

    public AircraftType findById(Long id) throws InvalidEntityIdException {
        final AircraftType aircraftType = this.em.find(AircraftType.class, id);

        if (aircraftType == null) {
            throw new InvalidEntityIdException();
        }

        return aircraftType;
    }
}
