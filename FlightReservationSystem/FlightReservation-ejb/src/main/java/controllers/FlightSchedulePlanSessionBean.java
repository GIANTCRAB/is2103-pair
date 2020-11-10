package controllers;

import entities.Employee;
import entities.EmployeeRole;
import entities.Flight;
import entities.FlightSchedule;
import entities.FlightSchedulePlan;
import entities.FlightSchedulePlanType;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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
    public FlightSchedulePlan create(Employee employee, FlightSchedulePlanType flightSchedulePlanType, List<FlightSchedule> flightSchedules) throws NotAuthenticatedException, InvalidConstraintException {
        this.authService.checkPermission(employee, this.PERMISSION_REQUIRED);
        
        return this.flightSchedulePlanService.create(flightSchedulePlanType, flightSchedules);
    }
    
    @Override
    public FlightSchedule createFlightSchedule(Employee employee, String flightCode, Date departureDate, Time departureTime, Long estimatedDuration) throws NotAuthenticatedException, InvalidConstraintException {
        this.authService.checkPermission(employee, this.PERMISSION_REQUIRED);
        
        Flight flight = this.flightService.getFlightByFlightCode(flightCode);
        
        return this.flightScheduleService.create(flight, departureDate, departureTime, estimatedDuration);
    }
    
    @Override
    public List<FlightSchedule> createRecurrentFlightSchedule(Employee employee, String flightCode, Date departureDate, Time departureTime, Long estimatedDuration, Date recurrentEndDate, int nDays) throws NotAuthenticatedException, InvalidConstraintException {
        this.authService.checkPermission(employee, this.PERMISSION_REQUIRED);
        
        List<FlightSchedule> flightSchedules = new ArrayList<>();
        for (LocalDate date = departureDate.toLocalDate(); date.isBefore(recurrentEndDate.toLocalDate()); date = date.plusDays(nDays)) {
            Date sqlDate = Date.valueOf(date);
            flightSchedules.add(this.createFlightSchedule(employee, flightCode, sqlDate, departureTime, estimatedDuration));
        }
        return flightSchedules;
    }
           
    @Override
    public List<FlightSchedulePlan> getFlightSchedulePlans(Employee employee) throws NotAuthenticatedException {
        this.authService.checkPermission(employee, this.PERMISSION_REQUIRED);
        
        return this.flightSchedulePlanService.getFlightSchedulePlans();
    }
    
    @Override
    public FlightSchedulePlan getFlightSchedulePlanById(Employee employee, Long id) throws NotAuthenticatedException, InvalidEntityIdException {
        this.authService.checkPermission(employee, this.PERMISSION_REQUIRED);
        
        FlightSchedulePlan flightSchedulePlan = this.flightSchedulePlanService.getFlightSchedulePlanById(id);
        
        if (flightSchedulePlan == null) {
            throw new InvalidEntityIdException();
        }
        return flightSchedulePlan;
    }
    
    @Override
    public List<FlightSchedule> getFlightSchedules(Employee employee) throws NotAuthenticatedException {
        this.authService.checkPermission(employee, this.PERMISSION_REQUIRED);
        
        return this.flightScheduleService.getFlightSchedules();
    }
    
    @Override
    public List<FlightSchedule> getFlightSchedulesByDate(Employee employee, Date startDate, Date endDate) throws NotAuthenticatedException {
        this.authService.checkPermission(employee, this.PERMISSION_REQUIRED);
        
        return this.flightScheduleService.getFlightSchedulesByDate(startDate, endDate);
    } 
}
