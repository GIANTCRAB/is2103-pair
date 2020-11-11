package controllers;

import entities.Airport;
import entities.CabinClassType;
import entities.Customer;
import entities.FlightSchedule;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import lombok.NonNull;

import javax.ejb.Remote;
import java.sql.Date;
import java.util.List;
import java.util.Set;

@Remote
public interface VisitorBeanRemote {
    Customer register(String firstName,
                      String lastName,
                      String email,
                      String password,
                      String phoneNumber,
                      String address) throws InvalidConstraintException;

    Set<List<FlightSchedule>> searchFlight(@NonNull Airport departureAirport,
                                           @NonNull Airport destinationAirport,
                                           @NonNull Date departureDate,
                                           @NonNull Integer passengerCount,
                                           Boolean directOnly,
                                           CabinClassType cabinClassType) throws InvalidEntityIdException;
}
