package controllers;

import entities.Employee;
import entities.Fare;
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
import java.util.List;
import javax.ejb.Remote;

@Remote
public interface FlightSchedulePlanBeanRemote {
    Employee login(String username, String password) throws IncorrectCredentialsException, InvalidEntityIdException;

    FlightSchedulePlan create(FlightSchedulePlanType flightSchedulePlanType, List<FlightSchedule> flightSchedules) throws NotAuthenticatedException, InvalidConstraintException;

    FlightSchedule createFlightSchedule(String flightCode, Date departureDate, Time departureTime, Long estimatedDuration) throws NotAuthenticatedException, InvalidConstraintException, EntityIsDisabledException, InvalidEntityIdException, EntityAlreadyExistException;

    List<FlightSchedule> createRecurrentFlightSchedule(String flightCode, Date departureDate, Time departureTime, Long estimatedDuration, Date recurrentEndDate, int nDays) throws NotAuthenticatedException, InvalidConstraintException, EntityIsDisabledException, InvalidEntityIdException, EntityAlreadyExistException;

    FlightSchedulePlan getFlightSchedulePlanById(Long id) throws NotAuthenticatedException, InvalidEntityIdException;

    List<FlightSchedulePlan> getFlightSchedulePlans() throws NotAuthenticatedException;

    List<FlightSchedule> getFlightSchedules() throws NotAuthenticatedException;
    
    void updateFares(List<Fare> fares) throws NotAuthenticatedException;
    
    void updateFlightSchedules(List<FlightSchedule> flightSchedules) throws NotAuthenticatedException;
    
    void addFlightSchedules(FlightSchedulePlan flightSchedulePlan, List<FlightSchedule> flightSchedules) throws NotAuthenticatedException;
    
    void deleteFlightSchedule(FlightSchedulePlan flightSchedulePlan, FlightSchedule flightSchedule) throws NotAuthenticatedException, EntityInUseException;

    String deleteFlightSchedulePlan(Long flightSchedulePlanId) throws NotAuthenticatedException, InvalidEntityIdException;

    void logout() throws NotAuthenticatedException;
}
