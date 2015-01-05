package org.gl.jmd;

import org.gl.jmd.view.Accueil;

import com.google.android.gcm.GCMBaseIntentService;

import android.app.*;
import android.content.*;
import android.support.v4.app.NotificationCompat;

public class GCMIntentService extends GCMBaseIntentService {

	public GCMIntentService() {
		super("1029438593914");
	}

	private static void generateNotification(Context context, String message) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		String title = context.getString(R.string.app_name);
		long when = System.currentTimeMillis();
		
		Intent notificationIntent = new Intent(context, Accueil.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		PendingIntent pendIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
			.setSmallIcon(R.drawable.ic_launcher)
			.setContentTitle(title)
			.setContentIntent(pendIntent)
			.setContentText(message)
			.setWhen(when)
			.setDefaults(Notification.DEFAULT_ALL) 
			.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
		
		notificationManager.notify(0, builder.build());
	}

	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onMessage(Context arg0, Intent arg1) {
		generateNotification(arg0, arg1.getStringExtra("message"));
	}

	@Override
	protected void onRegistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
	}
}