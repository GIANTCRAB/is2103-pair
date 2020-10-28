package initialisation;

import entities.Airport;
import entities.AircraftType;
import entities.Employee;
import entities.EmployeeRole;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;

@Startup
@Singleton
public class DataMigrationBean {
    @PersistenceContext(unitName = "frs")
    private EntityManager em;
    @Inject
    private Pbkdf2PasswordHash passwordHash;

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
        final Employee fleetManager = new Employee();
        fleetManager.setEmployeeRole(EmployeeRole.FLEET_MANAGER);
        fleetManager.setFirstName("John");
        fleetManager.setLastName("Doe");
        fleetManager.setPassword(this.passwordHash.generate("123".toCharArray()));
        fleetManager.setUsername("fleet_manager");
        em.persist(fleetManager);

        final Employee routePlanner = new Employee();
        routePlanner.setEmployeeRole(EmployeeRole.ROUTE_PLANNER);
        routePlanner.setFirstName("John");
        routePlanner.setLastName("Doe");
        routePlanner.setPassword(this.passwordHash.generate("123".toCharArray()));
        routePlanner.setUsername("route_planner");
        em.persist(routePlanner);

        final Employee salesManager = new Employee();
        salesManager.setEmployeeRole(EmployeeRole.SALES_MANAGER);
        salesManager.setFirstName("John");
        salesManager.setLastName("Doe");
        salesManager.setPassword(this.passwordHash.generate("123".toCharArray()));
        salesManager.setUsername("sales_manager");
        em.persist(salesManager);

        final Employee scheduleManager = new Employee();
        scheduleManager.setEmployeeRole(EmployeeRole.SCHEDULE_MANAGER);
        scheduleManager.setFirstName("John");
        scheduleManager.setLastName("Doe");
        scheduleManager.setPassword(this.passwordHash.generate("123".toCharArray()));
        scheduleManager.setUsername("schedule_manager");
        em.persist(scheduleManager);

        em.flush();
    }
}
