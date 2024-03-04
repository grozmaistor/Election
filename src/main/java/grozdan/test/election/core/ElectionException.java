package grozdan.test.election.core;

public class ElectionException extends Exception {

    public ElectionException() {
        super();
    }

    public ElectionException(String message) {
        super(message);
    }

    public ElectionException(Throwable t) {
        super(t);
    }

    public ElectionException(String message, Throwable t) {
        super(message, t);
    }
}
