package controllers;

import entities.*;
import entities.EmployeeRole;
import exceptions.InvalidConstraintException;
import exceptions.NotAuthenticatedException;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.Stateful;
import javax.inject.Inject;
import services.AuthService;
import services.FareService;

@Stateful
public class FareSessionBean implements FareBeanRemote {

    @Inject
    FareService fareService;

    @Inject
    AuthService authService;

    private final EmployeeRole PERMISSION_REQUIRED = EmployeeRole.SCHEDULE_MANAGER;
    
    @Override
    public Fare create(Employee employee, String fareBasisCode, BigDecimal fareAmount, CabinClass cabinClass, FlightSchedulePlan flightSchedulePlan) throws NotAuthenticatedException, InvalidConstraintException {
        this.authService.checkPermission(employee, this.PERMISSION_REQUIRED);
        
        return this.fareService.create(fareBasisCode, fareAmount, cabinClass, flightSchedulePlan);
    }
    
}
