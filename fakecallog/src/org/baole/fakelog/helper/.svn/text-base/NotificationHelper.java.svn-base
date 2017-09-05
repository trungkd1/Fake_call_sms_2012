package org.baole.fakelog.helper;

import org.baole.fakelog.R;
import org.baole.fakelog.R.drawable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

public class NotificationHelper {
	public static final String TAG = "BlackListReceiver";
	protected static int mBlackListIdCounter = 2;

	public static void addMissedCallNotification(Context context, String name,
			String number, long date, Uri uri) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(ns);

		if (TextUtils.isEmpty(name)) {
			name = number;
		}

		int icon = R.drawable.stat_notify_missed_call;

		Notification notification = new Notification(icon, String.format(
				"Missed call from %s", name), date);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		Intent notificationIntent = new Intent(Intent.ACTION_VIEW, uri);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, name, number, contentIntent);

		mNotificationManager.notify(mBlackListIdCounter, notification);
		mBlackListIdCounter++;
	}

	public static void addSMSNotification(Context context, String name,
			String number, String msg, long date, Uri uri) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(ns);

		if (TextUtils.isEmpty(name)) {
			name = number;
		}

		int icon = R.drawable.stat_notify_sms;

		Notification notification = new Notification(icon, String.format(
				"%s: %s", name, msg), System.currentTimeMillis());
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
		notificationIntent.setType("vnd.android-dir/mms-sms");
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, name, msg, contentIntent);

		mNotificationManager.notify(mBlackListIdCounter, notification);
		mBlackListIdCounter++;
	}

	public static final Dialog createGoProDialog(final Activity context,
			final String pkg, int msgId) {
		AlertDialog.Builder builder;

		builder = new AlertDialog.Builder(context);
		builder.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle(R.string.app_name)
				.setMessage(msgId)
				.setPositiveButton(R.string.purchase_,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent pro = new Intent(Intent.ACTION_VIEW, Uri
										.parse(String.format(
												"market://details?id=%s", pkg)));
								context.startActivity(pro);
							}

						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog,
									final int id) {
								dialog.cancel();
							}
						});

		return builder.create();
	}
}