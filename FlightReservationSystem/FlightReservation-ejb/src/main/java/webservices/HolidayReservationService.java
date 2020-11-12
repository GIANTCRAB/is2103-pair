package webservices;

import entities.*;
import exceptions.IncorrectCredentialsException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;
import lombok.NonNull;
import pojo.PossibleFlightSchedules;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.List;

@WebService
public interface HolidayReservationService {
    @WebMethod
    Partner partnerLogin(String username, String password) throws IncorrectCredentialsException;

    @WebMethod
    PossibleFlightSchedules searchFlight(@NonNull Airport departureAirport,
                                         @NonNull Airport destinationAirport,
                                         @NonNull Long departureDate,
                                         @NonNull Integer passengerCount,
                                         Boolean directOnly,
                                         CabinClassType cabinClassType) throws InvalidEntityIdException;

    @WebMethod
    Fare getFlightScheduleFare(@NonNull FlightSchedule flightSchedule,
                               @NonNull CabinClassType cabinClassType) throws InvalidEntityIdException;

    @WebMethod
    List<FlightReservation> getFlightReservations() throws InvalidEntityIdException, NotAuthenticatedException;
}
