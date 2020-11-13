package exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=true)
public class EntityAlreadyExistException extends Exception {

    public EntityAlreadyExistException() {
    }

    public EntityAlreadyExistException(String msg) {
        super(msg);
    }
}
