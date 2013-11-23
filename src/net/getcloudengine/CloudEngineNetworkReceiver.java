package net.getcloudengine;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class CloudEngineNetworkReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		 
		
		if(!CloudEngineUtils.isNetworkAvailable(context))
    	{
    		return;
    	}
		else{
			String app_id = CloudEngineUtils.getAppId();
			CloudEngine.initPushService(app_id);
	    	// Save all pending requests to server
			CloudObject.syncServer();

		}
    	

	}

}
