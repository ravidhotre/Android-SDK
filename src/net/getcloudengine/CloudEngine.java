package net.getcloudengine;
import android.content.Context;
import android.content.Intent;


/**
 * This class needs to be instantiated and initialized by every app at the begginging 
 * of its execution, typically in the onCreate method of its main activity. 
 */
public class CloudEngine {
	
	private static Context app_context = null;
	//private static Boolean authorized = false;

	/**
	 * Initialize the Cloudengine library functions and services.
	 * 
	 * @param ctx
     *            The application context object
     * 
     * @param key The REST API client key given to the user
     * 
     * @param appid The application id for current application
     * 
     */
	public static void initialize(Context ctx, String key, String app_id)
	{
		CloudEngineUtils.setApiKey(key);
		CloudEngineUtils.setAppId(app_id);
		app_context = ctx;
				
		//Start Push notification service
		Intent intent = new Intent(ctx, CloudPushService.class);
		ctx.startService(intent);
	}
	
	
	
	public static Context getContext() {
		return app_context;
	}
	
	
}









