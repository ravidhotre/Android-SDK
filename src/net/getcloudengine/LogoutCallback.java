package net.getcloudengine;

/**
 * Callback function for CloudUser logout requests
 */
public abstract class LogoutCallback {
	
	/**
	 * Called upon the completion of the logout request.
	 * 
	 * @param e Any exception that might have occurred during the request.
	 * null if the request was successful.
	 */
	public abstract void done(CloudException e);
}
