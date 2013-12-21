package net.getcloudengine;

/**
 * Callback function for password reset requests.
 */
public abstract class PasswordResetCallback {
	
	/**
	 * Called upon the completion of the password reset request.
	 * 
	 * @param e Any exception that might have occurred during the request.
	 * null if the request was successful.
	 */
	public abstract void done(CloudException e);
}
