package org.example;

import controllers.AircraftConfigurationBeanRemote;
import controllers.FareBeanRemote;
import controllers.FlightRouteBeanRemote;
import controllers.FlightBeanRemote;
import controllers.FlightSchedulePlanBeanRemote;
import entities.Employee;
import entities.EmployeeRole;
import exceptions.IncorrectCredentialsException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Scanner;

@RequiredArgsConstructor
public class ManagementClient implements SystemClient {
    @NonNull
    private final FlightRouteBeanRemote flightRouteBeanRemote;
    @NonNull
    private final AircraftConfigurationBeanRemote aircraftConfigurationBeanRemote;
    @NonNull
    private final FareBeanRemote fareBeanRemote;
    @NonNull
    private final FlightBeanRemote flightBeanRemote;
    @NonNull
    private final FlightSchedulePlanBeanRemote flightSchedulePlanBeanRemote;

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
            System.out.println("Enter Employee Role: (FLEET_MANAGER, ROUTE_PLANNER, SCHEDULE_MANAGER, SALES_MANAGER)");
            final String roleName = this.scanner.next();
            System.out.println("Enter Username:");
            final String username = this.scanner.next();
            System.out.println("Enter Password:");
            final String password = this.scanner.next();
            try {
                this.authenticatedEmployee = this.loginBasedOnRole(EmployeeRole.valueOf(roleName), username, password);
                System.out.println("Logged in as " + this.authenticatedEmployee.getFirstName() + " (ID: " + this.authenticatedEmployee.getEmployeeId() + ")");
                System.out.println("Employee Role: " + this.getEmployeeRoleName());
                this.createSystemBasedOnRole().runApp();
                loginLoop = false;
            } catch (IncorrectCredentialsException e) {
                System.out.println("Incorrect credentials! Try again!");
            } catch (NotAuthenticatedException e) {
                System.out.println("Invalid role in system. Please try again");
                this.authenticatedEmployee = null;
            } catch (InvalidEntityIdException e) {
                System.out.println("You do not have this role!");
            }
        }
    }

    private Employee loginBasedOnRole(EmployeeRole employeeRole, String username, String password) throws IncorrectCredentialsException, InvalidEntityIdException {
        switch (employeeRole) {
            case ROUTE_PLANNER:
                return this.flightRouteBeanRemote.login(username, password);
            case SCHEDULE_MANAGER:
                this.flightSchedulePlanBeanRemote.login(username, password);
                return this.flightBeanRemote.login(username, password);
            case FLEET_MANAGER:
                return this.aircraftConfigurationBeanRemote.login(username, password);
        }

        throw new IncorrectCredentialsException();
    }

    private SystemClient createSystemBasedOnRole() throws NotAuthenticatedException {
        if (this.authenticatedEmployee != null && this.authenticatedEmployee.getEmployeeRole() != null) {
            switch (this.authenticatedEmployee.getEmployeeRole()) {
                case FLEET_MANAGER:
                    return new AircraftConfigurationClient(this.scanner, this.authenticatedEmployee, aircraftConfigurationBeanRemote);
                case ROUTE_PLANNER:
                    return new FlightRouteClient(this.scanner, this.authenticatedEmployee, flightRouteBeanRemote);
                case SCHEDULE_MANAGER:
                    return new ScheduleManagerClient(this.scanner, this.authenticatedEmployee, fareBeanRemote, flightBeanRemote, flightRouteBeanRemote, aircraftConfigurationBeanRemote, flightSchedulePlanBeanRemote);
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
