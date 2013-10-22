
package net.getcloudengine;

/**
 * This is the base class for all exceptions of CloudEngine. 
 *  All exception thrown by CloudEngine Android SDK library can be caught
 *  using this class.
 */
public class CloudException extends RuntimeException {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructs a new CloudEngineException.
     */
    public CloudException() {
        super();
    }

    /**
     * Constructs a new CloudEngineException.
     * 
     * @param message
     *            the detail message of this exception
     */
    public CloudException(String message) {
        super(message);
    }

    /**
     * Constructs a new CloudEngineException.
     * 
     * @param message
     *            the detail message of this exception
     * @param throwable
     *            the cause of this exception
     */
    public CloudException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Constructs a new CloudEngineException.
     * 
     * @param throwable
     *            the cause of this exception
     */
    public CloudException(Throwable throwable) {
        super(throwable);
    }
}


