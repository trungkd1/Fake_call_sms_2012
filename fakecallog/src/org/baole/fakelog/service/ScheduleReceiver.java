package org.baole.fakelog.service;

import java.sql.SQLException;

import org.baole.fakelog.FakeCallPageActivity;
import org.baole.fakelog.helper.DBAdapter;
import org.baole.fakelog.helper.LogHelper;
import org.baole.fakelog.helper.NotificationHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class ScheduleReceiver extends BroadcastReceiver {

	public static final String NAME = "name";
	public static final String NUMBER = "number";
	public static final String MESSAGE = "message";
	public static final String READ = "read";
	public static final String TYPE = "type";
	public static final String CALL_TYPE = "calltype";
	public static final String DURATION = "duration";
	public static final String DATE = "date";
	public static final String NOTIF = "nof";
	public static final String VOICE = "voice";

	public static final String PROXIMITY = "proximity";

	private DBAdapter adapter;
	public static int INCOMING = 1;
	public static int NOTIFICATION_SMS = 1;
	public static int NOTIFICATION_MISSED = 1;

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();

		boolean isProximity = false;
		if (bundle != null) {
			isProximity = bundle.getBoolean(PROXIMITY);
		}

		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			Intent service = new Intent();
			service.setClass(context, SchedulerService.class);
			context.startService(service);

		} else if (isProximity) {
			Intent service = new Intent();
			service.putExtras(bundle);
			service.setClass(context, SchedulerService.class);
			context.startService(service);

		} else {
			Log.e("fake", "come here");
			try {
				String name = bundle.getString(NAME);
				String number = bundle.getString(NUMBER);
				String message = bundle.getString(MESSAGE);
				int read = bundle.getInt(READ);
				int type = bundle.getInt(TYPE);
				int calltype = bundle.getInt(CALL_TYPE);
				int duration = bundle.getInt(DURATION);
				long datetime = System.currentTimeMillis();
				int nof = bundle.getInt(NOTIF);

				Log.e("fake", "come here: " + name + ", number: " + number
						+ ", calltype: " + calltype + ", type :" + type
						+ ", nof" + nof);

				adapter = new DBAdapter(context);
				if (!TextUtils.isEmpty(message)) {

					Uri uri = LogHelper.insertSMSEntry(context, number,
							message, datetime, read, type, true);

					if (nof == NOTIFICATION_SMS) {
						NotificationHelper.addSMSNotification(context, name,
								number, message, datetime, uri);
					}
					try {
						adapter.openWrite();
						int sms_id = bundle.getInt("sms_id");
						boolean result = adapter.RemoveSMS(sms_id);
						if (result == true)
							adapter.close();

					} catch (SQLException e) {
						e.printStackTrace();
					}

				} else {

					Uri uri = LogHelper.insertCallEntry(context, name, number,
							datetime, duration, calltype, true);

					if (nof == NOTIFICATION_MISSED) {
						NotificationHelper.addMissedCallNotification(context,
								name, number, datetime, uri);

					}

					if (calltype == INCOMING) {
						Intent j = intent;
						j.setClass(context, FakeCallPageActivity.class);
						j.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(j);
						Log.e("fake", "fake call: " + name + ", number: "
								+ number);

					}

					try {
						adapter.openWrite();
						int call_id = bundle.getInt("call_id");
						boolean result = adapter.RemoveCall(call_id);
						if (result == true)
							adapter.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}

				}

				adapter.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
