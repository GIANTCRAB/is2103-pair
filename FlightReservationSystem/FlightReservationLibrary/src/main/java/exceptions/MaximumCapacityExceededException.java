package exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=true)
public class MaximumCapacityExceededException extends Exception {

    public MaximumCapacityExceededException() {
    }

    public MaximumCapacityExceededException(String msg) {
        super(msg);
    }
}
