package controllers;

import entities.Employee;
import entities.Fare;
import entities.CabinClass;
import entities.FlightSchedulePlan;
import exceptions.InvalidConstraintException;
import exceptions.NotAuthenticatedException;
import javax.ejb.Remote;
import java.math.BigDecimal;
import java.util.List;

@Remote
public interface FareBeanRemote {
    
    Fare create(Employee employee, String fareBasisCode, BigDecimal fareAmount, CabinClass cabinClass, FlightSchedulePlan flightSchedulePlan) throws NotAuthenticatedException, InvalidConstraintException;

}
