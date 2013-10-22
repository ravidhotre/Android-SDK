
package net.getcloudengine;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import java.net.MalformedURLException;
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
	 
	 public CloudPushService() {
	      super("PushService");
	 }
	 
	 public void NotifyUser(String msg)
		{
			NotificationCompat.Builder mBuilder =
			        new NotificationCompat.Builder(this)
			        .setSmallIcon(R.drawable.default_android_icon)
			        .setContentTitle("Notification")
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
	 
	 
	 public SocketIO socket_call()
	{
			final String TAG = "Pushproject";
			String apikey = CloudEngineUtils.getApiKey();
			SocketIO socket =  null;
			try {
				String host = CloudEndPoints.socketServer;
				socket = new SocketIO().addHeader("Authorization", "Token " + apikey);
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
								
							}

							@Override
							public void onError(SocketIOException arg0) {
								
								Log.e(TAG, "error in connecting" + arg0.getMessage());
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
		Log.d(TAG, "Service initiated");
		socket_call();
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




