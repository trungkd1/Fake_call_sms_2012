package org.baole.fakelog.service;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class ProximityListener implements SensorEventListener {

	private static SensorManager mSensorManager;
	private static Sensor mProximity;
	private static ProximityListener mProximityListener;
	private static Intent mIntent;
	private static boolean mRegister = false;
	private Context mContext;
	private int mCounter;
	private int mCounterSetting;

	public ProximityListener(Context c) {
		mContext = c;
		SharedPreferences p = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		try {
			mCounterSetting = Integer.parseInt(p.getString("proximity", "3"));
		} catch (NumberFormatException e) {
			mCounterSetting = 3;
		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		Log.e("fake", "onAccuracyChanged: " + sensor.getName() + ", acuracy: "
				+ accuracy);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float distance = event.values[0];
		Log.e("fake", "distance: " + distance + "cm, acuracy: "
				+ event.accuracy);
		mCounter++;

		if (mCounter >= mCounterSetting) {

			mContext.sendBroadcast(mIntent);
			Log.e("fake", "activate call:  " + distance + "cm, acuracy: "
					+ event.accuracy);

			unregister(mContext);
		}
	}

	public static void create(Context c) {
		Log.e("fake", "create sensor listener");
		mSensorManager = (SensorManager) c
				.getSystemService(Context.SENSOR_SERVICE);

		List<Sensor> list = mSensorManager.getSensorList(Sensor.TYPE_PROXIMITY);
		for (Sensor s : list) {
			Log.e("fake", "sensor: " + s.getName() + ", " + s);
		}

		mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		mProximityListener = new ProximityListener(c);

	}

	public static void register(Context c, Intent intent) {
		if (mSensorManager != null && mRegister == false) {
			mIntent = intent;
			mRegister = true;
			Log.e("fake", "register sensor listener");
			mSensorManager.registerListener(mProximityListener, mProximity,
					SensorManager.SENSOR_DELAY_NORMAL);
			Toast.makeText(c, "Proximity Sensor is registered",
					Toast.LENGTH_LONG).show();
		}
	}

	private static void unregister(Context c) {
		if (mProximityListener != null) {
			Log.e("fake", "unregister sensor listener");
			mSensorManager.unregisterListener(mProximityListener);
			mRegister = false;
		}
	}
}