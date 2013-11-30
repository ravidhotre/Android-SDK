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
	
	public void handleMessage(Context context, String message){
		DisplayNotification(context, message);
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
			        .setVibrate(pattern);
			PendingIntent resultPendingIntent = PendingIntent.getActivity(
								context, 0, new Intent(), 0);
	
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager =
				    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				int mId = notificationId;
				// mId allows you to update the notification later on.
				mNotificationManager.notify(mId , mBuilder.build());
			
	}
}
