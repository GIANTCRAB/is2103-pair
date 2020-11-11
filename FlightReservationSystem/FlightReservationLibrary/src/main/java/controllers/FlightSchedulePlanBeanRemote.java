package controllers;

import entities.Employee;
import entities.FlightSchedule;
import entities.FlightSchedulePlan;
import entities.FlightSchedulePlanType;
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

    FlightSchedule createFlightSchedule(String flightCode, Date departureDate, Time departureTime, Long estimatedDuration) throws NotAuthenticatedException, InvalidConstraintException;

    List<FlightSchedule> createRecurrentFlightSchedule(String flightCode, Date departureDate, Time departureTime, Long estimatedDuration, Date recurrentEndDate, int nDays) throws NotAuthenticatedException, InvalidConstraintException;

    FlightSchedulePlan getFlightSchedulePlanById(Long id) throws NotAuthenticatedException, InvalidEntityIdException;

    List<FlightSchedulePlan> getFlightSchedulePlans() throws NotAuthenticatedException;

    List<FlightSchedule> getFlightSchedules() throws NotAuthenticatedException;

    List<FlightSchedule> getFlightSchedulesByDate(Date startDate, Date endDate) throws NotAuthenticatedException;
    
    void updateFlight(Long flightSchedulePlanId, String newFlightCode) throws NotAuthenticatedException, InvalidEntityIdException;

    String deleteFlightSchedulePlan(Long flightSchedulePlanId) throws NotAuthenticatedException, InvalidEntityIdException;
}
