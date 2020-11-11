package exceptions;

public class EntityInUseException extends Exception {

    public EntityInUseException() {
    }

    public EntityInUseException(String msg) {
        super(msg);
    }
}
