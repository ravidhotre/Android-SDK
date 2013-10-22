package net.getcloudengine;


/**
 * Exceptions related to CloudObject are instances of this class.
 * 
 */
public class CloudObjectException extends CloudException {
	
	private static final long serialVersionUID = 1L;

	/**
     * Constructs a new CloudEngineObjectException.
     */
    public CloudObjectException() {
        super();
    }

    /**
     * Constructs a new CloudEngineObjectException.
     * 
     * @param message
     *            the detail message of this exception
     */
    public CloudObjectException(String message) {
        super(message);
    }

    /**
     * Constructs a new CloudEngineObjectException.
     * 
     * @param message
     *            the detail message of this exception
     * @param throwable
     *            the cause of this exception
     */
    public CloudObjectException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Constructs a new CloudEngineObjectException.
     * 
     * @param throwable
     *            the cause of this exception
     */
    public CloudObjectException(Throwable throwable) {
        super(throwable);
    }
}
