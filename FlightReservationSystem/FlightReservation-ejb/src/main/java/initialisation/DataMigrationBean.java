package initialisation;

import entities.*;
import lombok.SneakyThrows;
import services.*;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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

    private final Airport sinAirport = new Airport();
    private final Airport nrtAirport = new Airport();
    private FlightRoute nrtToSinFR;
    private AircraftType boeingSecondType;
    private AircraftConfiguration nrtSinAC = new AircraftConfiguration();

    @PostConstruct
    public void init() {
        this.initAirportData();
        this.initAircraftTypeData();
        this.initFlightRouteData();
        this.initAircraftConfiguration();
        this.initFlightData();
        this.initFlightSchedulePlanData();
        this.initSinToNrtData();
        this.initEmployeeData();
        this.initPartnerData();
    }

    private void initAirportData() {
        sinAirport.setAirportName("Singapore Airport");
        sinAirport.setIataCode("SIN");
        sinAirport.setCity("Singapore");
        sinAirport.setCountry("Singapore");
        sinAirport.setStateName("Singapore");
        sinAirport.setZoneId("Asia/Singapore");
        em.persist(sinAirport);

        final Airport sfoAirport = new Airport();
        sfoAirport.setAirportName("San Francisco Airport");
        sfoAirport.setIataCode("SFO");
        sfoAirport.setCity("San Francisco");
        sfoAirport.setCountry("United States of America");
        sfoAirport.setStateName("California");
        sfoAirport.setZoneId("America/Los Angeles");
        em.persist(sfoAirport);

        nrtAirport.setAirportName("Narita International Airport");
        nrtAirport.setIataCode("NRT");
        nrtAirport.setCity("Narita");
        nrtAirport.setCountry("Japan");
        nrtAirport.setStateName("Chiba");
        nrtAirport.setZoneId("Asia/Tokyo");
        em.persist(nrtAirport);

        em.flush();
    }

    private void initAircraftTypeData() {
        final AircraftType boeingFirstType = new AircraftType();
        boeingFirstType.setAircraftTypeName("Boeing 737");
        boeingFirstType.setMaxCapacity(204);
        em.persist(boeingFirstType);

        this.boeingSecondType = new AircraftType();
        this.boeingSecondType.setAircraftTypeName("Boeing 747");
        this.boeingSecondType.setMaxCapacity(660);
        em.persist(this.boeingSecondType);

        em.flush();
    }

    @SneakyThrows
    private void initFlightRouteData() {
        nrtToSinFR = this.flightRouteService.create(nrtAirport, sinAirport);
    }

    @SneakyThrows
    private void initAircraftConfiguration() {
        nrtSinAC = this.aircraftConfigurationService.create("basic2", boeingSecondType);
        CabinClass cabinClass2 = cabinClassService.create(CabinClassType.J, 3, "3-2-3", nrtSinAC);
        nrtSinAC.getCabinClasses().add(cabinClass2);
        this.em.persist(nrtSinAC);
    }

    @SneakyThrows
    private void initFlightData() {
        this.flightService.create("ML124", nrtToSinFR, nrtSinAC);
    }

    @SneakyThrows
    private void initFlightSchedulePlanData() {
    }

    @SneakyThrows
    private void initSinToNrtData() {
        // Create Flight route
        final FlightRoute sinToNrtFR = this.flightRouteService.create(sinAirport, nrtAirport);

        // Create Aircraft configuration
        final AircraftConfiguration sinNrtAC = this.aircraftConfigurationService.create("basic", boeingSecondType);
        final CabinClass sinNrtCabinClassF = cabinClassService.create(CabinClassType.F, 3, "3-2-3", sinNrtAC);
        final CabinClass sinNrtCabinClassJ = cabinClassService.create(CabinClassType.J, 3, "3-2-3", sinNrtAC);
        sinNrtAC.getCabinClasses().add(sinNrtCabinClassF);
        sinNrtAC.getCabinClasses().add(sinNrtCabinClassJ);
        this.em.persist(sinNrtAC);

        // Create Flight
        final Flight sinToNrtFlight = this.flightService.create("ML123", sinToNrtFR, sinNrtAC);

        // Create flight schedules
        final LocalDateTime timeFourHoursFromNow = LocalDateTime.now().plusHours(4);
        final LocalDate localDate = LocalDate.from(timeFourHoursFromNow);
        final LocalTime localTime = LocalTime.from(timeFourHoursFromNow);
        final Date dateToday = Date.valueOf(localDate);
        final Time timeToday = Time.valueOf(localTime);
        final Long estimatedFlightDuration = 9L;
        final FlightSchedule sinToNrtFS = this.flightScheduleService.create(sinToNrtFlight, dateToday, timeToday, estimatedFlightDuration);

        final List<FlightSchedule> flightSchedules = new ArrayList<>();
        flightSchedules.add(sinToNrtFS);

        // Create flight schedule plans
        final FlightSchedulePlan flightSchedulePlan = this.flightSchedulePlanService.create(FlightSchedulePlanType.SINGLE, flightSchedules);

        // Add fares to the flight schedule plan
        final List<Fare> fares = new ArrayList<>();
        final Fare economyFare = fareService.create("123", BigDecimal.valueOf(150), sinNrtCabinClassF, flightSchedulePlan);
        final Fare premiumEconomy = fareService.create("124", BigDecimal.valueOf(350), sinNrtCabinClassF, flightSchedulePlan);
        fares.add(economyFare);
        fares.add(premiumEconomy);

        flightSchedulePlanService.associateWithFares(flightSchedulePlan, fares);
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

    private void initPartnerData() {
        final Partner partner = new Partner();
        partner.setCompanyName("Pepega Co.");
        partner.setUsername("partner1");
        partner.setPassword(this.passwordHash.generate("123".toCharArray()));
        em.persist(partner);
    }
}
