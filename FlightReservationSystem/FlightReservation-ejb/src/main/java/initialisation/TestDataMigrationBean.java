package initialisation;

import entities.Employee;
import entities.EmployeeRole;
import services.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;

public class TestDataMigrationBean {
    @PersistenceContext(unitName = "frs")
    private EntityManager em;
    @Inject
    AircraftConfigurationService aircraftConfigurationService;
    @Inject
    CabinClassService cabinClassService;
    @Inject
    FlightRouteService flightRouteService;
    @Inject
    FlightService flightService;
    @Inject
    FlightScheduleService flightScheduleService;
    @Inject
    FlightSchedulePlanService flightSchedulePlanService;
    @Inject
    FareService fareService;
    @Inject
    private Pbkdf2PasswordHash passwordHash;

    @PostConstruct
    public void init() {
        // Create Employee
        final Employee employee1 = new Employee();
        employee1.setFirstName("Fleet");
        employee1.setLastName("Manager");
        employee1.setUsername("fleetmanager");
        employee1.setPassword(this.passwordHash.generate("password".toCharArray()));
        employee1.setEmployeeRole(EmployeeRole.FLEET_MANAGER);
        this.em.persist(employee1);

        final Employee employee2 = new Employee();
        employee2.setFirstName("Route");
        employee2.setLastName("Planner");
        employee2.setUsername("routeplanner");
        employee2.setPassword(this.passwordHash.generate("password".toCharArray()));
        employee2.setEmployeeRole(EmployeeRole.ROUTE_PLANNER);
        this.em.persist(employee2);




    }
}
