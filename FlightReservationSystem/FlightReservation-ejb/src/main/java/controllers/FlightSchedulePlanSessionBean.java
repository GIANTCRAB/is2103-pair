package controllers;

import entities.Employee;
import entities.EmployeeRole;
import entities.Fare;
import entities.Flight;
import entities.FlightSchedule;
import entities.FlightSchedulePlan;
import entities.FlightSchedulePlanType;
import exceptions.EntityAlreadyExistException;
import exceptions.EntityInUseException;
import exceptions.EntityIsDisabledException;
import exceptions.IncorrectCredentialsException;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import services.AuthService;
import services.FareService;
import services.FlightScheduleService;
import services.FlightSchedulePlanService;
import services.FlightService;

@Stateful
public class FlightSchedulePlanSessionBean implements FlightSchedulePlanBeanRemote {
    private Employee loggedInEmployee = null;
    @Inject
    FareService fareService;
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
    public FlightSchedule createFlightSchedule(String flightCode, Date departureDate, Time departureTime, Long estimatedDuration) throws NotAuthenticatedException, InvalidConstraintException, EntityIsDisabledException, InvalidEntityIdException, EntityAlreadyExistException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }
        
        Flight flight = this.flightService.getFlightByFlightCode(flightCode);
        if (flight == null) {
            throw new InvalidEntityIdException("Flight does not exist.");
        }
        if (!flight.getEnabled()) {
            throw new EntityIsDisabledException("Selected flight is disabled.");
        }
        if (!checkExistingFlightSchedules(flightCode, departureDate, departureTime, estimatedDuration)) {
            throw new EntityAlreadyExistException("An overlapping flight schedule already exists.");
        }
        
        return this.flightScheduleService.create(flight, departureDate, departureTime, estimatedDuration);
    }

    @Override
    public List<FlightSchedule> createRecurrentFlightSchedule(String flightCode, Date departureDate, Time departureTime, Long estimatedDuration, Date recurrentEndDate, int nDays) throws NotAuthenticatedException, InvalidConstraintException, EntityIsDisabledException, InvalidEntityIdException, EntityAlreadyExistException {
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

        return sortFlightSchedulePlans(this.flightSchedulePlanService.getFlightSchedulePlans());
    }

    @Override
    public FlightSchedulePlan getFlightSchedulePlanById(Long id) throws NotAuthenticatedException, InvalidEntityIdException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }

        FlightSchedulePlan flightSchedulePlan = this.flightSchedulePlanService.getFlightSchedulePlanById(id);

        if (flightSchedulePlan == null) {
            throw new InvalidEntityIdException("Invalid flight schedule plan ID.");
        }
        return flightSchedulePlan;
    }
   
    private FlightSchedulePlan getDirectReturnFlightSchedulePlan(FlightSchedulePlan flightSchedulePlan) throws NotAuthenticatedException {
        String origin = flightSchedulePlan.getFlightSchedules().get(0).getFlight().getFlightRoute().getOrigin().getIataCode();
        String dest = flightSchedulePlan.getFlightSchedules().get(0).getFlight().getFlightRoute().getDest().getIataCode();
        try {
            Flight returnFlight = this.flightService.getFlightByOriginDest(dest, origin);
            Date date = flightSchedulePlan.getFlightSchedules().get(0).getDate();
            Time time = flightSchedulePlan.getFlightSchedules().get(0).getTime();
            FlightSchedulePlan returnFlightSchedulePlan = this.flightSchedulePlanService.getFlightSchedulePlansByFlightCodeAndDateTime(returnFlight.getFlightCode(), date, time).get(0);
            return returnFlightSchedulePlan;
        } catch (NoResultException e) {
            return null;
        }
    }
    
    private List<FlightSchedulePlan> sortFlightSchedulePlans(List<FlightSchedulePlan> flightSchedulePlans) throws NotAuthenticatedException {
        List<FlightSchedulePlan> sortedFlightSchedulePlans = new ArrayList<>();
        for (FlightSchedulePlan flightSchedulePlan : flightSchedulePlans) {
            if(!sortedFlightSchedulePlans.contains(flightSchedulePlan)) {
                sortedFlightSchedulePlans.add(flightSchedulePlan);
            }
            
            FlightSchedulePlan returnFlightSchedulePlan = getDirectReturnFlightSchedulePlan(flightSchedulePlan);

            if(returnFlightSchedulePlan != null && !sortedFlightSchedulePlans.contains(returnFlightSchedulePlan)) {
                sortedFlightSchedulePlans.add(returnFlightSchedulePlan);
            }
        }
        return sortedFlightSchedulePlans;
    }

    @Override
    public List<FlightSchedule> getFlightSchedules() throws NotAuthenticatedException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }

        return this.flightScheduleService.getFlightSchedules();
    }
    
    @Override
    public void updateFares(List<Fare> fares) throws NotAuthenticatedException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }
        this.fareService.updateFares(fares);
    }
    
    @Override
    public void updateFlightSchedules(List<FlightSchedule> flightSchedules) throws NotAuthenticatedException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }
        this.flightScheduleService.updateFlightSchedules(flightSchedules);
    }
    
    @Override
    public void addFlightSchedules(FlightSchedulePlan flightSchedulePlan, List<FlightSchedule> flightSchedules) throws NotAuthenticatedException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }
        this.flightSchedulePlanService.addFlightSchedules(flightSchedulePlan, flightSchedules);
    }
    
    @Override
    public void deleteFlightSchedule(FlightSchedulePlan flightSchedulePlan, FlightSchedule flightSchedule) throws NotAuthenticatedException, EntityInUseException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }
        
        if (flightSchedule.getFlightReservations().isEmpty() && flightSchedulePlan.getFlightSchedules().size() > 1) {
            this.flightScheduleService.deleteFlightSchedule(flightSchedule);
        } else {
            throw new EntityInUseException("Flight schedule is in use and cannot be deleted!");
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public String deleteFlightSchedulePlan(Long flightSchedulePlanId) throws NotAuthenticatedException, InvalidEntityIdException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }
        FlightSchedulePlan flightSchedulePlan = this.flightSchedulePlanService.getFlightSchedulePlanById(flightSchedulePlanId);

        if (flightSchedulePlan == null) {
            throw new InvalidEntityIdException("Invalid flight schedule plan ID.");
        }

        String msg = "";
        if (canDeleteFlightSchedulePlan(flightSchedulePlan)) {
            flightSchedulePlan.getFlightSchedules().forEach(flightSchedule -> this.flightScheduleService.deleteFlightSchedule(flightSchedule));
            flightSchedulePlan.getFares().forEach(fare -> this.fareService.delete(fare));
            this.flightSchedulePlanService.deleteFlightSchedulePlan(flightSchedulePlan);
            
            msg = "Flight schedule plan successfully deleted.";
        } else {
            this.flightSchedulePlanService.disableFlightSchedulePlan(flightSchedulePlan);
            msg = "Flight schedule plan is in use, will be disabled instead.";
        }
        return msg;
    }
    
    private boolean canDeleteFlightSchedulePlan(FlightSchedulePlan flightSchedulePlan) {
        for (FlightSchedule flightSchedule : flightSchedulePlan.getFlightSchedules()) {
            if (!flightSchedule.getFlightReservations().isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    private boolean checkExistingFlightSchedules(String flightCode, Date departureDate, Time departureTime, Long estimatedDuration) {
        LocalDateTime arrivalDateTime = LocalDateTime.of(departureDate.toLocalDate(), departureTime.toLocalTime().plusMinutes(estimatedDuration));
        List<FlightSchedule> sameDateFlightSchedules = this.flightScheduleService.getFlightSchedulesByFlightAndDate(flightCode, departureDate, departureDate);
        for (FlightSchedule flightSchedule : sameDateFlightSchedules) {
             if (!flightSchedule.getArrivalDateTime().toLocalDateTime().isAfter(arrivalDateTime.plusHours(2))) {
                 return false;
             }
        }
        return true;
    }

    @Override
    public void logout() throws NotAuthenticatedException {
        if (loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }

        loggedInEmployee = null;
    }
}
