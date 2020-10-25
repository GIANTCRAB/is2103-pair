package org.example;

import controllers.EmployeeAuthBeanRemote;
import entities.Employee;
import exceptions.IncorrectCredentialsException;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.util.Scanner;

@RequiredArgsConstructor
public class ManagementClient {
    @NonNull
    private final EmployeeAuthBeanRemote employeeAuthBeanRemote;

    @Setter(AccessLevel.PRIVATE)
    private Scanner scanner;
    @Setter(AccessLevel.PRIVATE)
    private Employee authenticatedEmployee;

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
                System.out.println("Logged in as " + this.authenticatedEmployee.getFirstName() + " (ID: " + this.authenticatedEmployee.getEmployeeId() + " )");
                loginLoop = false;
            } catch (IncorrectCredentialsException e) {
                System.out.println("Incorrect credentials! Try again!");
            }
        }
    }
}
