package exceptions;

public class EntityAlreadyExistException extends Exception {

    public EntityAlreadyExistException() {
    }

    public EntityAlreadyExistException(String msg) {
        super(msg);
    }
}
