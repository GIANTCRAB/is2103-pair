package webservices;

import entities.FlightReservation;
import entities.Partner;
import entities.PartnerRole;
import exceptions.IncorrectCredentialsException;
import exceptions.InvalidEntityIdException;
import services.AuthService;
import services.FlightReservationService;
import services.PartnerService;

import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.Arrays;
import java.util.List;

@WebService(serviceName = "HolidayReservationService", targetNamespace = "http://localhost:8080/ws/HolidayReservationService")
public class HolidayReservationServiceBean implements HolidayReservationService {
    @Inject
    AuthService authService;
    @Inject
    FlightReservationService flightReservationService;
    @Inject
    PartnerService partnerService;

    @Override
    @WebMethod(operationName = "getPartnerRoles")
    public List<PartnerRole> getPartnerRoles() {
        return Arrays.asList(PartnerRole.values());
    }

    @Override
    @WebMethod(operationName = "login")
    public Partner partnerLogin(@WebParam(name = "username") String username, @WebParam(name = "password") String password) throws IncorrectCredentialsException {
        return this.authService.partnerLogin(username, password);
    }

    @Override
    public List<FlightReservation> getFlightReservations(@WebParam(name = "partner") Partner partner) throws InvalidEntityIdException {
        return this.flightReservationService.getFlightReservations(this.partnerService.findById(partner.getPartnerId()));
    }
}
