package net.getcloudengine;

/**
 * Callback function for CloudObject save operations.
 */
public abstract class SaveCallback {
	
	public abstract void done(CloudException e);
}
