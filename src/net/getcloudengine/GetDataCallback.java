package net.getcloudengine;

public abstract class GetDataCallback {

	abstract void done(byte[] data, CloudException e);
		
}
