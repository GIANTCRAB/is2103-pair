package services;

import entities.AircraftConfiguration;
import entities.CabinClass;
import entities.AircraftType;
import entities.CabinClassType;
import exceptions.InvalidEntityIdException;

import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@LocalBean
@Stateless
public class AircraftConfigurationService {
    private final int MIN_CABIN_CLASS_SIZE = 1;
    private final int MAX_CABIN_CLASS_SIZE = CabinClassType.values().length;

    @PersistenceContext(unitName = "frs")
    private EntityManager em;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AircraftConfiguration create(String aircraftConfigurationName, AircraftType aircraftType) {
        final AircraftConfiguration newAircraftConfiguration = new AircraftConfiguration();
        newAircraftConfiguration.setAircraftConfigurationName(aircraftConfigurationName);
        newAircraftConfiguration.setAircraftType(aircraftType);
        this.em.persist(newAircraftConfiguration);
        this.em.flush();

        return newAircraftConfiguration;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AircraftConfiguration associateWithCabinClass(AircraftConfiguration aircraftConfiguration, List<CabinClass> cabinClassList) throws InvalidEntityIdException {
        if (cabinClassList.size() < MIN_CABIN_CLASS_SIZE || cabinClassList.size() > MAX_CABIN_CLASS_SIZE) {
            throw new InvalidEntityIdException();
        }
        
        AircraftConfiguration managedAircraftConfiguration = em.find(AircraftConfiguration.class, aircraftConfiguration.getAircraftConfigurationId());
        
        for (CabinClass cabinClass : cabinClassList) {
            CabinClass managedCabinClass = em.find(CabinClass.class, cabinClass.getCabinClassId());
            managedCabinClass.setAircraftConfiguration(aircraftConfiguration);
            managedAircraftConfiguration.getCabinClasses().add(cabinClass);
        }

        this.em.flush();

        return managedAircraftConfiguration;
    }
    
    public List<Object[]> getAircraftConfigurations() {
        final Query searchQuery = this.em.createQuery("SELECT a.aircraftTypeName, ac.aircraftConfigurationName FROM " +
                                                                                  "AircraftConfiguration ac JOIN ac.aircraftType a " +
                                                                                  "ORDER BY a.aircraftTypeName, ac.aircraftConfigurationName");

        return searchQuery.getResultList();
    }
    
    public AircraftConfiguration getAircraftConfigurationById(Long id) {
        final Query searchQuery = this.em.createQuery("SELECT ac FROM AircraftConfiguration ac WHERE ac.aircraftConfigurationId = :id")
                .setParameter("id", id);
        AircraftConfiguration aircraftConfiguration = (AircraftConfiguration) searchQuery.getSingleResult();
        aircraftConfiguration.getCabinClasses().size();

        return aircraftConfiguration;
    }
    
    // Need to ensure that name is unique if using this
    public AircraftConfiguration getAircraftConfigurationByName(String name) {
        final Query searchQuery = this.em.createQuery("SELECT ac FROM AircraftConfiguration ac WHERE ac.aircraftConfigurationName = :name")
                .setParameter("name", name);
        AircraftConfiguration aircraftConfiguration = (AircraftConfiguration) searchQuery.getSingleResult();
        aircraftConfiguration.getCabinClasses().size();

        return aircraftConfiguration;
    }
}
