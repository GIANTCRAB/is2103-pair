package controllers;

import entities.Employee;
import entities.EmployeeRole;
import entities.Flight;
import entities.FlightSchedule;
import entities.FlightSchedulePlan;
import entities.FlightSchedulePlanType;
import exceptions.IncorrectCredentialsException;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateful;
import javax.inject.Inject;

import services.AuthService;
import services.FlightScheduleService;
import services.FlightSchedulePlanService;
import services.FlightService;

@Stateful
public class FlightSchedulePlanSessionBean implements FlightSchedulePlanBeanRemote {
    private Employee loggedInEmployee = null;
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
    public Employee login(String username, String password) throws IncorrectCredentialsException, InvalidEntityIdException {
        final Employee employee = this.authService.employeeLogin(username, password);

        if (employee.getEmployeeRole().equals(PERMISSION_REQUIRED)) {
            this.loggedInEmployee = employee;
            return employee;
        } else {
            throw new InvalidEntityIdException();
        }
    }

    @Override
    public FlightSchedulePlan create(FlightSchedulePlanType flightSchedulePlanType, List<FlightSchedule> flightSchedules) throws NotAuthenticatedException, InvalidConstraintException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }

        return this.flightSchedulePlanService.create(flightSchedulePlanType, flightSchedules);
    }

    @Override
    public FlightSchedule createFlightSchedule(String flightCode, Date departureDate, Time departureTime, Long estimatedDuration) throws NotAuthenticatedException, InvalidConstraintException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }
        Flight flight = this.flightService.getFlightByFlightCode(flightCode);

        return this.flightScheduleService.create(flight, departureDate, departureTime, estimatedDuration);
    }

    @Override
    public List<FlightSchedule> createRecurrentFlightSchedule(String flightCode, Date departureDate, Time departureTime, Long estimatedDuration, Date recurrentEndDate, int nDays) throws NotAuthenticatedException, InvalidConstraintException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }
        List<FlightSchedule> flightSchedules = new ArrayList<>();
        for (LocalDate date = departureDate.toLocalDate(); date.isBefore(recurrentEndDate.toLocalDate()); date = date.plusDays(nDays)) {
            Date sqlDate = Date.valueOf(date);
            flightSchedules.add(this.createFlightSchedule(flightCode, sqlDate, departureTime, estimatedDuration));
        }
        return flightSchedules;
    }

    @Override
    public List<FlightSchedulePlan> getFlightSchedulePlans() throws NotAuthenticatedException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }

        return this.flightSchedulePlanService.getFlightSchedulePlans();
    }

    @Override
    public FlightSchedulePlan getFlightSchedulePlanById(Long id) throws NotAuthenticatedException, InvalidEntityIdException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }

        FlightSchedulePlan flightSchedulePlan = this.flightSchedulePlanService.getFlightSchedulePlanById(id);

        if (flightSchedulePlan == null) {
            throw new InvalidEntityIdException();
        }
        return flightSchedulePlan;
    }

    @Override
    public List<FlightSchedule> getFlightSchedules() throws NotAuthenticatedException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }

        return this.flightScheduleService.getFlightSchedules();
    }

    @Override
    public List<FlightSchedule> getFlightSchedulesByDate(Date startDate, Date endDate) throws NotAuthenticatedException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }

        return this.flightScheduleService.getFlightSchedulesByDate(startDate, endDate);
    }
}
