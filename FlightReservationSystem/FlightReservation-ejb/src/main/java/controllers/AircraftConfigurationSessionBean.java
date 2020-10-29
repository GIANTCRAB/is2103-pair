package controllers;

import entities.*;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;
import exceptions.MaximumCapacityExceededException;
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
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AircraftConfiguration createConfiguration(Employee employee,
                                                     String aircraftConfigurationName,
                                                     Long aircraftTypeId,
                                                     List<CabinClass> cabinClassList) throws NotAuthenticatedException, InvalidConstraintException, InvalidEntityIdException, MaximumCapacityExceededException {
        this.authService.checkPermission(employee, this.PERMISSION_REQUIRED);

        // Create base empty aircraft configuration
        final AircraftType aircraftType = this.aircraftTypeService.findById(aircraftTypeId);
        final AircraftConfiguration aircraftConfiguration = this.aircraftConfigurationService.create(aircraftConfigurationName, aircraftType);

        // These are managed entities
        final List<CabinClass> managedCabinClassList = new ArrayList<>();
                
        for (CabinClass cabinClass : cabinClassList) {
            managedCabinClassList.add(this.cabinClassService.create(cabinClass, aircraftConfiguration));
        }
        
        // Check if max capacity of AircraftType is exceeded
        int totalCapacity = managedCabinClassList.stream().map(c -> c.getMaxCapacity()).mapToInt(Integer::intValue).sum();
        
        if (totalCapacity > aircraftType.getMaxCapacity()) {
            throw new MaximumCapacityExceededException("Maximum seat capacity exceeded!");
        } else {
            return this.aircraftConfigurationService.associateWithCabinClass(aircraftConfiguration, managedCabinClassList);
        }
    }
    
    @Override
    public List<Object[]> getAircraftConfigurations(Employee employee) throws NotAuthenticatedException {
        this.authService.checkPermission(employee, this.PERMISSION_REQUIRED);

        return this.aircraftConfigurationService.getAircraftConfigurations();
    }
    
    @Override
    public AircraftConfiguration getAircraftConfigurationById(Employee employee, Long id) throws NotAuthenticatedException {
        this.authService.checkPermission(employee, this.PERMISSION_REQUIRED);

        return this.aircraftConfigurationService.getAircraftConfigurationById(id);
    }
    
    @Override
    public AircraftConfiguration getAircraftConfigurationByName(Employee employee, String name) throws NotAuthenticatedException {
        this.authService.checkPermission(employee, this.PERMISSION_REQUIRED);

        return this.aircraftConfigurationService.getAircraftConfigurationByName(name);
    }
}
