package net.getcloudengine;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

/**
 * Callback function for Push messages
 */
public class PushCallback implements java.io.Serializable {
	
	//
	private Intent intent = new Intent();
	
	/**
	 * Handle push messages received from the server. The default behaviour
	 * of this method is to display a notification in the notification area.
	 * If you need a custom behavior you can override or extend this function 
	 * by subclassing PushCallback. 
	 * 
	 * @param context The application context
	 * 
	 * @param message The message received from push notification
     * 
     */
	public void handleMessage(Context context, String message){
		DisplayNotification(context, message);
	}
	
	public void setActivity(Intent intent){
		this.intent  = intent;
	}
	
	private void DisplayNotification(Context context, String msg)
	{
		int notificationId = 3893;
		 String applicationName = context.getResources().getString(R.string.app_name);
		 long [] pattern = {0, 500};	
		 Uri sound_uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			NotificationCompat.Builder mBuilder =
			        new NotificationCompat.Builder(context)
			        .setSmallIcon(R.drawable.default_android_icon)
			        .setContentTitle(applicationName)
			        .setContentText(msg)
			        .setSound(sound_uri)
			        .setVibrate(pattern)
			        .setAutoCancel(true);
			
			
			PendingIntent resultPendingIntent = PendingIntent.getActivity(
								context, 0, this.intent, 0);
	
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager =
				    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				int mId = notificationId;
				// mId allows you to update the notification later on.
				mNotificationManager.notify(mId , mBuilder.build());
			
	}
}
