package exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=true)
public class NotAuthenticatedException extends Exception {
}
