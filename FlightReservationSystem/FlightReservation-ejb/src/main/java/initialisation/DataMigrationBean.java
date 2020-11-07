package initialisation;

import entities.*;
import lombok.SneakyThrows;
import services.AircraftConfigurationService;
import services.CabinClassService;
import services.FlightService;

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
    AircraftConfigurationService aircraftConfigurationService;
    @Inject
    CabinClassService cabinClassService;
    @Inject
    FlightService flightService;
    @Inject
    private Pbkdf2PasswordHash passwordHash;

    private final Airport sinAirport = new Airport();
    private final Airport nrtAirport = new Airport();
    private final FlightRoute sinToNrtFR = new FlightRoute();
    private AircraftType sinNrtAT;
    private AircraftConfiguration sinNrtAC = new AircraftConfiguration();

    @PostConstruct
    public void init() {
        this.initAirportData();
        this.initAircraftTypeData();
        this.initFlightRouteData();
        this.initAircraftConfiguration();
        this.initFlightData();
        this.initEmployeeData();
    }

    private void initAirportData() {
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

        nrtAirport.setAirportName("Narita International Airport");
        nrtAirport.setIataCode("NRT");
        nrtAirport.setCity("Narita");
        nrtAirport.setCountry("Japan");
        nrtAirport.setStateName("Chiba");
        em.persist(nrtAirport);

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

        sinNrtAT = boeingSecondType;

        em.flush();
    }

    private void initFlightRouteData() {
        sinToNrtFR.setOrigin(sinAirport);
        sinToNrtFR.setDest(nrtAirport);
        em.persist(sinToNrtFR);
    }

    @SneakyThrows
    private void initAircraftConfiguration() {
        sinNrtAC = this.aircraftConfigurationService.create("basic", sinNrtAT);

        cabinClassService.create(CabinClassType.F, 3,  "3-2-3", sinNrtAC);
    }

    @SneakyThrows
    private void initFlightData() {
        this.flightService.create("ML123", sinToNrtFR, sinNrtAC);
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
