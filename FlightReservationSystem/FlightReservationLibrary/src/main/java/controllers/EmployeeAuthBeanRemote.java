package controllers;

import entities.Employee;
import exceptions.IncorrectCredentialsException;

import javax.ejb.Remote;

@Remote
public interface EmployeeAuthBeanRemote {
    Employee login(String username, String password) throws IncorrectCredentialsException;
}
