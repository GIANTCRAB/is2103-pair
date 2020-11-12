package webservices;

import entities.*;
import exceptions.IncorrectCredentialsException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;
import lombok.NonNull;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.ArrayList;
import java.util.HashSet;

@WebService
public interface HolidayReservationService {
    @WebMethod
    Partner partnerLogin(String username, String password) throws IncorrectCredentialsException;

    @WebMethod
    HashSet<ArrayList<FlightSchedule>> searchFlight(@NonNull Airport departureAirport,
                                                    @NonNull Airport destinationAirport,
                                                    @NonNull Long departureDate,
                                                    @NonNull Integer passengerCount,
                                                    Boolean directOnly,
                                                    CabinClassType cabinClassType) throws InvalidEntityIdException;

    @WebMethod
    ArrayList<FlightReservation> getFlightReservations() throws InvalidEntityIdException, NotAuthenticatedException;
}
