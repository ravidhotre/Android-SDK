package net.getcloudengine;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class CloudEngineNetworkReceiver extends BroadcastReceiver {

	static final String TAG = "CloudEngineNetworkReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		 
		Log.i(TAG, "Network state change broadcast received");
		
		if(!CloudEngineUtils.isNetworkAvailable(context))
    	{
			Log.i(TAG, "Network not connected");
    		return;
    	}
		else{
			Log.i(TAG, "Network connected");
		}
    	
    	// Save all pending requests to server
		CloudObject.syncServer();		

	}

}
