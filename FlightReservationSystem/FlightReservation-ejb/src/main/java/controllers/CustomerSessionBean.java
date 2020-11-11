package controllers;

import entities.*;
import exceptions.IncorrectCredentialsException;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;
import lombok.NonNull;
import pojo.Passenger;
import services.*;

import javax.ejb.Stateful;
import javax.inject.Inject;
import java.util.List;

@Stateful
public class CustomerSessionBean implements CustomerBeanRemote {
    private Customer loggedInCustomer = null;
    @Inject
    AuthService authService;
    @Inject
    CabinClassService cabinClassService;
    @Inject
    FlightScheduleService flightScheduleService;
    @Inject
    FlightReservationService flightReservationService;
    @Inject
    FlightReservationPaymentService flightReservationPaymentService;

    @Override
    public Customer login(String email, String password) throws IncorrectCredentialsException {
        final Customer customer = this.authService.customerLogin(email, password);
        this.loggedInCustomer = customer;
        return customer;
    }

    @Override
    public FlightReservationPayment reserveFlightForPassengers(String creditCard, @NonNull FlightSchedule flightSchedule, @NonNull CabinClassType cabinClassType, @NonNull List<Passenger> passengers) throws InvalidEntityIdException, InvalidConstraintException, NotAuthenticatedException {
        if (loggedInCustomer == null) {
            throw new NotAuthenticatedException();
        }
        final FlightSchedule managedFlightSchedule = this.flightScheduleService.findById(flightSchedule.getFlightScheduleId());
        final CabinClassId cabinClassId = new CabinClassId();
        cabinClassId.setCabinClassType(cabinClassType);
        cabinClassId.setAircraftConfigurationId(managedFlightSchedule.getFlight().getAircraftConfiguration().getAircraftConfigurationId());
        final CabinClass cabinClass = this.cabinClassService.findById(cabinClassId);
        final List<FlightReservation> flightReservations = this.flightReservationService.create(managedFlightSchedule, cabinClass, passengers);
        final FlightReservationPayment flightReservationPayment = this.flightReservationPaymentService.create(creditCard, loggedInCustomer);
        this.flightReservationPaymentService.associateFlightReservations(flightReservations, flightReservationPayment);

        // Load its flight reservations
        flightReservationPayment.getFlightReservations().forEach(flightReservation -> {
            flightReservation.getPassengerFirstName();
            flightReservation.getFlightSchedule().getFlight().getFlightRoute().getOrigin();
            flightReservation.getFlightSchedule().getFlight().getFlightRoute().getDest();
        });

        return flightReservationPayment;
    }

    @Override
    public List<FlightReservation> getFlightReservations() throws NotAuthenticatedException {
        if (loggedInCustomer == null) {
            throw new NotAuthenticatedException();
        }

        List<FlightReservation> flightReservations = this.flightReservationService.getFlightReservations(loggedInCustomer);

        flightReservations.forEach(flightReservation -> {
            flightReservation.getFlightReservationPayment().getPaymentId();
            flightReservation.getFlightSchedule().getFlight().getFlightRoute().getOrigin().getIataCode();
            flightReservation.getFlightSchedule().getFlight().getFlightRoute().getDest().getIataCode();
        });

        return flightReservations;
    }

    @Override
    public FlightReservationPayment getFlightReservationDetails(FlightReservationPayment flightReservationPayment) throws NotAuthenticatedException, InvalidEntityIdException {
        if (loggedInCustomer == null) {
            throw new NotAuthenticatedException();
        }

        final FlightReservationPayment managedFlightReservationPayment = this.flightReservationPaymentService.findById(flightReservationPayment.getPaymentId());

        if (!this.loggedInCustomer.getCustomerId().equals(managedFlightReservationPayment.getCustomer().getCustomerId())) {
            throw new InvalidEntityIdException();
        }

        // Load its flight reservations
        managedFlightReservationPayment.getFlightReservations().forEach(flightReservation -> {
            flightReservation.getPassengerFirstName();
            flightReservation.getFlightSchedule().getFlight().getFlightRoute().getOrigin();
            flightReservation.getFlightSchedule().getFlight().getFlightRoute().getDest();
        });

        return managedFlightReservationPayment;
    }

    @Override
    public void logout() throws NotAuthenticatedException {
        if (loggedInCustomer == null) {
            throw new NotAuthenticatedException();
        }

        loggedInCustomer = null;
    }
}
