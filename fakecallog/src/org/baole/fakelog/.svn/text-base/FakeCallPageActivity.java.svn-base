package org.baole.fakelog;

import com.anttek.common.dialog.AdDialog;
import com.anttek.phone.InCallScreen;
import com.anttek.widgets.InCallControls2;
import com.anttek.widgets.InCallControls2.OnTriggerListener;
import com.anttek.widgets.OnOffSwitchView;
import com.anttek.widgets.OnOffSwitchView.OnStatusSwitchListener;
import org.baole.ad.AdUtil;
import org.baole.fakelog.model.Configuration;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.anttek.coverflow.*;

import java.io.InputStream;
import java.util.Timer;

public class FakeCallPageActivity extends Activity implements OnClickListener,
		OnTriggerListener {
	private MediaPlayer mp;
	private MediaPlayer mp2;
	private TextView textNumber;
	private TextView textName;
	private TextView mtexCountMiliseconds;
	private boolean mCheckCallPref;
	private static boolean mCheckVibrationCallPref = false;
	private String mRingtoneCallPref;
	private Vibrator v;
	private boolean flag = false;
	private ImageView imageEndcallReCeive;
	private long starttime = 0;
	private Bundle bundle;
	private WakeLock mWakeLock = null;
	private Configuration mConfig;

	private final int IMG_RECEIVE_ANDROID_2_2 = 0;
	private final int IMG_RECEIVE_SAMSUNG = 1;
	private final int IMG_RECEIVE_ANDROID_1_6 = 2;
	private final int IMG_RECEIVE_FAKE = 3;
	private final int IMG_RECEIVE_HTC = 4;
	private final int IMG_RECEIVE_SONY_1 = 5;
	private final int IMG_RECEIVE_SONY_2 = 6;
	private final int IMG_RECEIVE_SONY_3 = 7;
	private final int IMG_RECEIVE_SONY_4 = 8;
	private final int IMG_RECEIVE_SONY_5 = 9;
	private final int IMG_RECEIVE_SONY_6 = 10;
	private final int IMG_RECEIVE_ANDROID_4_0 = 11;
	
	private static final String NUMBER = "number";
	private static final String NAME = "name";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		reloadPrefs(this);

		setContentView();

		boolean isAndroid_4_0 = setNameandPhoneNumber();
		if (isAndroid_4_0) {
			bundle = getIntent().getExtras();

			if (mCheckCallPref == true) {
				if (mCheckVibrationCallPref == true) {

					v = (Vibrator) getSystemService(FakeCallPageActivity.this.VIBRATOR_SERVICE);
					long[] pattern = { 200, 200, 500 };
					v.vibrate(pattern, 0);
					flag = true;
				}
				mp = MediaPlayer.create(this, Uri.parse(mRingtoneCallPref));
				startVibration();

			} else {
				Uri uri = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
				mp = MediaPlayer.create(this, uri);
				startVibration();
			}
			AdDialog.showPromotingAppGuardDialog(this, AdUtil.hasAd());
		}
	}

	private InCallControls2 inCallControls;

	// Keygard for incoming call
	private ViewGroup callInfoPanel;
	private Timer quitTimer;
	// Dnd views
	private ImageView endCallTarget;
	private ImageView holdTarget, answerTarget;
	// private Button middleAddCall;

	private static DisplayMetrics METRICS;
	private Bundle resolvedInfos = null;
	PackageManager pm;

	private void setContentView() {
		Log.e("FAKE ", String.valueOf(mConfig.mId_image));

		switch (mConfig.mId_image) {
		case IMG_RECEIVE_ANDROID_4_0:
			Intent intent = new Intent(getApplicationContext(),
					InCallScreen.class);
			intent.putExtra(NAME, getIntent().getExtras().getString(NAME)) ;
			intent.putExtra(NUMBER, getIntent().getExtras().getString(NUMBER)) ;
			intent.putExtra("CallPref",mCheckCallPref);
			intent.putExtra("VirationCallPref",mCheckVibrationCallPref);
			intent.putExtra("RingtoneCall", mRingtoneCallPref);
			startActivity(intent);
			break;
		case IMG_RECEIVE_ANDROID_2_2:
		case IMG_RECEIVE_ANDROID_1_6:
		case IMG_RECEIVE_SAMSUNG:
			setContentView(R.layout.in_call_main2);
			setupInCallView();
			break;

		case IMG_RECEIVE_FAKE:
			setContentView(R.layout.in_call_main4);
			findViewById(R.id.call).setOnClickListener(this);
			findViewById(R.id.reject).setOnClickListener(this);
			break;

		case IMG_RECEIVE_HTC:
			setContentView(R.layout.in_call_main5);
			findViewById(R.id.call).setOnClickListener(this);
			findViewById(R.id.reject).setOnClickListener(this);
			break;

		case IMG_RECEIVE_SONY_1:
		case IMG_RECEIVE_SONY_2:
		case IMG_RECEIVE_SONY_3:
		case IMG_RECEIVE_SONY_4:
		case IMG_RECEIVE_SONY_5:
		case IMG_RECEIVE_SONY_6:
			setContentView(R.layout.in_call_main6);

			LinearLayout layout = (LinearLayout) findViewById(R.id.layout6);

			switch (mConfig.mId_image) {
			case IMG_RECEIVE_SONY_1:
				layout.setBackgroundResource(R.drawable.background_sony);
				break;
			case IMG_RECEIVE_SONY_2:
				layout.setBackgroundResource(R.drawable.background_sony2);
				break;
			case IMG_RECEIVE_SONY_3:
				layout.setBackgroundResource(R.drawable.background_sony3);
				break;
			case IMG_RECEIVE_SONY_4:
				layout.setBackgroundResource(R.drawable.background_sony4);
				break;
			case IMG_RECEIVE_SONY_5:
				layout.setBackgroundResource(R.drawable.background_sony5);
				break;
			case IMG_RECEIVE_SONY_6:
				layout.setBackgroundResource(R.drawable.background_sony6);
				break;
			}

			OnOffSwitchView onoff = (OnOffSwitchView) this
					.findViewById(R.id.onoff);
			onoff.setUp();

			onoff.setStatusSwitchListener(new OnStatusSwitchListener() {
				@Override
				public void onSwitch(boolean on) {
					if (on) {
						onAnserCall();
					} else {
						finish();
						endcall();
					}
				}
			});

			break;
		}

	}

	private void setupInCallView() {
		pm = getPackageManager();

		METRICS = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(METRICS);

		takeKeyEvents(true);

		// remoteContact = (TextView) findViewById(R.id.remoteContact);
		inCallControls = (InCallControls2) findViewById(R.id.inCallControls);
		inCallControls.setOnTriggerListener(this);

		callInfoPanel = (ViewGroup) findViewById(R.id.callInfoPanel);

		endCallTarget = (ImageView) findViewById(R.id.dropHangup);
		endCallTarget.getBackground().setDither(true);
		holdTarget = (ImageView) findViewById(R.id.dropHold);
		holdTarget.getBackground().setDither(true);
		answerTarget = (ImageView) findViewById(R.id.dropAnswer);
		answerTarget.getBackground().setDither(true);
		if (quitTimer == null) {
			quitTimer = new Timer("Quit-timer");
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		powerScreen();
	}

	@Override
	public void onBackPressed() {
		endcall();
		super.onBackPressed();
	}

	private void startVibration() {
		if (mp != null) {
			mp.start();
			mp.setLooping(true);
		}
	}

	private void cancelAll() {
		if (mp != null) {
			mp.release();
			mp = null;
		}

		if (flag == true)
			v.cancel();
	}

	private void powerScreen() {
		PowerManager pm = (PowerManager) this
				.getSystemService(Context.POWER_SERVICE);

		if (pm.isScreenOn() == false) {
			mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
					| PowerManager.ACQUIRE_CAUSES_WAKEUP
					| PowerManager.ON_AFTER_RELEASE, getPackageName()
					+ ".fakecall");
			mWakeLock.acquire();

			KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
			mLock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);

			mLock.disableKeyguard();

		}
	}

	private KeyguardLock mLock = null;

	private void reloadPrefs(Context context) {

		mConfig = Configuration.getInstance();
		mConfig.init(context);
		mCheckCallPref = mConfig.mCallPref;
		mRingtoneCallPref = mConfig.mRingtoneCall;
		mCheckVibrationCallPref = mConfig.mVirationCallPref;

	}

	Handler mHandler = new Handler();
	Runnable mRun = new Runnable() {

		@Override
		public void run() {
			long millis = System.currentTimeMillis() - starttime;
			int seconds = (int) (millis / 1000);
			int minutes = seconds / 60;
			seconds = seconds % 60;

			mtexCountMiliseconds.setText(String.format("%02d:%02d", minutes,
					seconds));

			mHandler.postDelayed(this, 500);
		}
	};
	private Bitmap mPhoto;

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.imageEndcallRecieve || v.getId() == R.id.reject) {
			endcall();
		} else if (v.getId() == R.id.call) {
			onAnserCall();
		}
	}

	private void unlock(int layoutId) {
		setContentView(layoutId);
		cancelAll();
		setNameandPhoneNumber();

		imageEndcallReCeive = (ImageView) findViewById(R.id.imageEndcallRecieve);
		if (imageEndcallReCeive != null) {
			imageEndcallReCeive.setOnClickListener(this);
		}
		mtexCountMiliseconds = (TextView) findViewById(R.id.textCount);

		starttime = System.currentTimeMillis();
		mHandler.postDelayed(mRun, 0);

		// cancelAll();
		String pathVoice = Configuration.AUDIO_DIR + bundle.getString("voice");
		mp2 = MediaPlayer.create(FakeCallPageActivity.this,
				Uri.parse(pathVoice));
		if (mp2 != null) {
			mp2.start();

		}
	}

	private boolean setNameandPhoneNumber() {
		try {
			textNumber = (TextView) findViewById(R.id.textNumber);
			textName = (TextView) findViewById(R.id.textName);
			Intent intent = getIntent();
			Bundle bundle = intent.getExtras();

			String number = bundle.getString(NUMBER);
			textNumber.setText(number);
			textName.setText(bundle.getString(NAME));

			ImageView photo = (ImageView) findViewById(R.id.imageView1);

			mPhoto = loadPhoto(number);

			if (mPhoto != null) {
				photo.setImageBitmap(mPhoto);
			}
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	Bitmap loadPhoto(String number) {
		try {

			Uri uri1 = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
					Uri.encode(number));
			Cursor cursor = getContentResolver().query(uri1,
					new String[] { PhoneLookup._ID },
					PhoneLookup.NUMBER + " = ?", new String[] { number }, null);
			try {
				if (cursor.moveToFirst()) {
					Uri uri = ContentUris.withAppendedId(Contacts.CONTENT_URI,
							cursor.getLong(0));
					InputStream is = Contacts.openContactPhotoInputStream(
							getContentResolver(), uri);
					return BitmapFactory.decodeStream(is);
				}
			} finally {
				cursor.close();
			}

		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}

	private void endcall() {
		Log.e("FL", "end call");
		cancelAll();

		if (mLock != null)
			mLock.reenableKeyguard();

		if (mWakeLock != null)
			mWakeLock.release();

		finish();
	}

	@Override
	public void onTrigger(int whichAction) {
		try {
			switch (whichAction) {
			case TAKE_CALL: {
				onAnserCall();
				break;
			}
			case DECLINE_CALL:
				onCancelCall();
				break;
			}
		} catch (Exception e) {
		}
	}

	private void onCancelCall() {
		endcall();
	}

	private void onAnserCall() {
		Log.e("FAKE", String.valueOf(mConfig.mId_image));
		switch (mConfig.mId_image) {
		case IMG_RECEIVE_ANDROID_2_2:
			unlock(R.layout.receive1);
			break;
		case IMG_RECEIVE_ANDROID_1_6:
			unlock(R.layout.receive3);
			break;
		case IMG_RECEIVE_SAMSUNG:
			unlock(R.layout.receive2);
			break;

		case IMG_RECEIVE_FAKE:
			unlock(R.layout.receive4);
			break;

		case IMG_RECEIVE_HTC:
			unlock(R.layout.receive5);
			break;

		case IMG_RECEIVE_SONY_1:
		case IMG_RECEIVE_SONY_2:
		case IMG_RECEIVE_SONY_3:
		case IMG_RECEIVE_SONY_4:
		case IMG_RECEIVE_SONY_5:
		case IMG_RECEIVE_SONY_6:
			unlock(R.layout.receive6);
			break;
		}

	}

	public Drawable getDrawableResource(String name) {
		if (resolvedInfos != null) {
			String drawableName = resolvedInfos.getString(name);
			if (drawableName != null) {

				String packageName = drawableName.split(":")[0];

				PackageInfo pInfos;
				try {
					pInfos = pm.getPackageInfo(packageName, 0);
					Resources remoteRes = pm
							.getResourcesForApplication(pInfos.applicationInfo);
					int id = remoteRes.getIdentifier(drawableName, null, null);
					return pm.getDrawable(pInfos.packageName, id,
							pInfos.applicationInfo);
				} catch (NameNotFoundException e) {
				}
			} else {
			}
		} else {
		}
		return null;
	}

}
