package net.getcloudengine;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


public class CloudEngineReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		CloudEngineUtils utils = CloudEngineUtils.getInstance();
		
		if(!utils.isNetworkAvailable(context))
    	{
    		return;
    	}
		else{
			
			SharedPreferences sharedPref = context.getSharedPreferences(
					CloudEngine.PREFERENCE_FILE , Context.MODE_PRIVATE);
			
			String apiKey = sharedPref.getString(CloudEngine.API_KEY, null);
			String appId = sharedPref.getString(CloudEngine.APP_ID, null);
			
			if(apiKey != null && appId !=null)
			{
				CloudEngine.initialize(context, apiKey, appId);
				CloudObject.syncServer(context);
			}
			
		}
    	

	}

}
