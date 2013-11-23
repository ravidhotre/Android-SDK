
package net.getcloudengine;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import org.json.JSONObject;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;



public class CloudPushService extends Service {
	  private Looper mServiceLooper;
	  private ServiceHandler mServiceHandler;
	  static final String TAG = "CloudPushService";
	  SocketIO socket =  null;
	  int notificationId = 3422;
	  private static HashMap<String, SocketIO> subscriptions = new HashMap<String, SocketIO>();
	  
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
				int mId = notificationId;
				// mId allows you to update the notification later on.
				mNotificationManager.notify(mId , mBuilder.build());
			
		}
	  
	  public SocketIO socket_call(String app_id)
		{
				final String TAG = "CloudPushService";
				String apikey = CloudEngineUtils.getApiKey();
				
				try {
					String host = CloudEndPoints.socketServer;
					
					socket = new SocketIO();
					
					subscriptions.put(app_id, socket);
					
					socket.addHeader("Authorization", "Token " + apikey);
									
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
									//stopSelf();
								}

								@Override
								public void onError(SocketIOException arg0) {
									
									Log.e(TAG, "error in connecting. " + arg0.getMessage());
									arg0.printStackTrace();
									// todo: stop self only on critical errors such as timeout error
									//stopSelf();
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

	  // Handler that receives messages from the thread
	  private final class ServiceHandler extends Handler {
	      
		  public ServiceHandler(Looper looper) {
	          super(looper);
	      }
		  
	      @Override
	      public void handleMessage(Message msg) {
	    	  
	    	  Bundle data = msg.getData();
	    	  String app_id = data.getString("AppId");
	    	  socket_call(app_id);
	      }
	  }

	  @Override
	  public void onCreate() {
	    
	    HandlerThread thread = new HandlerThread("ServiceStartArguments",
	            Process.THREAD_PRIORITY_BACKGROUND);
	    thread.start();
	    
	    // Get the HandlerThread's Looper and use it for our Handler 
	    mServiceLooper = thread.getLooper();
	    mServiceHandler = new ServiceHandler(mServiceLooper);
	  }

	  @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
	      
		  String app_id = null;
		  Bundle data = new Bundle();
		  boolean connect = true;
		  
		  if (intent.hasExtra("AppId")) {
			  
			 	app_id = intent.getExtras().getString("AppId");
			 	//Check if we are already subscribed to this channel
			 	if(subscriptions.containsKey(app_id))
			 	{			
			 		SocketIO socket = subscriptions.get(app_id);
			 		if( socket != null && socket.isConnected())
			 			connect = false;
			 		
			 	}
			 	
			 	if(connect){
			 		Message msg = mServiceHandler.obtainMessage();
			        data.putString("AppId", app_id);
			        msg.setData(data);
			        mServiceHandler.sendMessage(msg);
			 	}
			 	
		   }
		  
	      return START_STICKY;
	  }

	  @Override
	  public IBinder onBind(Intent intent) {
	      return null;
	  }
	  
	  @Override
	  public void onDestroy() { 
	  }
}




