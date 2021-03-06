
package net.getcloudengine;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.os.Message;
import android.util.Log;



public class CloudPushService extends Service {
	  private Looper mServiceLooper;
	  private ServiceHandler mServiceHandler;
	  private static final String TAG = "CloudPushService";
	  private int maxAttempts = 3;
	  private final static String pushCallbackFilename = "PushCallback.ser";
	  private final static String activityFilename = "Activity.ser";
	  
	  private int attemptInterval = 60000;		// Around the same as heartbeat timeout of the server
	  private static PushCallback callback = new PushCallback();	//Default push callback handler
	  private static HashMap<String, SocketIO> subscriptions = new HashMap<String, SocketIO>();
	  private STATE state = STATE.DISCONNECTED;
	  
	  private enum STATE {
		  CONNECTED,
		  CONNECTION_IN_PROGRESS,
		  DISCONNECTED
		  
	  };
	  
	  
	  /**
		 * This method allows you to define which activity of
		 * the app will be invoked when the user clicks the
		 * notification message 
		 * 
		 * @param context The application's context
	     *            
	     * @param activity The Class of the activity that needs to be 
	     * 	invoked
	     * 
		 * @throws CloudException If invalid parameters are passed 
	     * 
	     */
	  public static void setActivity(Context context, Class<? extends Activity> activity){
		  
		  if(activity == null){
			  throw new CloudException("Invalid activity class");
		  }
		  
		  Intent intent = new Intent(context, activity);
		  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		  callback.setActivity(intent);
		  
		  File file = new File(context.getFilesDir(), activityFilename);
		  
		  if(file.exists()){
			  // Avoid writing to the file every time the app
			  // is opened.
			  return;
		  }
		  
		  // Check if we need to save custom callback
		  //Save any custom callbacks to disc
		  try
	      {
	         FileOutputStream fileOut =
	        		 context.openFileOutput(activityFilename, Context.MODE_PRIVATE);
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(activity);
	         out.close();
	         fileOut.close();	
	      }catch(IOException e)
	      {
	          e.printStackTrace();
	      }
	  	  
	  }
	  
	  /**
		 * This method allows you to install a custom callback function
		 * that will be invoked when the user clicks a notification
		 * message. 
		 * 
		 * @param context The application's context
	     *            
	     * @param cbk The callback object which needs to be installed. The object
	     * must be a subclass of net.getcloudengine.PushCallback. You may 
	     * extend or override the default handler function of the PushCallback
	     * object by defining handleMessage method of the object. The default 
	     * handleMessage method only displays the notification in the notification area
	     * when a message is received. The PushCallback object provided must satisfy 
	     * certain restrictions - 
	     * 1. The object must be serializable i.e. it should implement java.io.Serializable interface
	     * 2. The handleMessage method does not run in the UI thread
	     * 3. It is recommended to keep the handleMessage function as short as possible. 
	     * If you need to do blocking I/O such as networking activities, 
	     * it is recommended to spawn a separate thread for that purpose. 
	     * 4. If you're declaring the handler class inside an Activity or a container class, 
	     * please declare the class as static in order to make it Serializable
	     * 
		 * @throws CloudException If invalid parameters are passed 
	     * 
	     */
	  public static void installCallback(Context context, PushCallback cbk){
		  
		  if(context == null || cbk == null){
			  throw new CloudException("Invalid parameters provided");
		  }
		  
		  File file = new File(context.getFilesDir(), pushCallbackFilename);
			if(file.exists())
			{
				// Avoid writing to the file every time the app
				  // is opened.
				  return;
			}
			
			callback = cbk;
 			
			// Save custom callback to disc
			  try{
		         FileOutputStream fileOut =
		        		 context.openFileOutput(pushCallbackFilename, Context.MODE_PRIVATE);
		       	
		         ObjectOutputStream out = new ObjectOutputStream(fileOut);
		         out.writeObject(CloudPushService.callback);
		         out.close();
		         fileOut.close();	
		      }catch(IOException e)
		      {
		          e.printStackTrace();
		      }
	 		
	  }
	  
