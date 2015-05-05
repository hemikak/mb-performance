package exceptions;

/**
 *
 */
public class MBPerformanceException extends Exception {
    /**
     * Error message for exception
     */
    public String errorMessage;

    /**
     * Creates Andes exception
     */
    public MBPerformanceException() {
    }

    /**
     * Creates MB performance exception with error message
     *
     * @param message Error message
     */
    public MBPerformanceException(String message) {
        super(message);
        errorMessage = message;
    }

    /**
     * Creates MB performance exception with error message and throwable
     *
     * @param message Error message
     * @param cause   The throwable
     */
    public MBPerformanceException(String message, Throwable cause) {
        super(message, cause);
        errorMessage = message;
    }

    /**
     * Creates MB performance exception with throwable.
     *
     * @param cause The throwable
     */
    public MBPerformanceException(Throwable cause) {
        super(cause);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return errorMessage;
    }
}
