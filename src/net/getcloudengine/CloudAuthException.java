package net.getcloudengine;

/**
 * An Exception indicating that a Session failed to open or obtain new permissions.
 */
public class CloudAuthException extends CloudException {
    static final long serialVersionUID = 1;

    /**
     * Constructs a CloudEngineAuthorizationException with no additional
     * information.
     */
    public CloudAuthException() {
        super();
    }

    /**
     * Constructs a CloudEngineAuthorizationException with a message.
     * 
     * @param message
     *            A String to be returned from getMessage.
     */
    public CloudAuthException(String message) {
        super(message);
    }

    /**
     * Constructs a CloudEngineAuthorizationException with a message and inner
     * error.
     * 
     * @param message
     *            A String to be returned from getMessage.
     * @param throwable
     *            A Throwable to be returned from getCause.
     */
    public CloudAuthException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Constructs a CloudEngineAuthorizationException with an inner error.
     * 
     * @param throwable
     *            A Throwable to be returned from getCause.
     */
    public CloudAuthException(Throwable throwable) {
        super(throwable);
    }
}
