package services;

import entities.AircraftConfiguration;
import entities.CabinClass;
import entities.AircraftType;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

@LocalBean
@Stateless
public class AircraftConfigurationService {

    @PersistenceContext(unitName = "frs")
    private EntityManager em;
    
    public AircraftConfiguration create(String aircraftConfigurationName, int noOfCabinClasses, List<CabinClass> cabinClassList, AircraftType aircraftType) {
        final AircraftConfiguration newAircraftConfiguration = new AircraftConfiguration();
        newAircraftConfiguration.setAircraftConfigurationName(aircraftConfigurationName);
        newAircraftConfiguration.setNoOfCabinClasses(noOfCabinClasses);
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
        final Query searchQuery = this.em.createQuery(
                "SELECT ac from AircraftConfiguration ac WHERE ac.aircraftConfigurationName LIKE ?1")
                .setParameter(1, name);
        
        return (AircraftConfiguration)searchQuery.getSingleResult();
    }
}
