package net.getcloudengine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


public class CloudEngineBootupReceiver extends BroadcastReceiver {
    static final String TAG = "CloudEngineBroadcastReceiver";
    
    /**
     * Called when android boot process is completed. 
     * Initializes all necessary services.
     * 
     */
    @Override
    public void onReceive(Context context, Intent intent) {
    	
    	
    	SharedPreferences sharedPref = context.getSharedPreferences(
				CloudEngine.PREFERENCE_FILE , context.MODE_PRIVATE);
		
		String apiKey = sharedPref.getString(CloudEngine.API_KEY, null);
		String appId = sharedPref.getString(CloudEngine.APP_ID, null);
		
		if(apiKey != null && appId !=null)
		{
			CloudEngine.initialize(context, apiKey, appId);
		}
		
    }
     
}









