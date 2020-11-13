package exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=true)
public class EntityInUseException extends Exception {

    public EntityInUseException() {
    }

    public EntityInUseException(String msg) {
        super(msg);
    }
}
