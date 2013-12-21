package net.getcloudengine;

/**
 * Callback function for CloudUser signup operations.
 */
public abstract class SignupCallback {
	
	/**
	 * Called upon the completion of the signup request.
	 * 
	 * @param e Any exception that might have occurred during the request.
	 * null if the request was successful.
	 */
	public abstract void done(CloudException e);
}
