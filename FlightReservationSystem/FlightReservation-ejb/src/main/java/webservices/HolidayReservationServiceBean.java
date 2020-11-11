package webservices;

import entities.FlightReservation;
import entities.Partner;
import exceptions.IncorrectCredentialsException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;
import services.AuthService;
import services.FlightReservationService;
import services.PartnerService;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.util.List;

@WebService(serviceName = "HolidayReservationService", targetNamespace = "http://localhost:8080/ws/HolidayReservationService")
public class HolidayReservationServiceBean implements HolidayReservationService {
    @Resource
    private WebServiceContext wsContext;
    @Inject
    AuthService authService;
    @Inject
    FlightReservationService flightReservationService;
    @Inject
    PartnerService partnerService;

    @Override
    @WebMethod(operationName = "login")
    public Partner partnerLogin(@WebParam(name = "username") String username, @WebParam(name = "password") String password) throws IncorrectCredentialsException {
        final Partner partner = this.authService.partnerLogin(username, password);
        this.setPartner(partner);

        return partner;
    }

    @Override
    public List<FlightReservation> getFlightReservations() throws InvalidEntityIdException, NotAuthenticatedException {
        return this.flightReservationService.getFlightReservations(this.getPartner());
    }

    private Partner getPartner() throws InvalidEntityIdException, NotAuthenticatedException {
        final MessageContext mc = this.wsContext.getMessageContext();
        final HttpSession session = ((javax.servlet.http.HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST)).getSession();
        final Partner partner = (Partner) session.getAttribute("loggedInPartner");

        if (partner == null) {
            throw new NotAuthenticatedException();
        }

        return this.partnerService.findById(partner.getPartnerId());
    }

    private void setPartner(Partner partner) {
        final MessageContext mc = this.wsContext.getMessageContext();
        final HttpSession session = ((javax.servlet.http.HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST)).getSession();
        session.setAttribute("loggedInPartner", partner);
    }
}
