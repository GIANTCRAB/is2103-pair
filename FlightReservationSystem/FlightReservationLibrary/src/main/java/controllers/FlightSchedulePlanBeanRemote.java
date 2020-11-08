package controllers;

import entities.Employee;
import entities.Fare;
import entities.FlightSchedule;
import entities.FlightSchedulePlan;
import entities.FlightSchedulePlanType;
import exceptions.InvalidConstraintException;
import exceptions.NotAuthenticatedException;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import javax.ejb.Remote;

@Remote
public interface FlightSchedulePlanBeanRemote {
    FlightSchedulePlan create(Employee employee, FlightSchedulePlanType flightSchedulePlanType, Date recurrentEndDate) throws NotAuthenticatedException, InvalidConstraintException;
    FlightSchedule createFlightSchedule(Employee employee, String flightCode, Date departureDate, Time departureTime, Long estimatedDuration, List<Fare> fares) throws NotAuthenticatedException, InvalidConstraintException;
    void associateFlightSchedules(Employee employee, FlightSchedulePlan flightSchedulePlan, List<FlightSchedule> flightSchedules) throws NotAuthenticatedException, InvalidConstraintException;
}
