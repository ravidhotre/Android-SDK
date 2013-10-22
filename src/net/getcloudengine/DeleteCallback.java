package net.getcloudengine;

/** 
 *  Callback function for CloudObject delete operations
 */
public abstract class DeleteCallback {
	
	public abstract void done(CloudException e);
}
