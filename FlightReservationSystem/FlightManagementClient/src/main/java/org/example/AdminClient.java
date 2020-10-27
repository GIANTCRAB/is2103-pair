package org.example;

import entities.EmployeeRole;
import entities.Employee;
import exceptions.NotAuthenticatedException;
import controllers.AdminBeanRemote;
import java.util.Scanner;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdminClient implements SystemClient {
    @NonNull
    private final Scanner scanner;
    @NonNull
    private final Employee authenticatedEmployee;
    @NonNull
    private final AdminBeanRemote adminBeanRemote;
    
    @Override
    public void runApp() {
        this.displayAdminMenu();
    }
    
    private void displayAdminMenu() {
        boolean loop = true;
        while (loop) {
            System.out.println("*** System Administrator ***");
            System.out.println("1: Create New Employee");
            System.out.println("2: View All Employees");
            System.out.println("3: Exit");

            final int option = this.scanner.nextInt();

            switch (option) {
                case 1:
                    displayCreateNewEmployeeMenu();
                    break;
                case 2:
                    displayEmployeeMenu();
                    break;
                default:
                    System.out.println("Exiting...");
                    loop = false;
                    break;
            }
        }
        
    }
    
     private void displayCreateNewEmployeeMenu() {
        System.out.println("*** Create New Employee ***");
        System.out.println("Enter employee role:");
        System.out.println("(Type 1 for Fleet Manager, 2 for Route Planner, 3 for Schedule Manager, 4 for Sales Manager)");
        final int employeeRole = this.scanner.nextInt();
        System.out.println("Enter first name:");
        final String firstName = this.scanner.next();
        System.out.println("Enter last name:");
        final String lastName = this.scanner.next();
        System.out.println("Enter username:");
        final String username = this.scanner.next();
        System.out.println("Enter password:");
        final String password = this.scanner.next();
        
        try {
            Employee newEmployee;
            switch (employeeRole) {
                case 1:
                    newEmployee = this.adminBeanRemote.create(authenticatedEmployee, EmployeeRole.FLEET_MANAGER, firstName, lastName, password, username);
                    break;
                case 3:
                    newEmployee = this.adminBeanRemote.create(authenticatedEmployee, EmployeeRole.SCHEDULE_MANAGER, firstName, lastName, password, username);
                    break;
                case 4:
                    newEmployee = this.adminBeanRemote.create(authenticatedEmployee, EmployeeRole.SALES_MANAGER, firstName, lastName, password, username);
                    break;
                default:
                    newEmployee = this.adminBeanRemote.create(authenticatedEmployee, EmployeeRole.ROUTE_PLANNER, firstName, lastName, password, username);
                    break;
            }
            System.out.println("Employee " + firstName + " " + lastName + " created successfully.");
            
        } catch (NotAuthenticatedException e) {
            System.out.println("You do not have permission to do this!");
        }
     }
     
     private void displayEmployeeMenu() {
        System.out.println("*** View All Employees ***");

        try {
            final List<Employee> employeeList = this.adminBeanRemote.getEmployees(this.authenticatedEmployee);
            employeeList.forEach(employee -> System.out.println(employee.getEmployeeId() + "." + employee.getFirstName() + " " + employee.getLastName() + " " + employee.getEmployeeRole()));
        } catch (NotAuthenticatedException e) {
            System.out.println("You do not have permission to do this!");
        }
    }
}
