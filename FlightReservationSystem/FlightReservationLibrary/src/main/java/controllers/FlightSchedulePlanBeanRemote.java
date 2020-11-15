package controllers;

import entities.*;
import exceptions.EntityAlreadyExistException;
import exceptions.EntityInUseException;
import exceptions.EntityIsDisabledException;
import exceptions.IncorrectCredentialsException;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;
import lombok.NonNull;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
import javax.ejb.Remote;

@Remote
public interface FlightSchedulePlanBeanRemote {
    Employee login(String username, String password) throws IncorrectCredentialsException, InvalidEntityIdException;

    FlightSchedule createFlightSchedule(String flightCode, FlightSchedulePlan flightSchedulePlan, Date departureDate, Time departureTime, Long estimatedDuration) throws NotAuthenticatedException, InvalidConstraintException, EntityIsDisabledException, InvalidEntityIdException, EntityAlreadyExistException;

    FlightSchedulePlan createRecurrentFlightSchedule(@NonNull FlightSchedulePlanType flightSchedulePlanType,
                                                     @NonNull Flight flight,
                                                     @NonNull Date departureDate,
                                                     @NonNull Time departureTime,
                                                     @NonNull Long estimatedDuration,
                                                     @NonNull Date recurrentEndDate,
                                                     Integer nDays) throws InvalidConstraintException, InvalidEntityIdException, NotAuthenticatedException;

    FlightSchedulePlan createFlightSchedulePlanAndFlightSchedule(@NonNull FlightSchedule flightScheduleDraft) throws InvalidConstraintException, NotAuthenticatedException, InvalidEntityIdException;

    FlightSchedulePlan createFlightSchedulePlanAndFlightSchedule(@NonNull FlightSchedulePlanType flightSchedulePlanType,
                                                                 @NonNull List<FlightSchedule> flightSchedulesDraft) throws InvalidConstraintException, NotAuthenticatedException, InvalidEntityIdException;

    FlightSchedulePlan getFlightSchedulePlanById(Long id) throws NotAuthenticatedException, InvalidEntityIdException;

    List<FlightSchedulePlan> getFlightSchedulePlans() throws NotAuthenticatedException;

    List<FlightSchedule> getFlightSchedules() throws NotAuthenticatedException;
    
    void updateFares(List<Fare> fares) throws NotAuthenticatedException;
    
    void updateFlightSchedules(List<FlightSchedule> flightSchedules) throws NotAuthenticatedException;
    
    void deleteFlightSchedule(FlightSchedulePlan flightSchedulePlan, FlightSchedule flightSchedule) throws NotAuthenticatedException, EntityInUseException;

    String deleteFlightSchedulePlan(Long flightSchedulePlanId) throws NotAuthenticatedException, InvalidEntityIdException;

    void logout() throws NotAuthenticatedException;
}
