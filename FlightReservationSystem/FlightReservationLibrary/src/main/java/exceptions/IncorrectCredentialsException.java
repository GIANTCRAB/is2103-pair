package exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=true)
public class IncorrectCredentialsException extends Exception {
    public IncorrectCredentialsException() {
        super("The credentials supplied are incorrect!");
    }
}
