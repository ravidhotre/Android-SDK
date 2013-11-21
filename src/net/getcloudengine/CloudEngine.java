package net.getcloudengine;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * This class needs to be instantiated and initialized by every app at the begginging 
 * of its execution, typically in the onCreate method of its main activity. 
 */
public class CloudEngine {
	
	private static final String TAG = "CloudEngine";
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
		initPushService(app_id);
		
	}
	
	
	public static void initPushService(String app_id) {
		
		//Check if push service is running
		Log.d(TAG, "CloudEngine starting Push service");
		//Start Push notification service
		Intent intent = new Intent(app_context, CloudPushService.class);
		intent.putExtra("AppId", app_id);
		app_context.startService(intent);
		return;
		

	}
	

	private static boolean isPushServiceRunning() {
	    ActivityManager manager = (ActivityManager) app_context.getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (CloudPushService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	
	public static Context getContext() {
		return app_context;
	}
	
	
}









