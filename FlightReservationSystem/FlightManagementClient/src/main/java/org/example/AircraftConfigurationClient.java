package org.example;

import controllers.AircraftConfigurationBeanRemote;
import entities.AircraftConfiguration;
import entities.CabinClass;
import entities.CabinClassType;
import entities.Employee;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@RequiredArgsConstructor
public class AircraftConfigurationClient implements SystemClient {
    @NonNull
    private final Scanner scanner;
    @NonNull
    private final Employee authenticatedEmployee;
    @NonNull
    private final AircraftConfigurationBeanRemote aircraftConfigurationBeanRemote;

    @Override
    public void runApp() {
        this.displayAircraftConfigurationMenu();
    }

    private void displayAircraftConfigurationMenu() {
        boolean loop = true;
        while (loop) {
            System.out.println("*** Aircraft Configurator ***");
            System.out.println("1: Create Aircraft Configuration");
            System.out.println("2: View All Aircraft Configuration");
            System.out.println("3: View Aircraft Configuration Details");
            System.out.println("4: Exit");

            final int option = this.scanner.nextInt();

            switch (option) {
                case 1:
                    this.displayAircraftConfigurationCreatorMenu();
                    break;
                case 4:
                default:
                    System.out.println("Exiting...");
                    loop = false;
                    break;
            }
        }
    }

    private void displayAircraftConfigurationCreatorMenu() {
        final int MIN_CABIN_CLASS_COUNT = 1;
        final int MAX_CABIN_CLASS_COUNT = CabinClassType.values().length;

        System.out.println("Create Aircraft Configuration");
        System.out.println("Enter Aircraft Configuration name: ");
        final String aircraftConfigurationName = this.scanner.next();
        System.out.println("Enter Aircraft Type ID:");
        final Long aircraftTypeId = this.scanner.nextLong();
        System.out.println("Enter number of cabin classes: (1 to " + MAX_CABIN_CLASS_COUNT + ")");
        final int cabinClassCount = this.scanner.nextInt();

        if (cabinClassCount >= MIN_CABIN_CLASS_COUNT && cabinClassCount < MAX_CABIN_CLASS_COUNT) {
            final List<CabinClass> cabinClassList = new ArrayList<>();
            for (int i = 0; i < cabinClassCount; i++) {
                System.out.println("Creating Cabin Class No. " + (i + 1) + "/" + (cabinClassCount + 1));

                final CabinClass cabinClass = new CabinClass();
                System.out.println("Enter cabin class type: (" + Arrays.toString(CabinClassType.values()) + ")");
                final String cabinClassType = this.scanner.next();
                cabinClass.setTemporaryCabinClassType(CabinClassType.valueOf(cabinClassType));
                System.out.println("Enter seat configuration: (3-4-3)");
                final String seatConfiguration = this.scanner.next();
                cabinClass.setSeatConfiguration(seatConfiguration);
                System.out.println("Enter number of aisles: ");
                final int noOfAisles = this.scanner.nextInt();
                cabinClass.setNoOfAisles(noOfAisles);
                System.out.println("Enter number of rows: ");
                final int noOfRows = this.scanner.nextInt();
                cabinClass.setNoOfRows(noOfRows);

                cabinClassList.add(cabinClass);
            }

            try {
                final AircraftConfiguration aircraftConfiguration = this.aircraftConfigurationBeanRemote.createConfiguration(this.authenticatedEmployee, aircraftConfigurationName, aircraftTypeId, cabinClassList);
                System.out.println("Aircraft configuration created successfully with ID " + aircraftConfiguration.getAircraftConfigurationId());
            } catch (NotAuthenticatedException e) {
                e.printStackTrace();
            } catch (InvalidConstraintException e) {
                this.displayConstraintErrorMessage(e);
            } catch (InvalidEntityIdException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Invalid number of cabin classes");
        }
    }

    private void displayConstraintErrorMessage(InvalidConstraintException invalidConstraintException) {
        System.out.println("There were some validation errors!");
        System.out.println(invalidConstraintException.toString());
    }
}
