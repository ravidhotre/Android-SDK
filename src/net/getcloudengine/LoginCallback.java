package net.getcloudengine;

/**
 * Callback function for CloudUser login requests
 */
public abstract class LoginCallback {
	
	/**
	 * Called upon the completion of the login request.
	 * 
	 * @param user The user object corresponding to the user
	 * who is logged in with the given credentials
	 * 
	 * @param e Any exception that might have occurred during the request.
	 * null if the request was successful.
	 */
	public abstract void done(CloudUser user, CloudException e);
}
