package org.example;

import controllers.VisitorBeanRemote;
import entities.Customer;
import exceptions.InvalidConstraintException;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.naming.InitialContext;
import javax.validation.constraints.NotNull;
import java.util.Scanner;

@RequiredArgsConstructor
public class ReservationClient implements SystemClient {
    @NotNull
    private final InitialContext initialContext;
    @NonNull
    private final VisitorBeanRemote visitorBeanRemote;

    @Setter(AccessLevel.PRIVATE)
    private Scanner scanner;

    @Override
    public void runApp() {
        this.scanner = new Scanner(System.in);
        this.displayVisitorMenu();
        this.scanner.close();
    }

    private void displayVisitorMenu() {
        boolean loop = true;

        while (loop) {
            System.out.println("*** Flight Reservation Client ***");
            System.out.println("1: Registration");
            System.out.println("2: Customer Login");
            System.out.println("3: Search Flight");
            System.out.println("4: Exit");
            final int option = this.scanner.nextInt();

            switch (option) {
                case 1:
                    displayRegistrationMenu();
                    break;
                case 4:
                default:
                    loop = false;
                    break;
            }
        }
    }

    private void displayRegistrationMenu() {
        boolean loop = true;

        while (loop) {
            System.out.println("Enter First Name:");
            final String firstName = this.scanner.next();
            System.out.println("Enter Last Name:");
            final String lastName = this.scanner.next();
            System.out.println("Enter Email:");
            final String email = this.scanner.next();
            System.out.println("Enter Password:");
            final String password = this.scanner.next();
            System.out.println("Enter Phone Number:");
            final String phoneNumber = this.scanner.next();
            System.out.println("Enter Address:");
            final String address = this.scanner.next();


            try {
                final Customer customer = this.visitorBeanRemote.register(firstName, lastName, email, password, phoneNumber, address);
                System.out.println("Successfully registered as " + customer.getFirstName() + " (ID: " + customer.getCustomerId() + ")");
                loop = false;
            } catch (InvalidConstraintException e) {
                this.displayConstraintErrorMessage(e);
            }
        }

    }

    private void displayConstraintErrorMessage(InvalidConstraintException invalidConstraintException) {
        System.out.println("There were some validation errors!");
        System.out.println(invalidConstraintException.toString());
    }
}
