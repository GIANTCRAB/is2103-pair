package controllers;

import entities.*;
import entities.EmployeeRole;
import exceptions.IncorrectCredentialsException;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.Stateful;
import javax.inject.Inject;
import services.AuthService;
import services.FareService;

@Stateful
public class FareSessionBean implements FareBeanRemote {
    private Employee loggedInEmployee = null;
    @Inject
    FareService fareService;
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
    public Fare create(String fareBasisCode, BigDecimal fareAmount, CabinClass cabinClass, FlightSchedulePlan flightSchedulePlan) throws NotAuthenticatedException, InvalidConstraintException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }
        
        return this.fareService.create(fareBasisCode, fareAmount, cabinClass, flightSchedulePlan);
    }
    
    @Override
    public void delete(Fare fare) throws NotAuthenticatedException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }
        this.fareService.delete(fare);
    }

    @Override
    public void logout() throws NotAuthenticatedException {
        if (loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }

        loggedInEmployee = null;
    }
}
