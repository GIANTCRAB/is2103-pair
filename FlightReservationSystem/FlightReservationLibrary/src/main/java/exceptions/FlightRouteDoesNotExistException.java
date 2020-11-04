package exceptions;

public class FlightRouteDoesNotExistException extends Exception {

    public FlightRouteDoesNotExistException() {
    }

    public FlightRouteDoesNotExistException(String msg) {
        super(msg);
    }
}
