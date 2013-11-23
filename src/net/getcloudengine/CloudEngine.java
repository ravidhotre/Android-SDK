package net.getcloudengine;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;


/**
 * This class needs to be instantiated and initialized by every app at the begginging 
 * of its execution, typically in the onCreate method of its main activity. 
 */
public class CloudEngine {
	
	private static final String TAG = "CloudEngine";
	private static Context app_context = null;
	public static final String APP_ID = "AppId";
	public static final String API_KEY = "AppKey";
	public static final String PREFERENCE_FILE = "net.getcloudengine.PREFERENCE_FILE_KEY";
	static String applicationName;
	
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
		applicationName = ctx.getResources().getString(R.string.app_name);
		initPushService(app_id);
		//initDB();
		saveCredentials();
		
	}
	
	private static void saveCredentials(){
		
		SharedPreferences sharedPref = app_context.getSharedPreferences(
				PREFERENCE_FILE , app_context.MODE_PRIVATE);
		
		String apiKey = sharedPref.getString(API_KEY, null);
		String appId = sharedPref.getString(APP_ID, null);
		
		if(apiKey == null || appId == null)
		{
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString(API_KEY, CloudEngineUtils.getApiKey());
			editor.putString(APP_ID, CloudEngineUtils.getAppId());
			editor.commit();
		}
		
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

