package controllers;

import entities.Employee;
import entities.EmployeeRole;
import exceptions.NotAuthenticatedException;
import java.util.List;
import javax.ejb.Remote;

@Remote
public interface AdminBeanRemote {
    
    public Employee create(Employee employee, EmployeeRole employeeRole, String firstName, String lastName, String password, String username) throws NotAuthenticatedException;
    
    public List<Employee> getEmployees(Employee employee) throws NotAuthenticatedException;
}
