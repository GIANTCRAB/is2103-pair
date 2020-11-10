package controllers;

import entities.*;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import lombok.NonNull;
import pojo.Passenger;
import services.CustomerService;
import services.FlightReservationPaymentService;
import services.FlightReservationService;

import javax.ejb.Stateful;
import javax.inject.Inject;
import java.util.List;

@Stateful
public class CustomerSessionBean implements CustomerBeanRemote {
    @Inject
    CustomerService customerService;
    @Inject
    FlightReservationService flightReservationService;
    @Inject
    FlightReservationPaymentService flightReservationPaymentService;

    //TODO: implement this
    @Override
    public FlightReservationPayment reserveFlightForPassengers(@NonNull Customer customer, String creditCard, @NonNull FlightSchedule flightSchedule, @NonNull CabinClassType cabinClassType, @NonNull List<Passenger> passengers) throws InvalidEntityIdException, InvalidConstraintException {
        final Customer managedCustomer = this.customerService.findById(customer.getCustomerId());
        // TODO: find the managed flight schedule

        final List<FlightReservation> flightReservations = this.flightReservationService.create(flightSchedule, passengers);
        final FlightReservationPayment flightReservationPayment = this.flightReservationPaymentService.create(creditCard, managedCustomer);
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
    public List<FlightReservation> getFlightReservations(@NonNull Customer customer) throws InvalidEntityIdException {
        final Customer managedCustomer = this.customerService.findById(customer.getCustomerId());

        return this.flightReservationService.getFlightReservations(managedCustomer);
    }

    @Override
    public FlightReservationPayment getFlightReservationDetails(@NonNull Customer customer, FlightReservationPayment flightReservationPayment) throws InvalidEntityIdException {
        final Customer managedCustomer = this.customerService.findById(customer.getCustomerId());

        final FlightReservationPayment managedFlightReservationPayment = this.flightReservationPaymentService.findById(flightReservationPayment.getPaymentId());

        if (!managedCustomer.getCustomerId().equals(managedFlightReservationPayment.getCustomer().getCustomerId())) {
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
}