	  private SocketIO socket_call(String app_id)
		{
				String apiKey = CloudEngine.getApiKey();
				String appId = CloudEngine.getAppId();
				final Context context = getApplicationContext();
				if(apiKey == null || apiKey == "" || appId == null || appId == "")
					return null;
				
				try {
					
					String host = CloudEndPoints.socketServer;
					SocketIO socket = new SocketIO();
					subscriptions.put(app_id, socket);
					socket.addHeader("Authorization", "Token " + apiKey);
					socket.addHeader("AppId", appId);
					socket.connect(host, new IOCallback(){ 

								@Override
								public void on(String event, IOAcknowledge ack, Object... args) {
									
									Log.d(TAG, event + " event received. ");
									if ("push".equals(event) && args.length > 0)
									{
										String msg = (String) args[0];
										callback.handleMessage(context, msg);
										
									}						
								}

								@Override
								public void onConnect() {
									
									Log.d(TAG, "socket io connected to server");
									state = STATE.CONNECTED;
								}

								@Override
								public void onDisconnect() {
									
									Log.d(TAG, "socket io disconnected");
									state = STATE.DISCONNECTED;
								}

								@Override
								public void onError(SocketIOException exception) {
									
									String msg = exception.getMessage();
									Log.e(TAG, "Error in connecting. " + msg);
									exception.printStackTrace();
								}

								@Override
								public void onMessage(String arg0, IOAcknowledge arg1) {
									
								}

								@Override
								public void onMessage(JSONObject arg0, IOAcknowledge arg1) {
									
									
								}
								
							});
						return socket;
					
				}
				catch (Exception e)
				{
					e.printStackTrace();
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
	    	  SocketIO socket = null;
	    	  Bundle data = msg.getData();
	    	  String app_id = data.getString("AppId");
	    	  int attempts = 0;
	    	  while(attempts < maxAttempts)
	    	  {
	    		  Log.d(TAG, "trying to connect to socketio server...");
	    		 socket  = socket_call(app_id);
	    	  
	  	    	 //Wait for either socket to be connected
	  			 if(state != STATE.CONNECTED)
	  			 {
	  				try {
	  					Thread.sleep(attemptInterval);	
	  				} catch (InterruptedException e) {
	  				}
	  			 }
	  			
	  			 // Still not connected?
	  			if(state != STATE.CONNECTED){
	  				 Log.d(TAG, "Attempting again..");
					attempts += 1;
					continue;		// try again
				}
	  			 else{
	  				 break;
	  			 }
	  			 
	    	  }
	    	  
	    	  if(state != STATE.CONNECTED){
	    		  
	    		  // If state is left in STATE.CONNECTION_IN_PROGRESS,
	    		  // further attempts to connect will be blocked.
	    		  state = STATE.DISCONNECTED;
	    		  Log.e(TAG, "Socket not connected. Giving up...");
	    	  }
	    	
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
	      
		  String appId = null;
		  Bundle data = new Bundle();
		  boolean connect = true;
		  
		  //Read app id from disc
		  SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
		  CloudEngine.PREFERENCE_FILE , Context.MODE_PRIVATE);
		  appId = sharedPref.getString(CloudEngine.APP_ID, null);
		  
		  if (appId != null) {
			  
			  	//Check if we are already subscribed to this channel
			 	if(subscriptions.containsKey(appId))
			 	{			
			 		SocketIO socket = subscriptions.get(appId);
			 		if( socket != null && socket.isConnected()){
			 			connect = false;
			 			Log.d(TAG, "Already subscribed. Not connecting push service");
			 		}
			 	}
			 	
			 	if(connect){
			 		
			 		manageCustomCallback();
			 		installActivity();
			 		// Start the service if no connection is in progress
			 		if( state == STATE.DISCONNECTED )
			 		{
			 			Message msg = mServiceHandler.obtainMessage();
				        data.putString("AppId", appId);
				        msg.setData(data);
				        mServiceHandler.sendMessage(msg);
				        state = STATE.CONNECTION_IN_PROGRESS;
			 		}			 		
			 	}
			 	
		   }else{
			   Log.d(TAG, "Push service didn't find app id.");
		   }
	      return START_STICKY;
	  }
	  
	  private void installActivity(){
		  
		// check if we need to install custom handler
	 		File file = new File(getFilesDir(), activityFilename);
	 		if(file.exists())
	 		{
	 			FileInputStream fileIn = null;
	 			ObjectInputStream inputStream = null;
				try {
					fileIn = new FileInputStream(file.getAbsolutePath());
					inputStream = new ObjectInputStream(fileIn);
					Class<? extends Activity> activity = (Class<? extends Activity>) inputStream.readObject();
					setActivity( getApplicationContext(), activity);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				finally{
					
						try{
							if(inputStream != null)
								inputStream.close();
							
							if(fileIn != null)
								fileIn.close();
						}
						catch(Exception e){
						}
				}
	 		}
		  
	  }

	  private void manageCustomCallback(){
		  
		// check if we need to install custom handler
	 		File file = new File(getFilesDir(), pushCallbackFilename);
	 		if(file.exists())
	 		{
	 			 FileInputStream fileIn = null;
	 			ObjectInputStream inputStream = null;
				try {
					fileIn = new FileInputStream(file.getAbsolutePath());
					inputStream = new ObjectInputStream(fileIn);
					CloudPushService.callback = (PushCallback) inputStream.readObject();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				finally{
					
						try{
							if(inputStream != null)
								inputStream.close();
							
							if(fileIn != null)
								fileIn.close();
						}
						catch(Exception e){
						}
				}
	 		}
	  }
	  
	  
	  @Override
	  public IBinder onBind(Intent intent) {
	      return null;
	  }
	  
	  @Override
	  public void onDestroy() { 
		  
	  }
}




