package controllers;

import entities.*;
import exceptions.*;
import services.AircraftConfigurationService;
import services.AircraftTypeService;
import services.AuthService;
import services.CabinClassService;

import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Stateful
public class AircraftConfigurationSessionBean implements AircraftConfigurationBeanRemote {
    private Employee loggedInEmployee = null;
    @Inject
    AuthService authService;
    @Inject
    AircraftConfigurationService aircraftConfigurationService;
    @Inject
    AircraftTypeService aircraftTypeService;
    @Inject
    CabinClassService cabinClassService;

    private final EmployeeRole PERMISSION_REQUIRED = EmployeeRole.FLEET_MANAGER;

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
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AircraftConfiguration createConfiguration(String aircraftConfigurationName,
                                                     Long aircraftTypeId,
                                                     List<CabinClass> cabinClassList) throws NotAuthenticatedException, InvalidConstraintException, InvalidEntityIdException, MaximumCapacityExceededException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }

        // Create base empty aircraft configuration
        final AircraftType aircraftType = this.aircraftTypeService.findById(aircraftTypeId);
        final AircraftConfiguration aircraftConfiguration = this.aircraftConfigurationService.create(aircraftConfigurationName, aircraftType);

        // These are managed entities
        final List<CabinClass> managedCabinClassList = new ArrayList<>();

        for (CabinClass cabinClass : cabinClassList) {
            managedCabinClassList.add(this.cabinClassService.create(cabinClass, aircraftConfiguration));
        }

        this.aircraftConfigurationService.checkMaxCapacity(aircraftType, managedCabinClassList);
        return this.aircraftConfigurationService.associateWithCabinClass(aircraftConfiguration, managedCabinClassList);
    }

    @Override
    public List<Object[]> getAircraftConfigurations() throws NotAuthenticatedException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }

        return this.aircraftConfigurationService.getAircraftConfigurations();
    }

    @Override
    public AircraftConfiguration getAircraftConfigurationById(Long id) throws NotAuthenticatedException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }

        return this.aircraftConfigurationService.getAircraftConfigurationById(id);
    }

    @Override
    public AircraftConfiguration getAircraftConfigurationByName(String name) throws NotAuthenticatedException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }

        return this.aircraftConfigurationService.getAircraftConfigurationByName(name);
    }
}
