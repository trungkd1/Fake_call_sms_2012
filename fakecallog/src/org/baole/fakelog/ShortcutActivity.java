package org.baole.fakelog;

import android.app.Activity;
import android.os.Bundle;

public class ShortcutActivity extends Activity {

	public static final String KEY_INCREASE = "_in";
	public static final String KEY_DURATION = "_du";
	public static final String KEY_DATE = "_da";
	public static final String KEY_NAME = "_na";
	public static final String KEY_NUMBER = "_nu";
	public static final String KEY_CALLTYPE = "_ct";
	public static final String KEY_NOTIF = "_no";
	public static final String KEY_CUSTOM = "_cu";
	public static final String KEY_DATETIME = "_dt";
	public static final String KEY_RECORD = "_re";
	public static final String KEY_MESAAGE = "_me";
	public static final String KEY_READ = "_re";
	public static final String KEY_TYPE = "_ty";
	public static final String KEY_IS_CALL = "_ic";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int increase = getIntent().getIntExtra(KEY_INCREASE, 0);
		long date = getIntent().getLongExtra(KEY_DATE, 0);
		String datetime = getIntent().getStringExtra(KEY_DATETIME);
		boolean isCustom = getIntent().getBooleanExtra(KEY_CUSTOM, false);
		boolean notif = getIntent().getBooleanExtra(KEY_NOTIF, false);
		String number = getIntent().getStringExtra(KEY_NUMBER);
		String name = getIntent().getStringExtra(KEY_NAME);

		if (getIntent().getBooleanExtra(KEY_IS_CALL, false)) {

			int duration = getIntent().getIntExtra(KEY_DURATION, 0);
			String record = getIntent().getStringExtra(KEY_RECORD);
			int callType = getIntent().getIntExtra(KEY_CALLTYPE, 0);

			FakeLogActivity.onProcessCallItem(this, increase, duration, date,
					name, number, callType, notif, isCustom, datetime, record);
		} else {
			int read = getIntent().getIntExtra(KEY_READ, 0);
			int type = getIntent().getIntExtra(KEY_TYPE, 0);
			String message= getIntent().getStringExtra(KEY_MESAAGE);
			
			FakeLogActivity.onProcessSMSItem(this, increase, date, name,
					number, message, read, type, notif, isCustom, datetime);
		}
		finish();
	}

}
