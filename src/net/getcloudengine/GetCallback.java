package net.getcloudengine;


/**
 * Callback function for CloudObject retrieve operations
 */
public abstract class GetCallback {
	
	abstract void done(CloudObject obj, CloudException e);
}
