package controllers;

import entities.AircraftConfiguration;
import entities.CabinClass;
import entities.Employee;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;
import exceptions.MaximumCapacityExceededException;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface AircraftConfigurationBeanRemote {
    AircraftConfiguration createConfiguration(Employee employee,
                                              String aircraftConfigurationName,
                                              Long aircraftTypeId,
                                              List<CabinClass> cabinClassList) throws NotAuthenticatedException, InvalidConstraintException, InvalidEntityIdException, MaximumCapacityExceededException;
    
    public List<Object[]> getAircraftConfigurations(Employee employee) throws NotAuthenticatedException;
    
    public AircraftConfiguration getAircraftConfigurationById(Employee employee, Long id) throws NotAuthenticatedException;
    
    public AircraftConfiguration getAircraftConfigurationByName(Employee employee, String name) throws NotAuthenticatedException; 
}
