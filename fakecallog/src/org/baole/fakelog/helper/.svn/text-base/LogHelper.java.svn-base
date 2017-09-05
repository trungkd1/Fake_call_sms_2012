package org.baole.fakelog.helper;

import org.baole.fakelog.FakeLogActivity;
import org.baole.fakelog.model.Configuration;

import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.util.Log;
import android.widget.Toast;

public class LogHelper {

	private static String mRingtoneSMSPref;
	private static boolean mCheckVibrationSMSPref;
	private static boolean mCheckSMSPref;
	protected static int mBlackListIdCounter = 2;

	private static boolean mCheckCallPref;
	private static String mRingtoneCallPref;

	public static Uri insertCallEntry(Context context, String name,
			String number, long date, int cDuration, int cType, boolean ring) {
		int FLAG = 0;
		ContentResolver contentResolver = context.getContentResolver();

		if (cType == CallLog.Calls.MISSED_TYPE) {
			cDuration = 0;
			FLAG = 1;

		}
		// Log.e("ringtone1", mRingtoneSMSPref);
		ContentValues values = new ContentValues();
		values.put(CallLog.Calls.NUMBER, number);
		values.put(CallLog.Calls.DATE, date);
		values.put(CallLog.Calls.DURATION, cDuration);
		values.put(CallLog.Calls.TYPE, cType);
		values.put(CallLog.Calls.NEW, 1);
		values.put(CallLog.Calls.CACHED_NAME, name);
		values.put(CallLog.Calls.CACHED_NUMBER_TYPE, 0);
		values.put(CallLog.Calls.CACHED_NUMBER_LABEL, "");
		Uri uri = contentResolver.insert(CallLog.Calls.CONTENT_URI, values);

		if (ring) {
			if (FLAG == 1) {
				reloadCallPrefs(context);
				if (mCheckCallPref == true) {

					MediaPlayer mp = MediaPlayer.create(context,
							Uri.parse(mRingtoneCallPref));
					mp.start();
					try {
						Thread.sleep(2000);
						mp.release();
						mp = null;
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
				} else {
					Uri uri1 = RingtoneManager
							.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
					MediaPlayer mp = MediaPlayer.create(context, uri1);
					mp.start();
					try {
						Thread.currentThread().sleep(2000);
						mp.release();
						mp = null;
					} catch (InterruptedException e) {

						e.printStackTrace();
					}

				}
			}
		}

		return uri;
	}

	public static Uri insertSMSEntry(Context context, String number,
			String message, long date, int read, int type, boolean ring) {
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification mNotification = new Notification();
		reloadSMSPrefs(context);
		if (mCheckSMSPref == true) {
			mNotification.sound = Uri.parse(mRingtoneSMSPref);
			if (mCheckVibrationSMSPref == true) {
				mNotification.defaults = Notification.DEFAULT_VIBRATE;
			}
		} else {
			mNotification.defaults = Notification.DEFAULT_VIBRATE
					| Notification.DEFAULT_SOUND;
		}

		mNotificationManager.notify(mBlackListIdCounter, mNotification);
		mBlackListIdCounter++;
		ContentResolver contentResolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		values.put("address", number);
		values.put("date", date);
		values.put("read", read);
		values.put("type", type);
		values.put("body", message);
		Uri smsUri = Uri.parse("content://sms");
		Uri uri = contentResolver.insert(smsUri, values);
		return uri;
	}

	private static void reloadSMSPrefs(Context context) {
		Configuration config = Configuration.getInstance();
		config.init(context);
		mCheckSMSPref = config.mSMSPref;
		mCheckVibrationSMSPref = config.mVirationSMSPref;
		mRingtoneSMSPref = config.mRingtoneSMS;

	}

	private static void reloadCallPrefs(Context context) {
		Configuration config = Configuration.getInstance();
		config.init(context);
		mCheckCallPref = config.mCallPref;
		mRingtoneCallPref = config.mRingtoneCall;

	}

	public static void displayToast(Context context, String msgText) {
		Toast.makeText(context, msgText, Toast.LENGTH_SHORT).show();
	}

}
