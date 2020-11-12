package controllers;

import entities.AircraftConfiguration;
import entities.CabinClass;
import entities.Employee;
import exceptions.*;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface AircraftConfigurationBeanRemote {
    Employee login(String username, String password) throws IncorrectCredentialsException, InvalidEntityIdException;

    AircraftConfiguration createConfiguration(String aircraftConfigurationName,
                                              Long aircraftTypeId,
                                              List<CabinClass> cabinClassList) throws NotAuthenticatedException, InvalidConstraintException, InvalidEntityIdException, MaximumCapacityExceededException;

    List<Object[]> getAircraftConfigurations() throws NotAuthenticatedException;

    AircraftConfiguration getAircraftConfigurationById(Long id) throws NotAuthenticatedException;

    AircraftConfiguration getAircraftConfigurationByName(String name) throws NotAuthenticatedException;

    void logout() throws NotAuthenticatedException;
}
