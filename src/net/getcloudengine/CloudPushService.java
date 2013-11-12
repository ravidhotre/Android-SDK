
package net.getcloudengine;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONObject;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class CloudPushService extends IntentService {

	 static final String TAG = "CloudPushService";
	 SocketIO socket =  null;
	 private static ArrayList<String> subscriptions = new ArrayList<String>();
	 
	 
	 public CloudPushService() {
	      super("PushService");
	 }
	 
	 public void NotifyUser(String msg)
		{
		 String applicationName = getResources().getString(R.string.app_name);
			NotificationCompat.Builder mBuilder =
			        new NotificationCompat.Builder(this)
			        .setSmallIcon(R.drawable.default_android_icon)
			        .setContentTitle(applicationName)
			        .setContentText(msg);
	
			PendingIntent resultPendingIntent = PendingIntent.getActivity(
								getApplicationContext(), 0, new Intent(), 0);

			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager =
				    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				int mId = 3422;
				// mId allows you to update the notification later on.
				mNotificationManager.notify(mId , mBuilder.build());
			
		}
	 
	 
	 public SocketIO socket_call(String app_id)
	{
			final String TAG = "CloudPushService";
			String apikey = CloudEngineUtils.getApiKey();
			
			try {
				String host = CloudEndPoints.socketServer;
				
				socket = new SocketIO().addHeader("Authorization", "Token " + apikey);
								
				String appId = CloudEngineUtils.getAppId();
				socket.addHeader("AppId", appId);
				socket.connect(host, new IOCallback(){

							@Override
							public void on(String event, IOAcknowledge ack, Object... args) {
								
								Log.d(TAG, event + " event received. ");
								if ("push".equals(event) && args.length > 0)
								{
									String msg = (String) args[0];
									NotifyUser(msg);
								}						
							}

							@Override
							public void onConnect() {
								
								Log.d(TAG, "socket io connected to server");
							}

							@Override
							public void onDisconnect() {
								
								Log.d(TAG, "socket io disconnected");
								socket = null;
								stopSelf();
							}

							@Override
							public void onError(SocketIOException arg0) {
								
								Log.e(TAG, "error in connecting" + arg0.getMessage());
								arg0.printStackTrace();
								socket = null;
								// todo: stop self only on critical errors such as timeout error
								stopSelf();
							}

							@Override
							public void onMessage(String arg0, IOAcknowledge arg1) {
								
								Log.d(TAG, "text message received");
							}

							@Override
							public void onMessage(JSONObject arg0, IOAcknowledge arg1) {
								
								Log.d(TAG, "json message received");
								
							}
							
						});
				
				return socket;
			} catch (MalformedURLException e1) {
				
				e1.printStackTrace();
			}
			return null;
		}
	 
	 	
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "CloudPush Service initiated");
		String app_id = null;
		
		 if (intent.hasExtra("AppId")) {
			 	
			 	app_id = intent.getExtras().getString("AppId");
			 	Log.d(TAG, "starting push service for appid: " + app_id);
			 	//Check if we are already subscribed to this channel
			 	if(!subscriptions.contains(app_id))
			 	{
			 		subscriptions.add(app_id);
			 		socket_call(app_id);
			 	}
			 	else{
			 		Log.d(TAG, "Already subscribed for this app");
			 	}
		      
		    }
		
		
		
	/*	synchronized (this){
			try {
				wait(3000);
				if(!socket.isConnected())
				{
					Log.e(TAG, "Socket not connected");
					stopSelf();
				}
				else{
					String msg = String.format("socket connected with %s!!", socket.getTransport()); 
					Log.i(TAG, msg);
				}
					
			} catch (InterruptedException e) {
				
				//e.printStackTrace();
			}
		}*/
		
	
		}
		
	}




