package webservices;

import entities.FlightReservation;
import entities.Partner;
import entities.PartnerRole;
import exceptions.IncorrectCredentialsException;
import exceptions.InvalidEntityIdException;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.List;

@WebService
public interface HolidayReservationService {
    // This is needed as the enum data is referenced through Partner class
    @WebMethod
    List<PartnerRole> getPartnerRoles();

    @WebMethod
    Partner partnerLogin(String username, String password) throws IncorrectCredentialsException;

    @WebMethod
    List<FlightReservation> getFlightReservations(Partner partner) throws InvalidEntityIdException;
}
