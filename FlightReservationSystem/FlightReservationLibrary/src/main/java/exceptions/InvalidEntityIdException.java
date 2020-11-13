package exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=true)
public class InvalidEntityIdException extends Exception {
    public InvalidEntityIdException() {

    }

    public InvalidEntityIdException(String message) {
        super(message);
    }
}
