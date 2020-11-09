package controllers;

import entities.CabinClassType;
import entities.Customer;
import entities.FlightReservation;
import entities.FlightSchedule;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;
import lombok.NonNull;
import pojo.Passenger;
import services.CustomerService;
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

    //TODO: implement this
    @Override
    public List<FlightReservation> reserveFlightForPassengers(@NonNull Customer customer, String creditCard, @NonNull FlightSchedule flightSchedule, @NonNull CabinClassType cabinClassType, @NonNull List<Passenger> passengers) throws InvalidEntityIdException, InvalidConstraintException {
        return null;
    }

    @Override
    public List<FlightReservation> getFlightReservations(@NonNull Customer customer) throws InvalidEntityIdException {
        final Customer managedCustomer = this.customerService.findById(customer.getCustomerId());

        return this.flightReservationService.getFlightReservations(managedCustomer);
    }
}
