package org.example;

import controllers.AircraftConfigurationBeanRemote;
import controllers.EmployeeAuthBeanRemote;
import controllers.FlightRouteBeanRemote;
import controllers.FlightBeanRemote;
import entities.Employee;
import exceptions.IncorrectCredentialsException;
import exceptions.NotAuthenticatedException;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.validation.constraints.NotNull;
import java.util.Scanner;

@RequiredArgsConstructor
public class ManagementClient implements SystemClient {
    @NotNull
    private final InitialContext initialContext;
    @NonNull
    private final EmployeeAuthBeanRemote employeeAuthBeanRemote;

    @Setter(AccessLevel.PRIVATE)
    private Scanner scanner;
    @Setter(AccessLevel.PRIVATE)
    private Employee authenticatedEmployee;

    @Override
    public void runApp() {
        this.scanner = new Scanner(System.in);

        boolean loop = true;

        while (loop) {
            System.out.println("*** Flight Management Client ***");
            System.out.println("1: Employee Login");
            System.out.println("2: Exit");
            final int option = this.scanner.nextInt();

            if (option == 1) {
                this.displayEmployeeLoginMenu();
            } else {
                loop = false;
            }
        }

        this.scanner.close();
    }

    private void displayEmployeeLoginMenu() {
        boolean loginLoop = true;
        while (loginLoop) {
            System.out.println("Enter Username:");
            final String username = this.scanner.next();
            System.out.println("Enter Password:");
            final String password = this.scanner.next();
            try {
                this.authenticatedEmployee = this.employeeAuthBeanRemote.login(username, password);
                System.out.println("Logged in as " + this.authenticatedEmployee.getFirstName() + " (ID: " + this.authenticatedEmployee.getEmployeeId() + ")");
                System.out.println("Employee Role: " + this.getEmployeeRoleName());
                this.createSystemBasedOnRole().runApp();
                loginLoop = false;
            } catch (IncorrectCredentialsException e) {
                System.out.println("Incorrect credentials! Try again!");
            } catch (NamingException e) {
                System.out.println("Server error, please try again!");
            } catch (NotAuthenticatedException e) {
                System.out.println("Invalid role in system. Please try again");
                this.authenticatedEmployee = null;
            }
        }
    }

    private SystemClient createSystemBasedOnRole() throws NamingException, NotAuthenticatedException {
        if (this.authenticatedEmployee != null && this.authenticatedEmployee.getEmployeeRole() != null) {
            final FlightRouteBeanRemote flightRouteBeanRemote = (FlightRouteBeanRemote) this.initialContext.lookup(FlightRouteBeanRemote.class.getName());
            switch (this.authenticatedEmployee.getEmployeeRole()) {
                case FLEET_MANAGER:
                    final AircraftConfigurationBeanRemote aircraftConfigurationBeanRemote = (AircraftConfigurationBeanRemote) this.initialContext.lookup(AircraftConfigurationBeanRemote.class.getName());
                    return new AircraftConfigurationClient(this.scanner, this.authenticatedEmployee, aircraftConfigurationBeanRemote);
                case ROUTE_PLANNER:
                    return new FlightRouteClient(this.scanner, this.authenticatedEmployee, flightRouteBeanRemote);
                case SCHEDULE_MANAGER:
                    final FlightBeanRemote flightBeanRemote = (FlightBeanRemote) this.initialContext.lookup(FlightBeanRemote.class.getName());
                    return new FlightClient(this.scanner, this.authenticatedEmployee, flightBeanRemote, flightRouteBeanRemote);
                case SALES_MANAGER:
                    break;
                default:
                    break;
            }
        }

        throw new NotAuthenticatedException();
    }

    private String getEmployeeRoleName() {
        if (this.authenticatedEmployee != null && this.authenticatedEmployee.getEmployeeRole() != null) {
            switch (this.authenticatedEmployee.getEmployeeRole()) {
                case FLEET_MANAGER:
                    return "Fleet Manager";
                case ROUTE_PLANNER:
                    return "Route Planner";
                case SALES_MANAGER:
                    return "Sales Manager";
                case SCHEDULE_MANAGER:
                    return "Schedule Manager";
                default:
                    return "Unknown";
            }
        }

        return null;
    }
}
