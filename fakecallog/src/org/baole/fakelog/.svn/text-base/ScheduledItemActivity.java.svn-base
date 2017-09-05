package org.baole.fakelog;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import org.baole.fakelog.helper.DBAdapter;
import org.baole.fakelog.model.FileItems;
import org.baole.fakelog.service.ScheduleReceiver;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

public class ScheduledItemActivity extends Activity implements
		OnItemClickListener,OnItemLongClickListener {
	private DBAdapter adapter;
	private MyArrayAdapter arrayAdapterCall;
	private MyArrayAdapter arrayAdapterSMS;
	private TabHost myTabHost;
	private FileItems fileitem;

	private ListView mCalllogListView;
	private ListView mSMSListView;
	public static final String SMS_LOG = "SMS";
	public static final String CALL_LOG = "CallLogs";
	private boolean mCheckCleearSchedule;

	protected Calendar scheduleCal;
	protected Calendar nowCal;
	protected int mHourOfDay;
	protected int mMinute;
	protected int mYear;
	protected int mMonthOfYear;
	protected int mDayOfMonth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_picker_activity);
		nowCal = Calendar.getInstance();
		scheduleCal = Calendar.getInstance();
		mCalllogListView = new ListView(this);
		mSMSListView = new ListView(this);		
		SharedPreferences prefs = PreferenceManager
		.getDefaultSharedPreferences(this);	
		mCheckCleearSchedule = prefs.getBoolean("ClearSchedulePref", false);
		mCalllogListView
				.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						MenuInflater Inflater = getMenuInflater();
						Inflater.inflate(R.menu.context_menu, menu);
					}
				});

		mSMSListView
				.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						MenuInflater Inflater = getMenuInflater();
						Inflater.inflate(R.menu.context_menu, menu);
					}
				});

		myTabHost = (TabHost) findViewById(android.R.id.tabhost);
		myTabHost.setup();
		myTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);

		TabSpec ts = null;

		ts = myTabHost.newTabSpec(SMS_LOG);
		ts.setIndicator(createTabView(this, SMS_LOG));
		ts.setContent(new TabHost.TabContentFactory() {
			public View createTabContent(String tag) {
				loadSMSLogs();
				return mSMSListView;
			}
		});
		myTabHost.addTab(ts);

		ts = myTabHost.newTabSpec(CALL_LOG);
		ts.setIndicator(createTabView(this, CALL_LOG));
		ts.setContent(new TabHost.TabContentFactory() {
			public View createTabContent(String tag) {
				loadCallLogs();
				return mCalllogListView;
			}
		});
		myTabHost.addTab(ts);

	}

	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context)
				.inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}

	// ====================================================================================

	protected void loadSMSLogs() {

		adapter = new DBAdapter(this);
		int EntryType_Call = 0;
		try {
			adapter.openRead();
			adapter.openWrite();
			ArrayList<FileItems> arraylist_SMS = adapter.querySMS();
			if (mCheckCleearSchedule == true) {
				for (int i = arraylist_SMS.size()-1 ; i >=0 ; i--) {
					spiltDateTime(arraylist_SMS.get(i).getDateSchedule());
					scheduleCal.set(mYear, mMonthOfYear, mDayOfMonth,
							mHourOfDay, mMinute);
					if (scheduleCal.before(nowCal) == true) {
						 adapter.RemoveSMS(arraylist_SMS.get(i).getSms_id());
						 arraylist_SMS.remove(i);	
						 
					}
					
				}
			}
			arrayAdapterSMS = new MyArrayAdapter(this, arraylist_SMS,
					EntryType_Call);
			mSMSListView.setAdapter(arrayAdapterSMS);
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		mSMSListView.setOnItemClickListener(this);
		mSMSListView.setOnItemLongClickListener(this);
	}

	protected void loadCallLogs() {

		adapter = new DBAdapter(this);
		int EntryType_Call = 1;
		try {
			adapter.openRead();
			ArrayList<FileItems> arraylist_Call = adapter.queryCall();
			if (mCheckCleearSchedule == true) {
				for (int i = arraylist_Call.size()-1 ; i >=0 ; i--) {

					spiltDateTime(arraylist_Call.get(i).getDateSchedule());
					scheduleCal.set(mYear, mMonthOfYear, mDayOfMonth,
							mHourOfDay, mMinute);

					if (scheduleCal.before(nowCal) == true) {
						 adapter.RemoveCall(arraylist_Call.get(i).getCall_id());
						 arraylist_Call.remove(i);
					}
				}
			}
			arrayAdapterCall = new MyArrayAdapter(this, arraylist_Call,
					EntryType_Call);
			mCalllogListView.setAdapter(arrayAdapterCall);

		} catch (SQLException e) {
			e.printStackTrace();
		} 
		mCalllogListView.setOnItemClickListener(this);
		mCalllogListView.setOnItemLongClickListener(this);
	}

	// ====================================================================================

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater Inflater = getMenuInflater();
		Inflater.inflate(R.menu.context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == (R.id.delete)) {
            remove(fileitem);
        } else if (item.getItemId() == (R.id.edit)) {
            editAndFinish(fileitem);
        } else {
        }
		return true;
	}

	// ====================================================================================

	private void remove(FileItems fileitem) {

		try {

			if (adapter != null && fileitem != null && !TextUtils.isEmpty(fileitem.getMessage())) {
				adapter.openWrite();
				adapter.RemoveSMS(fileitem.getSms_id());
				arrayAdapterSMS.NotifyDataSetChanged(fileitem);

				Intent intent = new Intent(this, ScheduleReceiver.class);
				intent.setAction("org.baole.fakelog.schedule_sms"
						+ fileitem.getSms_id());
				PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
						fileitem.getSms_id(), intent,
						PendingIntent.FLAG_CANCEL_CURRENT);
				pendingIntent.cancel();

			} else if ((fileitem.getDuration()) != 0) {
				adapter.RemoveCall(fileitem.getCall_id());
				arrayAdapterCall.NotifyDataSetChanged(fileitem);

				Intent intent = new Intent(this, ScheduleReceiver.class);
				intent.setAction("org.baole.fakelog.schedule_call"
						+ fileitem.getCall_id());
				PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
						fileitem.getCall_id(), intent,
						PendingIntent.FLAG_CANCEL_CURRENT);
				pendingIntent.cancel();

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}

	protected void editAndFinish(FileItems fileitem) {
		
		if (fileitem == null) {
			return;
		}
		
		Intent data = new Intent();

		if (fileitem.getMessage() == null) {

			String id = String.valueOf(fileitem.getCall_id());
			data.putExtra("id", id);
			data.putExtra("number", fileitem.getNumber());
			data.putExtra("dateschedule", fileitem.getDateSchedule());
			data.putExtra("duration", fileitem.getDuration());
			data.putExtra("calltype", fileitem.getType());
			data.putExtra("name", fileitem.getName());
			data.putExtra("voice", fileitem.getVoice());
		
		} else {

			String id = String.valueOf(fileitem.getSms_id());
			data.putExtra("id", id);
			data.putExtra("number", fileitem.getNumber());
			data.putExtra("message", fileitem.getMessage());
			data.putExtra("dateschedule", fileitem.getDateSchedule());
			data.putExtra("read", fileitem.getRead());
			data.putExtra("type", fileitem.getType());
			data.putExtra("name", fileitem.getName());
			
		}
		setResult(RESULT_OK, data);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Menu that appears when menu button is pressed on device
		Menu m_menu = menu;
		m_menu.add(Menu.NONE, Menu.FIRST + 3, 0, "Settings SMS");
		m_menu.add(Menu.NONE, Menu.FIRST + 4, 0, "Settings Call");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String msg = "Selected from menu: ";
		switch (item.getItemId()) {
		case Menu.FIRST + 3:
			//
			return true;
		case Menu.FIRST + 4:
			Toast.makeText(this, msg + "Settings Call", Toast.LENGTH_LONG)
					.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view,
			int position, long id) {
		if (mCalllogListView.equals(parent)) {
			fileitem = (FileItems) parent.getItemAtPosition(position);
			mCalllogListView.showContextMenu();
		} else if (mSMSListView.equals(parent)) {
			fileitem = (FileItems) parent.getItemAtPosition(position);
			mSMSListView.showContextMenu();
		}

	}

	
	private void spiltDateTime(String dateschedule) {

		String str1 = dateschedule;
		String eTime = str1.substring(0, str1.indexOf(" "));
		String[] Time = eTime.split(":");
		mHourOfDay = Integer.parseInt(Time[0]);
		mMinute = Integer.parseInt(Time[1]);
		String str2 = dateschedule;

		String eDate = str2.substring(str2.indexOf(" ") + 1);
		String[] Date = eDate.split("/");
		try {

			mDayOfMonth = Integer.parseInt(Date[0]);
			mMonthOfYear = Integer.parseInt(Date[1]) - 1;
			mYear = Integer.parseInt(Date[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		if (mCalllogListView.equals(parent)) {
			fileitem = (FileItems) parent.getItemAtPosition(position);
		} else if (mSMSListView.equals(parent)) {
			fileitem = (FileItems) parent.getItemAtPosition(position);
		}
		return false;
	}

	@Override
	protected void onDestroy() {
			adapter.close();
		super.onDestroy();
	}
}
