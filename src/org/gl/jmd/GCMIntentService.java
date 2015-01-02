package org.gl.jmd;

import org.gl.jmd.view.Accueil;

import com.google.android.gcm.GCMBaseIntentService;

import android.app.*;
import android.content.*;

public class GCMIntentService extends GCMBaseIntentService {

	public GCMIntentService() {
		super("1029438593914");
	}

	private static void generateNotification(Context context, String message) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		String title = context.getString(R.string.app_name);
		long when = System.currentTimeMillis();
		
		Notification notification = new Notification(R.drawable.ic_launcher, message, when);
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		Intent notificationIntent = new Intent(context, Accueil.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		notification.setLatestEventInfo(context, title, message, PendingIntent.getActivity(context, 0, notificationIntent, 0));
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		notificationManager.notify(0, notification);
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