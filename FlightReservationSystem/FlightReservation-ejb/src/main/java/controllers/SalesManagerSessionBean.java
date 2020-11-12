package controllers;

import entities.*;
import exceptions.IncorrectCredentialsException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;
import lombok.NonNull;
import pojo.SeatInventory;
import services.AuthService;
import services.FlightScheduleService;
import services.FlightService;

import javax.ejb.Stateful;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Stateful
public class SalesManagerSessionBean implements SalesManagerBeanRemote {
    private Employee loggedInEmployee;
    @Inject
    AuthService authService;
    @Inject
    FlightService flightService;
    @Inject
    FlightScheduleService flightScheduleService;

    private final EmployeeRole PERMISSION_REQUIRED = EmployeeRole.SALES_MANAGER;

    @Override
    public Employee login(String username, String password) throws IncorrectCredentialsException, InvalidEntityIdException {
        final Employee employee = this.authService.employeeLogin(username, password);

        if (employee.getEmployeeRole().equals(PERMISSION_REQUIRED)) {
            this.loggedInEmployee = employee;
            return employee;
        } else {
            throw new InvalidEntityIdException();
        }
    }

    @Override
    public List<FlightSchedule> getFlightSchedules(@NonNull String flightCode) throws NotAuthenticatedException, InvalidEntityIdException {
        if (this.loggedInEmployee == null) {
            throw new NotAuthenticatedException();
        }
        final Flight managedFlight = this.flightService.getFlightByFlightCode(flightCode);
        final List<FlightSchedule> flightSchedules = managedFlight.getFlightSchedules();
        // Load data about flight reservations
        flightSchedules.forEach(flightSchedule -> flightSchedule.getFlightReservations().size());

        return flightSchedules;
    }

    @Override
    public FlightSchedule getFlightScheduleDetails(@NonNull FlightSchedule flightSchedule) throws NotAuthenticatedException, InvalidEntityIdException {

        final List<SeatInventory> seatInventories = new ArrayList<>();

        //TODO: think about seat taken logic by cabin
        final FlightSchedule managedFlightSchedule = this.flightScheduleService.findById(flightSchedule.getFlightScheduleId());
        managedFlightSchedule.getFlightReservations().size();
        managedFlightSchedule.getFlight().getAircraftConfiguration().getCabinClasses().forEach(cabinClass -> {
            final SeatInventory seatInventory = new SeatInventory();
            seatInventory.setMaxSeats(cabinClass.getMaxCapacity());
            seatInventory.setCabinClassType(cabinClass.getCabinClassId().getCabinClassType());
        });
        managedFlightSchedule.getFlight().getAircraftConfiguration().getTotalCabinClassCapacity();


        return managedFlightSchedule;
    }

    @Override
    public List<FlightReservation> getFlightReservations(@NonNull FlightSchedule flightSchedule) throws NotAuthenticatedException, InvalidEntityIdException {
        return null;
    }
}
