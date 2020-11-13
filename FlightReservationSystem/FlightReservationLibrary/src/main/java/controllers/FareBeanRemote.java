package controllers;

import entities.Employee;
import entities.Fare;
import entities.CabinClass;
import entities.FlightSchedulePlan;
import exceptions.IncorrectCredentialsException;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;

import javax.ejb.Remote;
import java.math.BigDecimal;

@Remote
public interface FareBeanRemote {
    Employee login(String username, String password) throws IncorrectCredentialsException, InvalidEntityIdException;

    Fare create(String fareBasisCode, BigDecimal fareAmount, CabinClass cabinClass, FlightSchedulePlan flightSchedulePlan) throws NotAuthenticatedException, InvalidConstraintException, InvalidEntityIdException;

    void delete(Fare fare) throws NotAuthenticatedException;

    void logout() throws NotAuthenticatedException;
}
