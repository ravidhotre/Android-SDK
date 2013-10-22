package net.getcloudengine;


/**
 * Callback function for CloudObject fetch operations.
 */
public abstract class FetchCallback {

	public abstract void done(CloudObject obj, CloudException e);
}
