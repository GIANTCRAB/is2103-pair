package exceptions;

public class EntityIsDisabledException extends Exception {

    public EntityIsDisabledException() {
    }

    public EntityIsDisabledException(String msg) {
        super(msg);
    }
}
