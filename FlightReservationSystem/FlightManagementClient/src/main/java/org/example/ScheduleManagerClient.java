package org.example;

import controllers.FlightBeanRemote;
import controllers.FlightRouteBeanRemote;
import controllers.AircraftConfigurationBeanRemote;
import controllers.FlightSchedulePlanBeanRemote;
import entities.Employee;
import java.util.Scanner;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ScheduleManagerClient implements SystemClient {
    @NonNull
    private final Scanner scanner;
    @NonNull
    private final Employee authenticatedEmployee;
    @NonNull
    private final FlightBeanRemote flightBeanRemote;
    @NonNull
    private final FlightRouteBeanRemote flightRouteBeanRemote;
    @NonNull
    private final AircraftConfigurationBeanRemote aircraftConfigurationBeanRemote;
    @NonNull
    private final FlightSchedulePlanBeanRemote flightSchedulePlanBeanRemote;
    
    
    @Override
    public void runApp() {
        this.displayScheduleManagerMenu();
    }
    
    private SystemClient displayScheduleManagerMenu() {
        boolean loop = true; 
        
        while(loop) { 
            System.out.println("*** Schedule Manager Client ***");
            System.out.println("1: Flight Client");
            System.out.println("2: Flight Schedule Plan Client");
            final int option = this.scanner.nextInt();

            if (option == 1) {
                return new FlightClient(this.scanner, this.authenticatedEmployee, flightBeanRemote, flightRouteBeanRemote);
            } else if (option == 2) {
                return new FlightSchedulePlanClient(this.scanner, this.authenticatedEmployee, flightBeanRemote, aircraftConfigurationBeanRemote, flightSchedulePlanBeanRemote);
            } else {
                loop = false;
            }
        }
        return null;
    }
}
