
package org.baole.fakelog.model;

import com.anttek.common.pref.MCStringPreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class Configuration {

    public static final String KEY_DIAL_NUMBER = "dial_number";
    public static final String AUDIO_DIR;
    private static final String SHOW_PROXIMITY_GUIDE = "show_proxi_guide";
    private static final String PROXIMITY_TRY = "proxi_try";
    private static Configuration instance = null;
    private static Context mcontext;

    private final static String _SMS_PREF = "SMSPref";
    private final static String _CALL_PREF = "CallPref";
    private final static String VIBRATION_SMS_PREF = "VirationSMSPref";
    private final static String VIBRATION_CALL_PREF = "VirationCallPref";
    private final static String RINGTONES_SMS_PREF = "RingtoneSMS";
    private final static String RINGTONES_CALL_PREF = "RingtoneCall";
    private final static String ID_IMAGE = "mId_image";
    private MCStringPreference mDialNumber;

    private String mdefault_string = "DEFAULT_RINGTONE_URI";

    public boolean mSMSPref;
    public boolean mCallPref;
    public boolean mVirationSMSPref;
    public boolean mVirationCallPref;
    public String mRingtoneSMS;
    public String mRingtoneCall;

    public int mId_image;

    private static SharedPreferences pref;

    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    public void init(Context context) {
        mcontext = context;
        mDialNumber = new MCStringPreference(context, KEY_DIAL_NUMBER);
        setPref(mcontext);
        loadConfig();
    }

    public String getDialNumber() {
        String number = mDialNumber.getValue("888");
        if (TextUtils.isEmpty(number)) {
            number = "888";
        }
        return number;
    }

    private void loadConfig() {
        mVirationSMSPref = pref.getBoolean(VIBRATION_SMS_PREF, false);
        mVirationCallPref = pref.getBoolean(VIBRATION_CALL_PREF, false);
        mSMSPref = pref.getBoolean(_SMS_PREF, false);
        mCallPref = pref.getBoolean(_CALL_PREF, false);
        mRingtoneSMS = pref.getString(RINGTONES_SMS_PREF, mdefault_string);
        mRingtoneCall = pref.getString(RINGTONES_CALL_PREF, mdefault_string);
        mId_image = pref.getInt(ID_IMAGE, 0);
    }

    public void saveConfig() {
        Editor e = setEditor(pref);
        e.putBoolean(VIBRATION_SMS_PREF, mVirationSMSPref);
        e.putBoolean(VIBRATION_CALL_PREF, mVirationCallPref);
        e.putInt(ID_IMAGE, mId_image);
        e.commit();
    }

    private Editor setEditor(SharedPreferences pref) {
        Editor e = pref.edit();
        return e;
    }

    private void setPref(Context context) {
        this.pref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    static {
        AUDIO_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/LogMe/";
    }

    public static int getProximityTry(Context c) {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(c);
        return p.getInt(PROXIMITY_TRY, 0);
    }

    public static void setProximityTry(Context c, int count) {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(c);
        Editor e = p.edit();
        e.putInt(PROXIMITY_TRY, count);
        e.commit();
    }

    public static boolean isShowProximityGuide(Context c) {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(c);
        return p.getBoolean(SHOW_PROXIMITY_GUIDE, true);
    }

    public static void setShowProximityGuide(Context c, boolean show) {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(c);
        Editor e = p.edit();
        e.putBoolean(SHOW_PROXIMITY_GUIDE, show);
        e.commit();
    }

}
