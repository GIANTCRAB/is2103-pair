package controllers;

import entities.Employee;
import exceptions.IncorrectCredentialsException;
import services.AuthService;

import javax.ejb.Stateful;
import javax.inject.Inject;

/**
 * The auth session bean holds zero knowledge about entity manager. The purpose of this bean to parse certain data and route the data to services
 */
@Stateful
public class EmployeeAuthSessionBean implements EmployeeAuthBeanRemote {
    @Inject
    AuthService authService;

    @Override
    public Employee login(String username, String password) throws IncorrectCredentialsException {
        return this.authService.employeeLogin(username, password);
    }
}
