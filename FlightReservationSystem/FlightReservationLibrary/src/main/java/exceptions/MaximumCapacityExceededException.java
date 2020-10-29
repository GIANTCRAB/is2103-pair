package exceptions;

public class MaximumCapacityExceededException extends Exception {

    public MaximumCapacityExceededException() {
    }

    public MaximumCapacityExceededException(String msg) {
        super(msg);
    }
}
