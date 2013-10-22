package net.getcloudengine;

import java.util.List;

/**
 * Callback function for CloudObject query operations.
 */
public abstract class FindCallback {
	public abstract void done(List<CloudObject> result,
						CloudException e);
}
