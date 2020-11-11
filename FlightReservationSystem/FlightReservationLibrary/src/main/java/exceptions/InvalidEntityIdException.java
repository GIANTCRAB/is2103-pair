package exceptions;

public class InvalidEntityIdException extends Exception {
    public InvalidEntityIdException() {

    }

    public InvalidEntityIdException(String message) {
        super(message);
    }
}
