package exceptions;

public class IncorrectCredentialsException extends Exception {
    public IncorrectCredentialsException() {
        super("The credentials supplied are incorrect!");
    }
}
