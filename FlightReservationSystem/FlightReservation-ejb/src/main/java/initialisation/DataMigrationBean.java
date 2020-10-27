package initialisation;

import entities.Airport;
import entities.AircraftType;
import entities.Employee;
import entities.EmployeeRole;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Startup
@Singleton
public class DataMigrationBean {
    @PersistenceContext(unitName = "frs")
    private EntityManager em;

    @PostConstruct
    public void init() {
        this.initAirportData();
        this.initAircraftTypeData();
        this.initEmployeeData();
    }

    private void initAirportData() {
        final Airport sinAirport = new Airport();
        sinAirport.setAirportName("Singapore Airport");
        sinAirport.setIataCode("SIN");
        sinAirport.setCity("Singapore");
        sinAirport.setCountry("Singapore");
        sinAirport.setStateName("Singapore");
        em.persist(sinAirport);

        final Airport sfoAirport = new Airport();
        sfoAirport.setAirportName("San Francisco Airport");
        sfoAirport.setIataCode("SFO");
        sfoAirport.setCity("San Francisco");
        sfoAirport.setCountry("United States of America");
        sfoAirport.setStateName("California");
        em.persist(sfoAirport);

        em.flush();
    }
    
    private void initAircraftTypeData() {
        final AircraftType boeingFirstType = new AircraftType();
        boeingFirstType.setAircraftTypeName("Boeing 737");
        boeingFirstType.setMaxCapacity(204);
        em.persist(boeingFirstType);
        
        final AircraftType boeingSecondType = new AircraftType();
        boeingSecondType.setAircraftTypeName("Boeing 747");
        boeingSecondType.setMaxCapacity(660);
        em.persist(boeingSecondType);
        
        em.flush();
    }
    
    private void initEmployeeData() {
        final Employee adminEmployee = new Employee();
        adminEmployee.setEmployeeRole(EmployeeRole.SYSTEM_ADMIN);
        adminEmployee.setFirstName("John");
        adminEmployee.setLastName("Doe");
        adminEmployee.setPassword("123");
        adminEmployee.setUsername("admin");
        em.persist(adminEmployee);
        
        em.flush();
    }
}
