package services;

import entities.AircraftConfiguration;
import entities.CabinClass;
import entities.AircraftType;
import exceptions.InvalidEntityIdException;

import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@LocalBean
@Stateless
public class AircraftConfigurationService {
    private final int MIN_CABIN_CLASS_SIZE = 1;
    private final int MAX_CABIN_CLASS_SIZE = 4;

    @PersistenceContext(unitName = "frs")
    private EntityManager em;

    public AircraftConfiguration create(String aircraftConfigurationName, List<CabinClass> cabinClassList, AircraftType aircraftType) throws InvalidEntityIdException {
        if (cabinClassList.size() < MIN_CABIN_CLASS_SIZE || cabinClassList.size() > MAX_CABIN_CLASS_SIZE) {
            throw new InvalidEntityIdException();
        }

        final AircraftConfiguration newAircraftConfiguration = new AircraftConfiguration();
        newAircraftConfiguration.setAircraftConfigurationName(aircraftConfigurationName);
        newAircraftConfiguration.setCabinClasses(cabinClassList);
        newAircraftConfiguration.setAircraftType(aircraftType);

        this.em.persist(newAircraftConfiguration);
        this.em.flush();

        return newAircraftConfiguration;
    }

    public List<AircraftConfiguration> getAircraftConfigurations() {
        final TypedQuery<AircraftConfiguration> searchQuery = this.em.createQuery("SELECT ac from AircraftConfiguration ac", AircraftConfiguration.class);

        return searchQuery.getResultList();
    }

    public AircraftConfiguration findAircraftConfigurationByName(String name) {
        final TypedQuery<AircraftConfiguration> searchQuery = this.em.createQuery(
                "SELECT ac from AircraftConfiguration ac WHERE ac.aircraftConfigurationName LIKE ?1", AircraftConfiguration.class)
                .setParameter(1, name);

        return searchQuery.getSingleResult();
    }
}
