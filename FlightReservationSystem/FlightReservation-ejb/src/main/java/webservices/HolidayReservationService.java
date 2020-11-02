package webservices;

import entities.Partner;
import exceptions.IncorrectCredentialsException;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface HolidayReservationService {
    @WebMethod
    Partner partnerLogin(String username, String password) throws IncorrectCredentialsException;
}
