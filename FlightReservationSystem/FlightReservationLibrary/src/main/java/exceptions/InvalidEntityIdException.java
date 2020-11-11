package exceptions;

public class InvalidEntityIdException extends Exception {
    InvalidEntityIdException() {

    }

    InvalidEntityIdException(String message) {
        super(message);
    }
}
