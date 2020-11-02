package webservices;

import entities.Partner;
import exceptions.IncorrectCredentialsException;
import services.AuthService;

import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(serviceName = "HolidayReservationService", targetNamespace = "http://localhost:8080/ws/HolidayReservationService")
public class HolidayReservationServiceBean implements HolidayReservationService {
    @Inject
    AuthService authService;

    @Override
    @WebMethod(operationName = "login")
    public Partner partnerLogin(@WebParam(name = "username") String username, @WebParam(name = "password") String password) throws IncorrectCredentialsException {
        return this.authService.partnerLogin(username, password);
    }
}
