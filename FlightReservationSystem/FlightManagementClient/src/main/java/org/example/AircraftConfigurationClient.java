package org.example;

import controllers.AircraftConfigurationBeanRemote;
import entities.AircraftConfiguration;
import entities.CabinClass;
import entities.CabinClassType;
import entities.Employee;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;
import exceptions.MaximumCapacityExceededException;
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
            System.out.println("2: View All Aircraft Configurations");
            System.out.println("3: View Aircraft Configuration Details");
            System.out.println("4: Exit");

            final int option = this.scanner.nextInt();

            switch (option) {
                case 1:
                    this.displayAircraftConfigurationCreatorMenu();
                    break;
                case 2:
                    this.displayViewAircraftConfigurationsMenu();
                    break;
                case 3:
                    this.displayViewAircraftConfigurationDetailsMenu();
                    break;
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

        System.out.println("*** Create Aircraft Configuration ***");
        System.out.println("Enter Aircraft Configuration name: ");
        final String aircraftConfigurationName = this.scanner.next();
        System.out.println("Enter Aircraft Type ID:");
        final Long aircraftTypeId = this.scanner.nextLong();
        System.out.println("Enter number of cabin classes: (1 to " + MAX_CABIN_CLASS_COUNT + ")");
        final int cabinClassCount = this.scanner.nextInt();

        if (cabinClassCount >= MIN_CABIN_CLASS_COUNT && cabinClassCount < MAX_CABIN_CLASS_COUNT) {
            final List<CabinClass> cabinClassList = new ArrayList<>();
            for (int i = 0; i < cabinClassCount; i++) {
                System.out.println("Creating Cabin Class No. " + (i + 1) + "/" + (cabinClassCount));

                final CabinClass cabinClass = new CabinClass();
                System.out.println("Enter cabin class type: (" + Arrays.toString(CabinClassType.values()) + ")");
                final String cabinClassType = this.scanner.next();
                cabinClass.setTemporaryCabinClassType(CabinClassType.valueOf(cabinClassType));
                System.out.println("Enter seat configuration: e.g. (3-4-3)");
                final String seatConfiguration = this.scanner.next();
                cabinClass.setSeatConfiguration(seatConfiguration);
                System.out.println("Enter number of rows: ");
                final int noOfRows = this.scanner.nextInt();
                cabinClass.setNoOfRows(noOfRows);

                cabinClassList.add(cabinClass);
            }

            try {
                final AircraftConfiguration aircraftConfiguration = this.aircraftConfigurationBeanRemote.createConfiguration(aircraftConfigurationName, aircraftTypeId, cabinClassList);
                System.out.println("Aircraft configuration created successfully with ID " + aircraftConfiguration.getAircraftConfigurationId());
            } catch (NotAuthenticatedException e) {
                e.printStackTrace();
            } catch (InvalidConstraintException e) {
                this.displayConstraintErrorMessage(e);
            } catch (InvalidEntityIdException e) {
                e.printStackTrace();
            } catch (MaximumCapacityExceededException e) {
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
    
    private void displayViewAircraftConfigurationsMenu() {
        System.out.println("*** View All Aircraft Configurations ***");

        try {
            final List<Object[]> resultList = this.aircraftConfigurationBeanRemote.getAircraftConfigurations();
            resultList.forEach(result -> System.out.println("Aircraft Type: " + result[0] + ", Aircraft Configuration: " + result[1]));
        } catch (NotAuthenticatedException e) {
            System.out.println("You do not have permission to do this!");
        }
    }
    
    private void displayViewAircraftConfigurationDetailsMenu() {
        System.out.println("*** View Aircraft Configuration Details ***");
        System.out.println("Enter the ID of the aircraft configuration details you would like to view:");
        long aircraftConfigurationId = this.scanner.nextLong();

        try {
            // 
            final AircraftConfiguration aircraftConfiguration = this.aircraftConfigurationBeanRemote.getAircraftConfigurationById(aircraftConfigurationId);
            final List<CabinClass> cabinClassList = aircraftConfiguration.getCabinClasses();
            
            System.out.println("Viewing details for aircraft configuration: " + aircraftConfiguration.getAircraftConfigurationName() + "\n");
            System.out.println("Total no. of cabin classes: " + cabinClassList.size());
            System.out.println("-----------------");
            cabinClassList.forEach(cabinClass -> System.out.println("Cabin class type: " + cabinClass.getCabinClassId().getCabinClassType() + "\n" +
                                                                    "Seating configuration: " + cabinClass.getSeatConfiguration() + "\n" +
                                                                    "No. of aisles: " + cabinClass.getNoOfAisles() + "\n" +
                                                                    "No. of rows: " + cabinClass.getNoOfRows() + "\n" +
                                                                    "No. of columns: " + cabinClass.getNoOfCols() + "\n" +
                                                                    "--------------------"));
        } catch (NotAuthenticatedException e) {
            System.out.println("You do not have permission to do this!");
        }
    }
}
