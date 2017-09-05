
package org.baole.fakelog;

import com.anttek.common.activity.HtmlViewerActivity;
import com.anttek.common.dialog.AdDialog;
import com.anttek.common.dialog.Eula;
import com.anttek.common.dialog.TogglableMessageDialog;
import com.anttek.common.dialog.TogglableMessageDialog.OnToggleListener;

import org.baole.ad.AdUtil;
import org.baole.fakelog.helper.DBAdapter;
import org.baole.fakelog.helper.LogHelper;
import org.baole.fakelog.helper.NotificationHelper;
import org.baole.fakelog.model.Configuration;
import org.baole.fakelog.model.FileItems;
import org.baole.fakelog.service.ProximityListener;
import org.baole.fakelog.service.ScheduleReceiver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class FakeLogActivity extends Activity implements OnClickListener {

    public static final String COMMAND = "c";
    public static final int COMMAND_CALL = 1;
    public static final int COMMAND_SMS = 2;
    public static final String NAME = "na";
    public static final String NUMBER = "nu";
    public static final String DATE = "da";
    public static final String DURATION = "du";
    public static final String TYPE = "ty";
    public static final String MESSAGE = "me";
    public static final String READ = "ra";

    private static final int CONTACT_PICKER_ACTIVITY = 0;
    private static final int LIST_CONTENT_ACTIVITY = 1;
    private static final int RECORD_ACTIVITY = 2;
    public static final String PRO_PKG = "org.baole.fakelogpro";

    public static int ID_SMS = 0;
    public static int ID_CALL = 0;

    protected int DURATION_RESULT = 26;
    protected int CALLTYPE_RESULT = 1;
    protected int TYPE_RESULT = 1;
    protected int READ_RESULT = 1;

    protected int mHourOfDay;
    protected int mMinute;
    protected int mYear;
    protected int mMonthOfYear;
    protected int mDayOfMonth;
    protected int mSeconds;
    protected int mIncrease = 0;

    private EditText eTime;
    private EditText eDate;
    private Spinner mSpinnerRecord;
    private File mDirectory;
    private ArrayList<String> mPathRecord;
    private String mRecord;

    private EditText edit_time;
    private EditText edit_date;
    private ViewSwitcher mSwitcher;
    String AD_ID = "a14e141a4260966";
    private Button mCallLogButton;
    private Button mSMSButton;
    private LinearLayout mCustomPanel;

    // protected Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        TogglableMessageDialog.showDialog(this, R.string.hide_icon_disable,
                R.string.hide_icon_ads, getPackageName() + "hide_icon", new OnToggleListener() {
                    @Override
                    public void onChange(boolean enable) {
                        onSettings();
                    }
                });

        Eula.show(this);
        HtmlViewerActivity.showAsChangeLogActivity(this, R.raw.change_logs, 25);

        AdUtil.setup(this, (LinearLayout) findViewById(R.id.ad_container), true);

        eTime = (EditText) findViewById(R.id.edit_time);
        eDate = (EditText) findViewById(R.id.edit_date);

        mSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher1);

        Date date = new Date(System.currentTimeMillis());
        mMinute = date.getMinutes();
        mHourOfDay = date.getHours();

        Calendar nowCal = Calendar.getInstance();
        mYear = nowCal.get(Calendar.YEAR);
        mMonthOfYear = nowCal.get(Calendar.MONTH);
        mDayOfMonth = nowCal.get(Calendar.DAY_OF_MONTH);

        mCallLogButton = (Button) findViewById(R.id.action_calllog);
        mSMSButton = (Button) findViewById(R.id.action_sms);
        mCustomPanel = (LinearLayout) findViewById(R.id.linearLayout9);

        findViewById(R.id.edit_time).setOnClickListener(this);

        edit_date = (EditText) this.findViewById(R.id.edit_date);
        edit_time = (EditText) this.findViewById(R.id.edit_time);

        if (AdUtil.hasAd()) {
            findViewById(R.id.action_go_pro).setOnClickListener(this);
        } else {
            findViewById(R.id.action_go_pro).setVisibility(View.GONE);
        }

        double d = Math.random();

        if (d > 0.3) {
            AdDialog.showPromotingAppGuardDialog(this, AdUtil.hasAd());
        } else if (d > 0.7) {
            AdDialog.showPromotingSecretBoxDialog(this, AdUtil.hasAd());
        }

        loadConfig();

        edit_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerDialog datePicker = new DatePickerDialog(
                        FakeLogActivity.this, dateSetListener, mYear,
                        mMonthOfYear, mDayOfMonth);
                datePicker.show();
            }
        });
        edit_time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TimePickerDialog timePicker = new TimePickerDialog(
                        FakeLogActivity.this, timeSetListener, mHourOfDay,
                        mMinute, true);
                timePicker.show();
            }
        });
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.action_go_pro) {
            Intent pro = new Intent(Intent.ACTION_VIEW, Uri.parse(String
                    .format("market://details?id=%s", PRO_PKG)));
            startActivity(pro);
        } else if (v.getId() == R.id.action_calllog) {
            activateCallLog(true);
        } else if (v.getId() == R.id.action_sms) {
            activateCallLog(false);
        } else if (v.getId() == R.id.action_pick_number) {
            startActivityForResult(
                    new Intent(this, ContactPickerActivity.class),
                    CONTACT_PICKER_ACTIVITY);
        } else if (v.getId() == R.id.action_call_schedule) {
            onSaveCallLogSchedule();
        } else if (v.getId() == R.id.action_make_call_shortcut) {
            if (AdUtil.hasAd()) {
                NotificationHelper.createGoProDialog(this, PRO_PKG,
                        R.string.create_shortcut_msg).show();
            } else {
                onMakeCallShortcut();
            }
        } else if (v.getId() == R.id.action_make_sms_shortcut) {
            if (AdUtil.hasAd()) {
                NotificationHelper.createGoProDialog(this, PRO_PKG,
                        R.string.create_shortcut_msg).show();
            } else {
                onMakeSMSShortcut();
            }
        } else if (v.getId() == R.id.action_sms_schedule) {
            onSaveSMSSchedule();
        } else if (v.getId() == R.id.schedule_5s || v.getId() == R.id.schedule_60s
                || v.getId() == R.id.schedule_5min || v.getId() == R.id.schedule_30min) {
            mCustomPanel.setVisibility(View.GONE);
            schedule(v.getId());
        } else if (v.getId() == R.id.schedule_proximity) {
            int proxTryCount = Configuration.getProximityTry(this);
            if (mCurrentSelectedId != v.getId()) {
                Configuration.setProximityTry(this, proxTryCount + 1);
            }
            if (proxTryCount < 4 || !AdUtil.hasAd()) {
                mCustomPanel.setVisibility(View.GONE);
                schedule(v.getId());
                if (Configuration.isShowProximityGuide(getApplicationContext())) {
                    showGuideDialog();
                }
            } else {
                NotificationHelper.createGoProDialog(this, PRO_PKG,
                        R.string.proximity_sensor_close_trial).show();
            }
            Log.e("hic hic",
                    String.format("try count: %s %s %s", proxTryCount, mCurrentSelectedId,
                            v.getId()));
        } else if (v.getId() == R.id.buttonCustom) {
            mCustomPanel.setVisibility(View.VISIBLE);
            schedule(v.getId());
        }

    }

    public void showGuideDialog() {
        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(R.string.proximity_sensor_title)
                .setMessage(R.string.proximity_sensor_guide)
                .setPositiveButton(R.string.proximity_sensor_remember,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                Configuration.setShowProximityGuide(
                                        getApplicationContext(), false);
                                dialog.cancel();
                            }

                        })
                .setNegativeButton(R.string.proximity_sensor_close,
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog,
                                    final int id) {
                                dialog.cancel();
                            }
                        });

        builder.create().show();
    }

    private void onMakeSMSShortcut() {

        EditText eNumber = (EditText) findViewById(R.id.edit_number);
        EditText eMsg = (EditText) findViewById(R.id.edit_message);
        CheckBox eCheck = (CheckBox) findViewById(R.id.check_sms_notif);
        EditText eName = (EditText) findViewById(R.id.text_name);

        String name = eName.getText().toString();
        String number = eNumber.getText().toString();
        String message = eMsg.getText().toString();

        if (TextUtils.isEmpty(number) || TextUtils.isEmpty(message)) {
            Toast.makeText(this, R.string.invalid_number_msg,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int read = getReadStatus();
        int type = getMsgType();
        boolean notif = eCheck.isChecked();
        long date = new GregorianCalendar(mYear, mMonthOfYear, mDayOfMonth,
                mHourOfDay, mMinute).getTimeInMillis();
        boolean isCustom = (mCurrentSelectedId == R.id.buttonCustom);
        String datetime = eTime.getText().toString() + " "
                + eDate.getText().toString();

        Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
        shortcutIntent.setClass(this, ShortcutActivity.class);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        shortcutIntent.putExtra(ShortcutActivity.KEY_IS_CALL, false);
        shortcutIntent.putExtra(ShortcutActivity.KEY_INCREASE, mIncrease);
        shortcutIntent.putExtra(ShortcutActivity.KEY_DATE, date);
        shortcutIntent.putExtra(ShortcutActivity.KEY_NAME, name);
        shortcutIntent.putExtra(ShortcutActivity.KEY_NUMBER, number);
        shortcutIntent.putExtra(ShortcutActivity.KEY_MESAAGE, message);
        shortcutIntent.putExtra(ShortcutActivity.KEY_READ, read);
        shortcutIntent.putExtra(ShortcutActivity.KEY_TYPE, type);
        shortcutIntent.putExtra(ShortcutActivity.KEY_NOTIF, notif);
        shortcutIntent.putExtra(ShortcutActivity.KEY_CUSTOM, isCustom);
        shortcutIntent.putExtra(ShortcutActivity.KEY_DATETIME, datetime);

        String label = name;
        if (TextUtils.isEmpty(label)) {
            label = number;
        }

        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, label);
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.icon);

        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        sendBroadcast(intent);
        Toast.makeText(this, R.string.shortcut_msg, Toast.LENGTH_SHORT).show();
    }

    private void onMakeCallShortcut() {
        EditText eName = (EditText) findViewById(R.id.text_name);
        EditText eNumber = (EditText) findViewById(R.id.edit_number);
        EditText eDuration = (EditText) findViewById(R.id.edit_duration);
        CheckBox eCheck = (CheckBox) findViewById(R.id.check_call_notif);

        String name = eName.getText().toString();
        String number = eNumber.getText().toString();
        String sduration = eDuration.getText().toString();

        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, R.string.invalid_number, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        int duration = Integer.parseInt(sduration);
        long date = new GregorianCalendar(mYear, mMonthOfYear, mDayOfMonth,
                mHourOfDay, mMinute).getTimeInMillis();
        boolean notif = eCheck.isChecked();
        int callType = getCallType();
        boolean isCustom = (mCurrentSelectedId == R.id.buttonCustom);
        String datetime = eTime.getText().toString() + " "
                + eDate.getText().toString();

        Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
        shortcutIntent.setClass(this, ShortcutActivity.class);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        shortcutIntent.putExtra(ShortcutActivity.KEY_IS_CALL, true);
        shortcutIntent.putExtra(ShortcutActivity.KEY_INCREASE, mIncrease);
        shortcutIntent.putExtra(ShortcutActivity.KEY_DURATION, duration);
        shortcutIntent.putExtra(ShortcutActivity.KEY_DATE, date);
        shortcutIntent.putExtra(ShortcutActivity.KEY_NAME, name);
        shortcutIntent.putExtra(ShortcutActivity.KEY_NUMBER, number);
        shortcutIntent.putExtra(ShortcutActivity.KEY_CALLTYPE, callType);
        shortcutIntent.putExtra(ShortcutActivity.KEY_NOTIF, notif);
        shortcutIntent.putExtra(ShortcutActivity.KEY_CUSTOM, isCustom);
        shortcutIntent.putExtra(ShortcutActivity.KEY_DATETIME, datetime);
        shortcutIntent.putExtra(ShortcutActivity.KEY_RECORD, mRecord);

        String label = name;
        if (TextUtils.isEmpty(label)) {
            label = number;
        }

        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, label);
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.icon);

        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        sendBroadcast(intent);
        Toast.makeText(this, R.string.shortcut_msg, Toast.LENGTH_SHORT).show();
    }

    private void activateCallLog(boolean b) {
        mSwitcher.setDisplayedChild(b ? 0 : 1);
        mCallLogButton.setSelected(b);
        mSMSButton.setSelected(!b);
        mCallLogButton.setTextColor(getResources().getColor(
                b ? R.color.selected_color : R.color.unselected_color));
        mSMSButton.setTextColor(getResources().getColor(
                b ? R.color.unselected_color : R.color.selected_color));
    }

    int mCurrentSelectedId = -1;

    private void schedule(int id) {

        if (id == R.id.schedule_5s) {
            mIncrease = 5;
        } else if (id == R.id.schedule_60s) {
            mIncrease = 60;
        } else if (id == R.id.schedule_5min) {
            mIncrease = 300;
        } else if (id == R.id.schedule_30min) {
            mIncrease = 1800;
        } else if (id == R.id.schedule_proximity) {
            mIncrease = -1;
        } else if (id == R.id.buttonCustom) {
            mIncrease = 0;
        }

        try {
            ((Button) findViewById(id)).setTextColor(getResources().getColor(
                    R.color.selected_color));

            if (mCurrentSelectedId != -1 && mCurrentSelectedId != id) {
                ((Button) findViewById(mCurrentSelectedId))
                        .setTextColor(getResources().getColor(
                                R.color.unselected_color));
            }
            mCurrentSelectedId = id;

        } catch (Throwable e) {
            mCurrentSelectedId = id;
            e.printStackTrace();
        }
    }

    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHourOfDay = hourOfDay;
            mMinute = minute;
            setTime();
        }
    };

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                int dayOfMonth) {
            mYear = year;
            mMonthOfYear = monthOfYear;
            mDayOfMonth = dayOfMonth;
            setDateInput();
        }
    };
    private ArrayAdapter<String> adapter;

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    protected void setTime() {
        EditText timeInput = (EditText) findViewById(R.id.edit_time);
        timeInput.setText(pad(mHourOfDay) + ":" + pad(mMinute));
    }

    protected void setDateInput() {
        final EditText dateInput = (EditText) findViewById(R.id.edit_date);
        dateInput.setText(pad(mDayOfMonth) + "/" + pad(mMonthOfYear + 1) + "/"
                + mYear);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CONTACT_PICKER_ACTIVITY && resultCode == RESULT_OK) {
            EditText eName = (EditText) findViewById(R.id.text_name);
            EditText eNumber = (EditText) findViewById(R.id.edit_number);
            String name = data.getStringExtra(ContactPickerActivity.NAME);
            String number = data.getStringExtra(ContactPickerActivity.NUMBER);

            if (!TextUtils.isEmpty(name)) {
                eName.setText(name);
            } else {
                eName.setText("");
            }
            if (!TextUtils.isEmpty(number)) {
                eNumber.setText(number);
            } else {
                eNumber.setText("");
            }
        }

        else if (requestCode == RECORD_ACTIVITY && resultCode == RESULT_OK) {
            loadRecordItems();
            adapter.notifyDataSetChanged();
        } else if (requestCode == LIST_CONTENT_ACTIVITY
                && resultCode == RESULT_OK) {

            EditText eName = (EditText) findViewById(R.id.text_name);
            EditText eNumber = (EditText) findViewById(R.id.edit_number);
            EditText eMessage = (EditText) findViewById(R.id.edit_message);
            EditText eDuration = (EditText) findViewById(R.id.edit_duration);

            RadioButton eIncomingCallRadio = (RadioButton) findViewById(R.id.incomingCallRadio);
            RadioButton eOutgoingCallRadio = (RadioButton) findViewById(R.id.outgoingCallRadio);
            RadioButton eMissedCallRadio = (RadioButton) findViewById(R.id.missedCallRadio);

            RadioButton eReceived = (RadioButton) findViewById(R.id.receivedSmsRadio);
            RadioButton eSent = (RadioButton) findViewById(R.id.sentSmsRadio);
            RadioButton eUnread = (RadioButton) findViewById(R.id.unreadRadio);
            RadioButton eRead = (RadioButton) findViewById(R.id.readRadio);

            String name = data.getStringExtra(ContactPickerActivity.NAME);
            String number = data.getStringExtra(ContactPickerActivity.NUMBER);
            int ContactPicker = data.getIntExtra("ContactPicker", 0);
            int ListContent = data.getIntExtra("ListContent", 0);
            if (ContactPicker == 0 || ListContent == 1) {

                int duration = data.getIntExtra(ContactPickerActivity.DURATION,
                        DURATION_RESULT);
                int calltype = data.getIntExtra(ContactPickerActivity.CALLTYPE,
                        CALLTYPE_RESULT);
                String message = data
                        .getStringExtra(ContactPickerActivity.MESSAGE);
                int type = data.getIntExtra(ContactPickerActivity.TYPE,
                        TYPE_RESULT);
                int read = data.getIntExtra(ContactPickerActivity.READ,
                        READ_RESULT);
                String ID = data.getStringExtra(ContactPickerActivity.ID);
                String dateschedule = data
                        .getStringExtra(ContactPickerActivity.DATESCHEDULE);

                if (!TextUtils.isEmpty(dateschedule)) {
                    // SpiltDateTime(dateschedule);
                    // DatePickerDialog datePicker = new DatePickerDialog(this,
                    // dateSetListener, mYear, mMonthOfYear, mDayOfMonth);
                    // datePicker.show();
                    //
                    // TimePickerDialog timePicker = new TimePickerDialog(this,
                    // timeSetListener, mHourOfDay, mMinute, true);
                    // timePicker.show();
                }

                if (type == 1)
                    eReceived.setChecked(true);
                else if (type == 2)
                    eSent.setChecked(true);

                if (read == 0)
                    eUnread.setChecked(true);
                else if (read == 1)
                    eRead.setChecked(true);

                if (calltype == 1)
                    eIncomingCallRadio.setChecked(true);
                else if (calltype == 2)
                    eOutgoingCallRadio.setChecked(true);
                else if (calltype == 3)
                    eMissedCallRadio.setChecked(true);

                if (!TextUtils.isEmpty(message)) {
                    ID_SMS = Integer.parseInt(ID);
                    eMessage.setText(message);
                } else {
                    ID_CALL = Integer.parseInt(ID);
                }
                if (!TextUtils.isEmpty(String.valueOf(duration))) {
                    eDuration.setText(String.valueOf(duration));
                }
            }

            if (!TextUtils.isEmpty(name)) {
                eName.setText(name);
            }
            if (!TextUtils.isEmpty(number)) {
                eNumber.setText(number);
            }
        }
    }

    private void onSaveCallLogSchedule() {

        EditText eName = (EditText) findViewById(R.id.text_name);
        EditText eNumber = (EditText) findViewById(R.id.edit_number);
        EditText eDuration = (EditText) findViewById(R.id.edit_duration);
        CheckBox eCheck = (CheckBox) findViewById(R.id.check_call_notif);

        String name = eName.getText().toString();
        String number = eNumber.getText().toString();
        String sduration = eDuration.getText().toString();

        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, R.string.invalid_number, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        int duration = 0;
        try {
            duration = Integer.parseInt(sduration);
        } catch (Throwable e) {
            Toast.makeText(this, R.string.invalid_duration, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        long date = new GregorianCalendar(mYear, mMonthOfYear, mDayOfMonth,
                mHourOfDay, mMinute).getTimeInMillis();
        boolean notif = eCheck.isChecked();
        int callType = getCallType();
        boolean isCustom = (mCurrentSelectedId == R.id.buttonCustom);
        String datetime = eTime.getText().toString() + " "
                + eDate.getText().toString();

        onProcessCallItem(this, mIncrease, duration, date, name, number,
                callType, notif, isCustom, datetime, mRecord);

    }

    public static void onProcessCallItem(Context c, int increase, int duration,
            long date, String name, String number, int callType, boolean notif,
            boolean isCustom, String datetime, String record) {
        int nof = 0;
        String voice = null;

        Calendar oldCal = Calendar.getInstance();
        Calendar nowCal = Calendar.getInstance();
        if (isCustom) {
            oldCal.setTimeInMillis(date);
        } else {
            oldCal.add(Calendar.SECOND, increase);
        }

        if (increase == -1) {// proximity sensor
            Intent intent = new Intent(c, ScheduleReceiver.class);
            intent.putExtra(ScheduleReceiver.NAME, name);
            intent.putExtra(ScheduleReceiver.NUMBER, number);
            intent.putExtra(ScheduleReceiver.CALL_TYPE, callType);
            intent.putExtra(ScheduleReceiver.DURATION, duration);
            intent.putExtra(ScheduleReceiver.NOTIF, nof);
            intent.putExtra(ScheduleReceiver.VOICE, voice);
            intent.putExtra(ScheduleReceiver.DATE, System.currentTimeMillis());

            ProximityListener.create(c);
            ProximityListener.register(c, intent);
            return;
        }

        if (isCustom && oldCal.before(nowCal)) {
            Uri uri = LogHelper.insertCallEntry(c, name, number, date,
                    duration, callType, false);

            if (notif && callType == CallLog.Calls.MISSED_TYPE) {
                NotificationHelper.addMissedCallNotification(c, name, number,
                        date, uri);
            }
            Toast.makeText(c, "New call log is created", Toast.LENGTH_SHORT)
                    .show();
        } else {

            if (notif && callType == CallLog.Calls.MISSED_TYPE)
                nof = 1;
            if (callType == 1)
                voice = record;

            // ===============Set Alarm===========================

            Intent intent = new Intent(c, ScheduleReceiver.class);
            AlarmManager alarm = (AlarmManager) c
                    .getSystemService(ALARM_SERVICE);
            intent.putExtra(ScheduleReceiver.NAME, name);
            intent.putExtra(ScheduleReceiver.NUMBER, number);
            intent.putExtra(ScheduleReceiver.CALL_TYPE, callType);
            intent.putExtra(ScheduleReceiver.DURATION, duration);
            intent.putExtra(ScheduleReceiver.DATE, date);
            intent.putExtra(ScheduleReceiver.NOTIF, nof);
            intent.putExtra(ScheduleReceiver.VOICE, voice);

            // =========Insert into databse =======================

            FileItems fileitem = new FileItems();
            fileitem.setName(name);
            fileitem.setNumber(number);
            fileitem.setDateSchedule(datetime.toString());
            fileitem.setDuration(duration);
            fileitem.setType(callType);
            fileitem.setNof(nof);
            fileitem.setVoice(voice);
            if (ID_CALL > 0) {
                intent.setAction("org.baole.fakelog.schedule_call" + ID_CALL);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(c,
                        ID_CALL, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                pendingIntent.cancel();
                DBAdapter adapter = new DBAdapter(c);

                try {
                    adapter.openWrite();
                    boolean result = adapter.RemoveCall(ID_CALL);
                    if (result == true) {
                        adapter.close();
                        Toast.makeText(c, "call is updated", Toast.LENGTH_LONG)
                                .show();
                    }
                } catch (SQLException e) {
                }
            }
            try {
                DBAdapter adapter = new DBAdapter(c);
                adapter.openWrite();
                boolean result = adapter.InsertOrUpdateCall(fileitem);
                if (result == true) {
                    adapter.close();
                    adapter.openRead();
                    int call_id = adapter.QueryCALL_ID();
                    adapter.close();
                    intent.setAction("org.baole.fakelog.schedule_call"
                            + call_id);

                    intent.putExtra("call_id", call_id);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(c,
                            call_id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    if (isCustom) {
                        alarm.set(AlarmManager.RTC_WAKEUP, date, pendingIntent);
                    } else {
                        alarm.set(AlarmManager.RTC_WAKEUP,
                                oldCal.getTimeInMillis(), pendingIntent);
                    }
                    Toast.makeText(c, "New call is scheduled",
                            Toast.LENGTH_LONG).show();
                }

            } catch (SQLException e) {
            }

            ID_CALL = 0;
        }

    }

    private void onSaveSMSSchedule() {

        EditText eNumber = (EditText) findViewById(R.id.edit_number);
        EditText eMsg = (EditText) findViewById(R.id.edit_message);
        CheckBox eCheck = (CheckBox) findViewById(R.id.check_sms_notif);
        EditText eName = (EditText) findViewById(R.id.text_name);

        String name = eName.getText().toString();
        String number = eNumber.getText().toString();
        String message = eMsg.getText().toString();

        if (TextUtils.isEmpty(number) || TextUtils.isEmpty(message)) {
            Toast.makeText(this, R.string.invalid_number_msg,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int read = getReadStatus();
        int type = getMsgType();
        boolean notif = eCheck.isChecked();
        long date = new GregorianCalendar(mYear, mMonthOfYear, mDayOfMonth,
                mHourOfDay, mMinute).getTimeInMillis();
        boolean isCustom = (mCurrentSelectedId == R.id.buttonCustom);
        String datetime = eTime.getText().toString() + " "
                + eDate.getText().toString();

        onProcessSMSItem(this, mIncrease, date, name, number, message, read,
                type, notif, isCustom, datetime);
    }

    public static void onProcessSMSItem(Context c, int increase, long date,
            String name, String number, String message, int read, int type,
            boolean notif, boolean isCustom, String datetime) {
        Calendar oldCal = Calendar.getInstance();

        if (isCustom) {
            oldCal.setTimeInMillis(date);
        } else {
            oldCal.add(Calendar.SECOND, increase);
        }

        Calendar nowCal = Calendar.getInstance();
        if (isCustom && oldCal.before(nowCal)) {
            Uri uri = LogHelper.insertSMSEntry(c, number, message, date, read,
                    type, false);

            if (notif && type == MESSAGE_TYPE_INBOX) {
                NotificationHelper.addSMSNotification(c, name, number, message,
                        date, uri);
            }
            Toast.makeText(c, "New sms log is created", Toast.LENGTH_SHORT)
                    .show();
        } else {

            int nof = 0;
            if (notif && type == MESSAGE_TYPE_INBOX) {
                nof = 1;
            }

            // ===============Set Alarm===========================

            Intent intent = new Intent(c, ScheduleReceiver.class);
            intent.putExtra(ScheduleReceiver.NAME, name);
            intent.putExtra(ScheduleReceiver.NUMBER, number);
            intent.putExtra(ScheduleReceiver.MESSAGE, message);
            intent.putExtra(ScheduleReceiver.TYPE, type);
            intent.putExtra(ScheduleReceiver.READ, read);
            intent.putExtra(ScheduleReceiver.NOTIF, nof);

            if (increase == -1) {// proximity sensor
                ProximityListener.create(c);
                ProximityListener.register(c, intent);

                return;
            }

            // =========Insert into databse =======================

            FileItems fileitem = new FileItems();
            fileitem.setName(name);
            fileitem.setNumber(number);
            fileitem.setDateSchedule(datetime.toString());
            fileitem.setMessage(message);
            fileitem.setType(type);
            fileitem.setRead(read);
            fileitem.setNof(nof);

            if (ID_SMS > 0) {
                intent.setAction("org.baole.fakelog.schedule_sms" + ID_SMS);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(c,
                        ID_SMS, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                pendingIntent.cancel();
                DBAdapter adapter = new DBAdapter(c);

                try {
                    adapter.openWrite();
                    boolean result = adapter.RemoveCall(ID_SMS);
                    if (result == true) {
                        adapter.close();
                        Toast.makeText(c, "Schedule Update", Toast.LENGTH_LONG)
                                .show();
                    }

                } catch (SQLException e) {

                }
            }

            try {
                DBAdapter adapter = new DBAdapter(c);
                if (!TextUtils.isEmpty(message)) {

                    adapter.openWrite();
                    boolean result = adapter.InsertOrUpdateSMS(fileitem);
                    if (result == true) {
                        adapter.close();
                        adapter.openRead();
                        int sms_id = adapter.QuerySMS_ID();
                        intent.setAction("org.baole.fakelog.schedule_sms"
                                + sms_id);
                        adapter.close();
                        intent.putExtra("sms_id", sms_id);
                        PendingIntent pendingIntent = PendingIntent
                                .getBroadcast(c, sms_id, intent,
                                        PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager alarm = (AlarmManager) c
                                .getSystemService(ALARM_SERVICE);
                        if (isCustom) {
                            alarm.set(AlarmManager.RTC_WAKEUP, date,
                                    pendingIntent);
                        } else {
                            alarm.set(AlarmManager.RTC_WAKEUP,
                                    oldCal.getTimeInMillis(), pendingIntent);
                        }
                        Toast.makeText(c, "Add new Schedule", Toast.LENGTH_LONG)
                                .show();
                    }

                } else {
                    Toast.makeText(c, "Invalid number or message",
                            Toast.LENGTH_SHORT).show();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            ID_SMS = 0;

        }
    }

    private int getCallType() {
        RadioButton incomingCallRadio = (RadioButton) findViewById(R.id.incomingCallRadio);
        RadioButton outgoingCallRadio = (RadioButton) findViewById(R.id.outgoingCallRadio);
        RadioButton missedCallRadio = (RadioButton) findViewById(R.id.missedCallRadio);

        int CALL_TYPE = 0;
        if (incomingCallRadio.isChecked() == true) {
            CALL_TYPE = CallLog.Calls.INCOMING_TYPE;
        } else if (outgoingCallRadio.isChecked() == true) {
            CALL_TYPE = CallLog.Calls.OUTGOING_TYPE;
        } else if (missedCallRadio.isChecked() == true) {
            CALL_TYPE = CallLog.Calls.MISSED_TYPE;
        }
        return CALL_TYPE;
    }

    private static final int MESSAGE_TYPE_ALL = 0;
    private static final int MESSAGE_TYPE_INBOX = 1;
    private static final int MESSAGE_TYPE_SENT = 2;
    private static final String LAST_BUTTON_ID = "_lbid";

    private int getMsgType() {
        RadioButton receivedSmsRadio = (RadioButton) findViewById(R.id.receivedSmsRadio);
        RadioButton sentSmsRadio = (RadioButton) findViewById(R.id.sentSmsRadio);

        int MSG_TYPE = MESSAGE_TYPE_ALL;
        if (receivedSmsRadio.isChecked() == true) {
            MSG_TYPE = MESSAGE_TYPE_INBOX;
        } else if (sentSmsRadio.isChecked() == true) {
            MSG_TYPE = MESSAGE_TYPE_SENT;
        }
        return MSG_TYPE;
    }

    private int getReadStatus() {
        RadioButton readRadio = (RadioButton) findViewById(R.id.readRadio);
        RadioButton unreadRadio = (RadioButton) findViewById(R.id.unreadRadio);
        int MSG_READ = 0;
        if (readRadio.isChecked() == true) {
            MSG_READ = 1;
        } else if (unreadRadio.isChecked() == true) {
            MSG_READ = 0;
        } else if (getMsgType() == MESSAGE_TYPE_SENT) {
            MSG_READ = 1;
        }
        return MSG_READ;
    }

    void saveConfig() {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        Editor e = pref.edit();

        EditText eName = (EditText) findViewById(R.id.text_name);
        EditText eNumber = (EditText) findViewById(R.id.edit_number);
        EditText eDuration = (EditText) findViewById(R.id.edit_duration);
        EditText eMsg = (EditText) findViewById(R.id.edit_message);
        CheckBox eCheckCallNotif = (CheckBox) findViewById(R.id.check_call_notif);
        CheckBox eCheckSMSNotif = (CheckBox) findViewById(R.id.check_sms_notif);
        Spinner eSpinner = (Spinner) findViewById(R.id.spinnerRecorder);

        String name = eName.getText().toString();
        String number = eNumber.getText().toString();
        String sduration = eDuration.getText().toString();
        String message = eMsg.getText().toString();
        Object spinner = eSpinner.getSelectedItem();
        if (spinner != null) {
            e.putString("spinner", spinner.toString());
        }
        e.putInt("mHourOfDay", mHourOfDay);
        e.putInt("mMinute", mMinute);
        e.putInt("mYear", mYear);
        e.putInt("mMonthOfYear", mMonthOfYear);
        e.putInt("mDayOfMonth", mDayOfMonth);

        e.putInt(LAST_BUTTON_ID, mCurrentSelectedId);

        e.putString("name", name);
        e.putString("number", number);
        e.putString("sduration", sduration);
        e.putString("message", message);

        e.putBoolean("mCallNotif", eCheckCallNotif.isChecked());
        e.putBoolean("mSMSNotif", eCheckSMSNotif.isChecked());

        e.commit();
    }

    void loadConfig() {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);

        EditText eName = (EditText) findViewById(R.id.text_name);
        EditText eNumber = (EditText) findViewById(R.id.edit_number);
        EditText eDuration = (EditText) findViewById(R.id.edit_duration);
        EditText eMsg = (EditText) findViewById(R.id.edit_message);
        CheckBox eCheckCallNotif = (CheckBox) findViewById(R.id.check_call_notif);
        CheckBox eCheckSMSNotif = (CheckBox) findViewById(R.id.check_sms_notif);

        String name = pref.getString("name", "");
        String number = pref.getString("number", "");
        String message = pref.getString("message", "");
        String duration = pref.getString("sduration", "60");
        boolean callnotif = pref.getBoolean("mCallNotif", false);
        boolean smsnotif = pref.getBoolean("mSMSNotif", false);

        schedule(pref.getInt(LAST_BUTTON_ID, R.id.schedule_5s));
        activateCallLog(true);

        eName.setText(name);
        eNumber.setText(number);
        eMsg.setText(message);
        eDuration.setText(duration);
        eCheckCallNotif.setChecked(callnotif);
        eCheckSMSNotif.setChecked(smsnotif);
        setTime();
        setDateInput();

        int pos = loadRecordItems();
        mSpinnerRecord = (Spinner) findViewById(R.id.spinnerRecorder);

        mSpinnerRecord.setVisibility(AdUtil.hasAd() ? View.GONE
                : View.VISIBLE);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, mPathRecord);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerRecord.setAdapter(adapter);
        if (pos > 0) {
            mSpinnerRecord.setSelection(pos);
        }
        mSpinnerRecord
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                            View view, int position, long id) {
                        mRecord = mPathRecord.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }
                });
    }

    private int loadRecordItems() {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        String spinner = pref.getString("spinner", "");
        int position = -1;

        try {

            mDirectory = new File(Configuration.AUDIO_DIR);
            mDirectory.mkdirs();

            final File[] file = mDirectory.listFiles();

            if (mPathRecord == null) {
                mPathRecord = new ArrayList<String>();
            } else {
                mPathRecord.clear();
            }
            for (int i = 0; i < file.length; i++) {
                mPathRecord.add(file[i].getName());
                if (spinner.equals(file[i].getName())) {
                    position = i;
                }
            }
        } catch (Throwable e) {
            Toast.makeText(getApplicationContext(), R.string.load_audio_failed,
                    Toast.LENGTH_LONG).show();
        }
        return position;
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveConfig();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_main_menu, menu);
        menu.findItem(R.id.action_other_apps).setVisible(AdUtil.hasAd());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            onSettings();
            return true;
        } else if (item.getItemId() == R.id.action_voice) {
            startActivityForResult(new Intent(FakeLogActivity.this,
                    VoiceManagerActivity.class), RECORD_ACTIVITY);
            return true;
        } else if (item.getItemId() == R.id.action_schedule) {
            startActivityForResult(
                    new Intent(this, ScheduledItemActivity.class),
                    LIST_CONTENT_ACTIVITY);
            return true;
        } else if (item.getItemId() == R.id.action_proximity) {
            showGuideDialog();
            return true;
        } else if (item.getItemId() == R.id.action_about) {
            AdDialog.startMarketPubIntent(this, "Droid Mate");
            return true;
        } else if (item.getItemId() == R.id.action_changelogs) {
            HtmlViewerActivity.showActivity(this, R.raw.change_logs);
            return true;
        } else if (item.getItemId() == R.id.action_other_apps) {
            AdDialog.startMarketAppIntent(this, "com.anttek.appguard");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSettings() {
        startActivity(new Intent(this, PreferencesActivity.class));
    }

}
