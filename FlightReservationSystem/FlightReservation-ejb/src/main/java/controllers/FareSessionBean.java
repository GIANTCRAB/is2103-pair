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
import services.CabinClassService;
import services.FareService;
import services.FlightSchedulePlanService;

@Stateful
public class FareSessionBean implements FareBeanRemote {
    private Employee loggedInEmployee = null;
    @Inject
    FareService fareService;
    @Inject
    AuthService authService;
    @Inject
    CabinClassService cabinClassService;
    @Inject
    FlightSchedulePlanService flightSchedulePlanService;

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
    public Fare create(String fareBasisCode, BigDecimal fareAmount, CabinClass cabinClass, FlightSchedulePlan flightSchedulePlan) throws NotAuthenticatedException, InvalidConstraintException, InvalidEntityIdException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }

        final CabinClass managedCabinClass = this.cabinClassService.findById(cabinClass.getCabinClassId());
        final FlightSchedulePlan managedFlightSchedulePlan = this.flightSchedulePlanService.getFlightSchedulePlanById(flightSchedulePlan.getFlightSchedulePlanId());

        return this.fareService.create(fareBasisCode, fareAmount, managedCabinClass, managedFlightSchedulePlan);
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
