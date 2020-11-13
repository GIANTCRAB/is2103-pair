package exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=true)
public class EntityIsDisabledException extends Exception {

    public EntityIsDisabledException() {
    }

    public EntityIsDisabledException(String msg) {
        super(msg);
    }
}
