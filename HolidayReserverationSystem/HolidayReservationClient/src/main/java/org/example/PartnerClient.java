package org.example;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import webservices.HolidayReservationService.HolidayReservationServiceBean;
import webservices.HolidayReservationService.IncorrectCredentialsException_Exception;
import webservices.HolidayReservationService.Partner;

import java.util.Scanner;

@RequiredArgsConstructor
public class PartnerClient implements SystemClient {
    @NonNull
    final HolidayReservationServiceBean holidayReservationServiceBean;

    @Setter(AccessLevel.PRIVATE)
    private Scanner scanner;

    public void runApp() {
        this.scanner = new Scanner(System.in);
        this.scanner.useDelimiter("\n");
        this.displayLoginMenu();
        this.scanner.close();
    }

    private void displayLoginMenu() {
        boolean loop = true;
        System.out.println("***Holiday Reservation System***");

        while (loop) {
            System.out.println("===Please login===");
            System.out.println("Logging in as: (1) Employee. (2) Manager");
            final int option = this.scanner.nextInt();
            System.out.println("Enter Email:");
            final String email = this.scanner.next();
            System.out.println("Enter Password:");
            final String password = this.scanner.next();

            try {
                final Partner partner = this.holidayReservationServiceBean.login(email, password);
                System.out.println("Logged in as " + partner.getCompanyName() + " (ID: " + partner.getPartnerId() + ")");
                loop = false;

                this.createSystemBasedOnRole(partner, option).runApp();
            } catch (IncorrectCredentialsException_Exception e) {
                System.out.println(e.getFaultInfo().getMessage());
            }
        }
    }

    private SystemClient createSystemBasedOnRole(Partner partner, int option) {
        switch (option) {
            case 2:
                return new PartnerManagerClient(this.scanner, this.holidayReservationServiceBean, partner);
            default:
            case 1:
                return new PartnerEmployeeClient(this.scanner, this.holidayReservationServiceBean, partner);
        }
    }
}
