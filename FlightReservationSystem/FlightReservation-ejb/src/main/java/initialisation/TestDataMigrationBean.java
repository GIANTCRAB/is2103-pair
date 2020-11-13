package initialisation;

import entities.*;
import lombok.SneakyThrows;
import services.*;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;

@Startup
@Singleton
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
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
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
        partner.setUsername("holidaydotcom");
        partner.setCompanyName("Holiday.com");
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

        // SIN, HKG
        final FlightRoute sinHkgFR = this.flightRouteService.create(airport1, airport2);
        // HKG, SIN
        final FlightRoute hkgSinFR = this.flightRouteService.create(airport2, airport1);
        // SIN, TPE
        final FlightRoute sinTpeFR = this.flightRouteService.create(airport1, airport3);
        // TPE, SIN
        final FlightRoute tpeSinFR = this.flightRouteService.create(airport3, airport1);
        // SIN, NRT
        final FlightRoute sinNrtFR = this.flightRouteService.create(airport1, airport4);
        // NRT, SIN
        final FlightRoute nrtSinFR = this.flightRouteService.create(airport4, airport1);
        // HKG, NRT
        final FlightRoute hkgNrtFR = this.flightRouteService.create(airport2, airport4);
        // NRT, HKG
        final FlightRoute nrtHkgFR = this.flightRouteService.create(airport4, airport2);
        // TPE, NRT
        final FlightRoute tpeNrtFR = this.flightRouteService.create(airport3, airport4);
        // NRT, TPE
        final FlightRoute nrtTpeFR = this.flightRouteService.create(airport4, airport3);
        // SIN, SYD
        final FlightRoute sinSydFR = this.flightRouteService.create(airport1, airport5);
        // SYD, SIN
        final FlightRoute sydSinFR = this.flightRouteService.create(airport5, airport1);
        // SYD, NRT
        final FlightRoute sydNrtFR = this.flightRouteService.create(airport5, airport4);
        // NRT, SYD
        final FlightRoute nrtSydFR = this.flightRouteService.create(airport4, airport5);

        final Flight ml111 = this.flightService.create("ML111", sinHkgFR, aircraftConfiguration2);
        final Flight ml112 = this.flightService.create("ML112", hkgSinFR, aircraftConfiguration2);

        final Flight ml211 = this.flightService.create("ML211", sinTpeFR, aircraftConfiguration2);
        final Flight ml212 = this.flightService.create("ML212", tpeSinFR, aircraftConfiguration2);

        final Flight ml311 = this.flightService.create("ML311", sinNrtFR, aircraftConfiguration2);
        final Flight ml312 = this.flightService.create("ML312", nrtSinFR, aircraftConfiguration2);

        final Flight ml411 = this.flightService.create("ML411", hkgNrtFR, aircraftConfiguration2);
        final Flight ml412 = this.flightService.create("ML412", nrtHkgFR, aircraftConfiguration2);

        final Flight ml511 = this.flightService.create("ML511", tpeNrtFR, aircraftConfiguration2);
        final Flight ml512 = this.flightService.create("ML512", nrtTpeFR, aircraftConfiguration2);

        final Flight ml611 = this.flightService.create("ML611", sinSydFR, aircraftConfiguration2);
        final Flight ml612 = this.flightService.create("ML612", sydSinFR, aircraftConfiguration2);

        final Flight ml621 = this.flightService.create("ML621", sinSydFR, aircraftConfiguration3);
        final Flight ml622 = this.flightService.create("ML622", sydSinFR, aircraftConfiguration3);

        final Flight ml711 = this.flightService.create("ML711", sydNrtFR, aircraftConfiguration4);
        final Flight ml712 = this.flightService.create("ML712", nrtSydFR, aircraftConfiguration4);

        // Flight Schedule Plan
        final CabinClassId cabinClassId1 = new CabinClassId();
        cabinClassId1.setCabinClassType(CabinClassType.F);
        cabinClassId1.setAircraftConfigurationId(aircraftConfiguration4.getAircraftConfigurationId());
        final CabinClass cabinClass1 = this.cabinClassService.findById(cabinClassId1);
        cabinClassId1.setCabinClassType(CabinClassType.J);
        final CabinClass cabinClass2 = this.cabinClassService.findById(cabinClassId1);
        cabinClassId1.setCabinClassType(CabinClassType.Y);
        final CabinClass cabinClass3 = this.cabinClassService.findById(cabinClassId1);

        final FlightSchedulePlan flightSchedulePlan1 = this.flightSchedulePlanService.createRecurrentFlightSchedule(FlightSchedulePlanType.RECURRENT_WEEKLY,
                ml711,
                Date.valueOf("2020-12-1"),
                Time.valueOf("9:00:00"),
                Integer.valueOf(14 * 60).longValue(),
                Date.valueOf("2020-12-31"));
        this.fareService.create("F001", BigDecimal.valueOf(6500), cabinClass1, flightSchedulePlan1);
        this.fareService.create("F002", BigDecimal.valueOf(6000), cabinClass1, flightSchedulePlan1);
        this.fareService.create("J001", BigDecimal.valueOf(3500), cabinClass2, flightSchedulePlan1);
        this.fareService.create("J002", BigDecimal.valueOf(3000), cabinClass2, flightSchedulePlan1);
        this.fareService.create("Y001", BigDecimal.valueOf(1500), cabinClass3, flightSchedulePlan1);
        this.fareService.create("Y002", BigDecimal.valueOf(1000), cabinClass3, flightSchedulePlan1);
        final FlightSchedulePlan flightSchedulePlan2 = this.flightSchedulePlanService.createRecurrentFlightSchedule(FlightSchedulePlanType.RECURRENT_WEEKLY,
                ml712,
                Date.valueOf("2020-12-2"),
                Time.valueOf("1:00:00"),
                Integer.valueOf(14 * 60).longValue(),
                Date.valueOf("2020-12-31"));
        this.fareService.create("F001", BigDecimal.valueOf(6500), cabinClass1, flightSchedulePlan2);
        this.fareService.create("F002", BigDecimal.valueOf(6000), cabinClass1, flightSchedulePlan2);
        this.fareService.create("J001", BigDecimal.valueOf(3500), cabinClass2, flightSchedulePlan2);
        this.fareService.create("J002", BigDecimal.valueOf(3000), cabinClass2, flightSchedulePlan2);
        this.fareService.create("Y001", BigDecimal.valueOf(1500), cabinClass3, flightSchedulePlan2);
        this.fareService.create("Y002", BigDecimal.valueOf(1000), cabinClass3, flightSchedulePlan2);
    }
}
