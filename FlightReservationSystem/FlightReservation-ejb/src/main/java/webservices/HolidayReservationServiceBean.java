package webservices;

import entities.*;
import exceptions.IncorrectCredentialsException;
import exceptions.InvalidConstraintException;
import exceptions.InvalidEntityIdException;
import exceptions.NotAuthenticatedException;
import lombok.NonNull;
import pojo.Passenger;
import pojo.PossibleFlightPathNodes;
import pojo.PossibleFlightSchedules;
import services.*;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.sql.Date;
import java.util.*;

@WebService(serviceName = "HolidayReservationService", targetNamespace = "http://localhost:8080/ws/HolidayReservationService")
public class HolidayReservationServiceBean implements HolidayReservationService {
    @Resource
    private WebServiceContext wsContext;
    @Inject
    AuthService authService;
    @Inject
    FlightReservationService flightReservationService;
    @Inject
    AirportService airportService;
    @Inject
    FlightRouteService flightRouteService;
    @Inject
    FlightService flightService;
    @Inject
    FlightScheduleService flightScheduleService;
    @Inject
    FareService fareService;
    @Inject
    CabinClassService cabinClassService;
    @Inject
    PartnerService partnerService;

    @Override
    @WebMethod(operationName = "login")
    public Partner partnerLogin(@WebParam(name = "username") String username, @WebParam(name = "password") String password) throws IncorrectCredentialsException {
        final Partner partner = this.authService.partnerLogin(username, password);
        this.setPartner(partner);

        return partner;
    }

    //TODO: test this
    @Override
    public PossibleFlightSchedules searchFlight(@NonNull @WebParam(name = "departureAirport") Airport departureAirport,
                                                @NonNull @WebParam(name = "destinationAirport") Airport destinationAirport,
                                                @NonNull @WebParam(name = "departureDate") Long departureDateLong,
                                                @NonNull @WebParam(name = "passengerCount") Integer passengerCount,
                                                @WebParam(name = "directOnly") Boolean directOnly,
                                                @WebParam(name = "cabinClassType") CabinClassType cabinClassType) throws InvalidEntityIdException {
        final Date departureDate = new Date(departureDateLong);
        final Airport managedDepartureAirport = this.airportService.findAirportByCode(departureAirport.getIataCode());
        final Airport managedDestinationAirport = this.airportService.findAirportByCode(destinationAirport.getIataCode());
        final FlightRoute flightRoute = this.flightRouteService.findFlightRouteByOriginDest(managedDepartureAirport, managedDestinationAirport);
        final PossibleFlightSchedules possibleFlightSchedules = new PossibleFlightSchedules();
        final Set<PossibleFlightPathNodes> possibleFlightPathNodesSet = new HashSet<>();

        final Set<List<Flight>> possibleFlights = this.flightService.getPossibleFlights(flightRoute.getOrigin(), flightRoute.getDest());

        if (directOnly != null && directOnly) {
            final PossibleFlightPathNodes searchResult = new PossibleFlightPathNodes();
            final List<FlightSchedule> possibleFlightPathNodes = searchResult.getFlightSchedules();
            final List<Flight> simpleFlightPath = this.flightService.getFlightByOriginDest(flightRoute.getOrigin(), flightRoute.getDest());
            if (cabinClassType != null) {
                simpleFlightPath.forEach(simpleFlightPathNode -> possibleFlightPathNodes.addAll(this.flightScheduleService.searchFlightSchedules(simpleFlightPathNode, departureDate, passengerCount, cabinClassType)));
            } else {
                simpleFlightPath.forEach(simpleFlightPathNode -> possibleFlightPathNodes.addAll(this.flightScheduleService.searchFlightSchedules(simpleFlightPathNode, departureDate, passengerCount)));
            }
            searchResult.setFlightSchedules(possibleFlightPathNodes);
            possibleFlightPathNodesSet.add(searchResult);
        } else {
            if (cabinClassType != null) {
                // The flight schedule MUST have the cabin class
                for (List<Flight> possibleFlight : possibleFlights) {
                    final PossibleFlightPathNodes searchResult = new PossibleFlightPathNodes();
                    final List<FlightSchedule> possibleFlightPathNodes = searchResult.getFlightSchedules();
                    for (Flight flightPathNode : possibleFlight) {
                        possibleFlightPathNodes.addAll(this.flightScheduleService.searchFlightSchedules(flightPathNode, departureDate, passengerCount, cabinClassType));
                    }
                    searchResult.setFlightSchedules(possibleFlightPathNodes);
                    possibleFlightPathNodesSet.add(searchResult);
                }
            } else {
                // Basic search only
                for (List<Flight> possibleFlight : possibleFlights) {
                    final PossibleFlightPathNodes searchResult = new PossibleFlightPathNodes();
                    final List<FlightSchedule> possibleFlightPathNodes = searchResult.getFlightSchedules();
                    for (Flight flightPathNode : possibleFlight) {
                        possibleFlightPathNodes.addAll(this.flightScheduleService.searchFlightSchedules(flightPathNode, departureDate, passengerCount));
                    }
                    searchResult.setFlightSchedules(possibleFlightPathNodes);
                    possibleFlightPathNodesSet.add(searchResult);
                }
            }
        }
        // Update the data into the object
        possibleFlightSchedules.setPossibleFlightPathNodesSet(Arrays.asList(possibleFlightPathNodesSet.toArray(new PossibleFlightPathNodes[]{})));
        return possibleFlightSchedules;
    }

    @Override
    public Fare getFlightScheduleFare(@NonNull @WebParam(name = "flightSchedule") FlightSchedule flightSchedule,
                                      @NonNull @WebParam(name = "cabinClassType") CabinClassType cabinClassType) throws InvalidEntityIdException {
        final FlightSchedule managedFlightSchedule = this.flightScheduleService.findById(flightSchedule.getFlightScheduleId());

        final AircraftConfiguration aircraftConfiguration = managedFlightSchedule.getFlight().getAircraftConfiguration();
        final CabinClassId cabinClassId = new CabinClassId();
        cabinClassId.setAircraftConfigurationId(aircraftConfiguration.getAircraftConfigurationId());
        cabinClassId.setCabinClassType(cabinClassType);
        final CabinClass cabinClass = this.cabinClassService.findById(cabinClassId);

        // Highest only because this is partner
        return this.fareService.findByScheduleAndCabinClass(managedFlightSchedule, cabinClass, true);
    }

    @Override
    public FlightReservationPayment reserveFlightForPassengers(@WebParam(name = "creditCard") String creditCard,
                                                               @NonNull @WebParam(name = "flightSchedule") FlightSchedule flightSchedule,
                                                               @NonNull @WebParam(name = "cabinClassType") CabinClassType cabinClassType,
                                                               @NonNull @WebParam(name = "passengers") List<Passenger> passengers) throws NotAuthenticatedException, InvalidEntityIdException, InvalidConstraintException {
        return null;
    }

    @Override
    public List<FlightReservation> getFlightReservations() throws InvalidEntityIdException, NotAuthenticatedException {
        return this.flightReservationService.getFlightReservations(this.getPartner());
    }

    @Override
    public FlightReservationPayment getFlightReservationDetails(FlightReservationPayment flightReservationPayment) throws NotAuthenticatedException, InvalidEntityIdException {
        return null;
    }

    @Override
    public void logout() throws NotAuthenticatedException, InvalidEntityIdException {
        this.getPartner();
        this.setPartner(null);
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
