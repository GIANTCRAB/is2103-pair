package initialisation;

import entities.*;
import lombok.SneakyThrows;
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

    @SneakyThrows
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

        final Employee employee3 = new Employee();
        employee3.setFirstName("Schedule");
        employee3.setLastName("Manager");
        employee3.setUsername("schedulemanager");
        employee3.setPassword(this.passwordHash.generate("password".toCharArray()));
        employee3.setEmployeeRole(EmployeeRole.SCHEDULE_MANAGER);
        this.em.persist(employee3);

        final Employee employee4 = new Employee();
        employee4.setFirstName("Schedule");
        employee4.setLastName("Manager");
        employee4.setUsername("schedulemanager");
        employee4.setPassword(this.passwordHash.generate("password".toCharArray()));
        employee4.setEmployeeRole(EmployeeRole.SCHEDULE_MANAGER);
        this.em.persist(employee4);

        final Partner partner = new Partner();
        partner.setUsername("Holiday.com");
        partner.setPassword(this.passwordHash.generate("password".toCharArray()));
        this.em.persist(partner);

        final Airport airport1 = new Airport();
        airport1.setAirportName("Changi");
        airport1.setIataCode("SIN");
        airport1.setCity("Singapore");
        airport1.setStateName("Singapore");
        airport1.setCountry("Singapore");
        airport1.setZoneId("Asia/Singapore");
        this.em.persist(airport1);

        final Airport airport2 = new Airport();
        airport2.setAirportName("Hong Kong");
        airport2.setIataCode("HKG");
        airport2.setCity("Chek Lap Kok");
        airport2.setStateName("Hong Kong");
        airport2.setCountry("China");
        airport2.setZoneId("Asia/Hong_Kong");
        this.em.persist(airport2);

        final Airport airport3 = new Airport();
        airport3.setAirportName("Taoyuan");
        airport3.setIataCode("TPE");
        airport3.setCity("Taoyuan");
        airport3.setStateName("Taipei");
        airport3.setCountry("Taiwan R.O.C.");
        airport3.setZoneId("Asia/Taipei");
        this.em.persist(airport3);

        final Airport airport4 = new Airport();
        airport4.setAirportName("Narita");
        airport4.setIataCode("NRT");
        airport4.setCity("Narita");
        airport4.setStateName("Chiba");
        airport4.setCountry("Japan");
        airport4.setZoneId("Asia/Tokyo");
        this.em.persist(airport4);

        final Airport airport5 = new Airport();
        airport5.setAirportName("Sydney");
        airport5.setIataCode("SYD");
        airport5.setCity("Sydney");
        airport5.setStateName("New South Wales");
        airport5.setCountry("Australia");
        airport5.setZoneId("Australia/NSW");
        this.em.persist(airport5);

        final AircraftType aircraftType1 = new AircraftType();
        aircraftType1.setAircraftTypeName("Boeing 737");
        aircraftType1.setMaxCapacity(200);
        this.em.persist(aircraftType1);

        final AircraftType aircraftType2 = new AircraftType();
        aircraftType2.setAircraftTypeName("Boeing 747");
        aircraftType2.setMaxCapacity(400);
        this.em.persist(aircraftType2);

        final AircraftConfiguration aircraftConfiguration1 = this.aircraftConfigurationService.create("Boeing 737 All Economy", aircraftType1);

        this.cabinClassService.create(CabinClassType.Y, 30, "3-3", aircraftConfiguration1);

        final AircraftConfiguration aircraftConfiguration2 = this.aircraftConfigurationService.create("Boeing 737 Three Classes", aircraftType1);

        this.cabinClassService.create(CabinClassType.F, 5, "1-1", aircraftConfiguration2);
        this.cabinClassService.create(CabinClassType.J, 5, "2-2", aircraftConfiguration2);
        this.cabinClassService.create(CabinClassType.Y, 25, "3-3", aircraftConfiguration2);

        final AircraftConfiguration aircraftConfiguration3 = this.aircraftConfigurationService.create("Boeing 747 All Economy", aircraftType2);

        this.cabinClassService.create(CabinClassType.Y, 38, "3-4-3", aircraftConfiguration3);

        final AircraftConfiguration aircraftConfiguration4 = this.aircraftConfigurationService.create("Boeing 747 Three Classes", aircraftType2);

        this.cabinClassService.create(CabinClassType.F, 5, "1-1", aircraftConfiguration4);
        this.cabinClassService.create(CabinClassType.J, 5, "2-2-2", aircraftConfiguration4);
        this.cabinClassService.create(CabinClassType.Y, 32, "3-4-3", aircraftConfiguration4);




    }
}
