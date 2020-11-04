package exceptions;

public class FlightRouteAlreadyExistException extends Exception {

    public FlightRouteAlreadyExistException() {
    }

    public FlightRouteAlreadyExistException(String msg) {
        super(msg);
    }
}
