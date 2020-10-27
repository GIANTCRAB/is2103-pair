
package controllers;

import entities.Employee;
import entities.EmployeeRole;
import exceptions.NotAuthenticatedException;

import java.util.List;
import javax.ejb.Stateful;
import javax.inject.Inject;
import services.AuthService;
import services.AdminService;


@Stateful
public class AdminSessionBean implements AdminBeanRemote {
    @Inject
    AuthService authService;
    @Inject
    AdminService adminService;
    
    private final EmployeeRole PERMISSION_REQUIRED = EmployeeRole.SYSTEM_ADMIN;
    
    @Override
    public Employee create(Employee employee, EmployeeRole employeeRole, String firstName, String lastName, String password, String username) throws NotAuthenticatedException {
        this.authService.checkPermission(employee, this.PERMISSION_REQUIRED);
        return this.adminService.create(employeeRole, firstName, lastName, password, username);
    }
    
    @Override
    public List<Employee> getEmployees(Employee employee) throws NotAuthenticatedException {
        this.authService.checkPermission(employee, this.PERMISSION_REQUIRED);

        return this.adminService.getEmployees();
    }
}
