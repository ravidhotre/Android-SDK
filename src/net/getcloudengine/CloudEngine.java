package net.getcloudengine;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;


/**
 * This class needs to be instantiated and initialized by every app at the begginging 
 * of its execution, typically in the onCreate method of its main activity. 
 */
public class CloudEngine {
	
	private static final String TAG = "CloudEngine";
	private static String apiKey = null;
	private static String appId = null;
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
		if(ctx == null || key == null || app_id == null
				|| key == "" || app_id == ""){
			throw new CloudException("Invalid arguments provided");
		}
		apiKey = key;
		appId = app_id;
		app_context = ctx;
		applicationName = ctx.getResources().getString(R.string.app_name);
		initPushService(app_id);
		saveCredentials();
		CookieSyncManager syncManager = CookieSyncManager.createInstance(ctx);
		syncManager.sync();
		
	
	}
	
	public static String getApiKey(){
		return apiKey;
	}

	public static String getAppId(){
		return appId;
	}

	
	private static void saveCredentials(){
		
		SharedPreferences sharedPref = app_context.getSharedPreferences(
				PREFERENCE_FILE , Context.MODE_PRIVATE);
		
		String key = sharedPref.getString(API_KEY, null);
		String id = sharedPref.getString(APP_ID, null);
		
		if(key == null || id == null)
		{
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString(API_KEY, apiKey);
			editor.putString(APP_ID, appId);
			editor.commit();
		}
		
	}
	
	
	private static void initPushService(String app_id) {
		
		CloudEngineUtils utils = CloudEngineUtils.getInstance();
		
		if(!utils.isNetworkAvailable(app_context))
    	{
    		return;
    	}
		
		//Check if push service is running
		Log.d(TAG, "CloudEngine starting Push service");
		//Start Push notification service
		Intent intent = new Intent(app_context, CloudPushService.class);
		intent.putExtra("AppId", app_id);
		app_context.startService(intent);
		return;
		
	}
		
	
	
	public static Context getContext() {
		return app_context;
	}
	
	
}

