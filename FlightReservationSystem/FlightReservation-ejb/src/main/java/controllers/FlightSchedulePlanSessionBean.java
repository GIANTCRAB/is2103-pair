package controllers;

import entities.Employee;
import entities.EmployeeRole;
import entities.Fare;
import entities.Flight;
import entities.FlightSchedule;
import entities.FlightSchedulePlan;
import entities.FlightSchedulePlanType;
import exceptions.InvalidConstraintException;
import exceptions.NotAuthenticatedException;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import javax.ejb.Stateful;
import javax.inject.Inject;
import services.AuthService;
import services.FlightScheduleService;
import services.FlightSchedulePlanService;
import services.FlightService;

@Stateful
public class FlightSchedulePlanSessionBean implements FlightSchedulePlanBeanRemote {
    
    @Inject
    FlightService flightService;
    
    @Inject
    FlightScheduleService flightScheduleService;

    @Inject
    FlightSchedulePlanService flightSchedulePlanService; 
    
    @Inject
    AuthService authService;

    private final EmployeeRole PERMISSION_REQUIRED = EmployeeRole.SCHEDULE_MANAGER;
    
    @Override
    public FlightSchedulePlan create(Employee employee, FlightSchedulePlanType flightSchedulePlanType, Date recurrentEndDate) throws NotAuthenticatedException, InvalidConstraintException {
        this.authService.checkPermission(employee, this.PERMISSION_REQUIRED);
        
        return this.flightSchedulePlanService.create(flightSchedulePlanType, recurrentEndDate);
    }
    
    @Override
    public FlightSchedule createFlightSchedule(Employee employee, String flightCode, Date departureDate, Time departureTime, Long estimatedDuration, List<Fare> fares) throws NotAuthenticatedException, InvalidConstraintException {
        this.authService.checkPermission(employee, this.PERMISSION_REQUIRED);
        
        Flight flight = this.flightService.getFlightByFlightCode(flightCode);
        
        return this.flightScheduleService.create(flight, flightCode, departureDate, departureTime, estimatedDuration);
    }
        
    @Override
    public void associateFlightSchedules(Employee employee, FlightSchedulePlan flightSchedulePlan, List<FlightSchedule> flightSchedules) throws NotAuthenticatedException, InvalidConstraintException {
        this.authService.checkPermission(employee, this.PERMISSION_REQUIRED);
        
        this.flightSchedulePlanService.associateFlightSchedules(flightSchedulePlan, flightSchedules);
    }
}
