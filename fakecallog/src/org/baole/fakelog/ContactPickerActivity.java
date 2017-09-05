package org.baole.fakelog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.baole.fakelog.helper.ContactHelperSdk5;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AlphabetIndexer;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SectionIndexer;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class ContactPickerActivity extends Activity implements
		OnItemClickListener {
	public static final String CONTACT = "Contacts";
	public static final String CALL_LOG = "CallLogs";
	public static final String SMS_LOG = "SMS";
	public static final String NAME = "name";
	public static final String NUMBER = "number";
	public static final String MESSAGE = "message";
	public static final String DATESCHEDULE = "dateschedule";
	public static final String DURATION = "duration";
	public static final String CALLTYPE = "calltype";
	public static final String READ = "read";
	public static final String TYPE = "type";
	public static final String ID = "id";
	public static final String VOICE = "voice";
	
	private ListView mContactListView;
	private ListView mCalllogListView;
	private ListView mSMSListView;

	private TabHost myTabHost;
	private Cursor mContactCursor;
	private ArrayList<CallLogEntry> mCallLogData;
	private ArrayList<SMSEntry> mSMSData;

	private static final String[] PHONE_CONTACT = new String[] { Phone._ID,
			Phone.DISPLAY_NAME, Phone.NUMBER, Phone.TYPE };

	private static final String[] CALL_LOGS = new String[] { Calls._ID,
			Calls.CACHED_NAME, Calls.NUMBER, Calls.TYPE, Calls.DATE };

	private static final String[] SMS = new String[] { Calls._ID, "person",
			"address", "body" };

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContactListView = new ListView(this);
		mCalllogListView = new ListView(this);
		mSMSListView = new ListView(this);

		setContentView(R.layout.contact_picker_activity);

		myTabHost = (TabHost) findViewById(android.R.id.tabhost);
		myTabHost.setup();
		myTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);

		TabSpec ts = null;

		ts = myTabHost.newTabSpec(CALL_LOG);
		ts.setIndicator(createTabView(this, CALL_LOG));
		ts.setContent(new TabHost.TabContentFactory() {
			public View createTabContent(String tag) {
				loadCallLogs();
				return mCalllogListView;
			}
		});
		myTabHost.addTab(ts);

		ts = myTabHost.newTabSpec(SMS_LOG);
		ts.setIndicator(createTabView(this, SMS_LOG));
		ts.setContent(new TabHost.TabContentFactory() {
			public View createTabContent(String tag) {
				loadSMSLogs();
				return mSMSListView;
			}
		});
		myTabHost.addTab(ts);

		ts = myTabHost.newTabSpec(CONTACT);
		ts.setIndicator(createTabView(this, CONTACT));
		ts.setContent(new TabHost.TabContentFactory() {
			public View createTabContent(String tag) {
				loadContactList();
				return mContactListView;
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

	ContactListAdapter mContactAdapter;
	ContactHelperSdk5 mContactHelper;

	public static class ContactListAdapter extends CursorAdapter implements
			Filterable {
		Activity ctx;

		public ContactListAdapter(Activity context, Cursor c) {
			super(context, c);
			ctx = context;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			final LayoutInflater inflater = LayoutInflater.from(context);
			final TextView view = (TextView) inflater.inflate(
					android.R.layout.simple_dropdown_item_1line, parent, false);
			view.setText(cursor.getString(5) + "[" + cursor.getString(3) + "]");
			return view;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			((TextView) view).setText(cursor.getString(5) + "("
					+ cursor.getString(3) + ")");
		}

		@Override
		public String convertToString(Cursor cursor) {
			return cursor.getString(5);
		}

		@Override
		public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
			if (getFilterQueryProvider() != null) {
				return getFilterQueryProvider().runQuery(constraint);
			}
			return new ContactHelperSdk5(ctx).queryFilter(constraint);
		}
	}

	static class SMSEntry {
		String mName;
		String mNumber;
		String mBody;

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SMSEntry other = (SMSEntry) obj;
			if (mNumber == null) {
				if (other.mNumber != null)
					return false;
			} else if (!mNumber.equals(other.mNumber))
				return false;
			return true;
		}
	}

	protected void loadSMSLogs() {
		Cursor cursor = getContentResolver().query(Uri.parse("content://sms"),
				SMS, null, null, null);

		mSMSData = new ArrayList<SMSEntry>();

		if (cursor.moveToFirst()) {
			do {
				SMSEntry e = new SMSEntry();
				e.mName = cursor.getString(1);
				e.mNumber = cursor.getString(2);
				e.mBody = cursor.getString(3);
				if (!mSMSData.contains(e))
					mSMSData.add(e);
			} while (cursor.moveToNext());
		}

		ListAdapter adapter = new SMSLogsAdapter(this, mSMSData);
		mSMSListView.setAdapter(adapter);
		mSMSListView.setOnItemClickListener(this);
	}

	static class CallLogEntry {
		String mName;
		String mNumber;
		int mType;
		int mDate;

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CallLogEntry other = (CallLogEntry) obj;
			if (mNumber == null) {
				if (other.mNumber != null)
					return false;
			} else if (!mNumber.equals(other.mNumber))
				return false;
			return true;
		}

	}

	protected void loadCallLogs() {
		Cursor cursor = getContentResolver().query(Calls.CONTENT_URI,
				CALL_LOGS, null, null, Calls.DATE + " DESC");

		mCallLogData = new ArrayList<CallLogEntry>();

		if (cursor.moveToFirst()) {
			do {
				CallLogEntry e = new CallLogEntry();
				e.mName = cursor.getString(1);
				e.mNumber = cursor.getString(2);
				e.mType = cursor.getInt(3);
				e.mDate = cursor.getInt(4);
				if (!mCallLogData.contains(e))
					mCallLogData.add(e);
			} while (cursor.moveToNext());
		}

		ListAdapter adapter = new CallLogsAdapter(this, mCallLogData);
		mCalllogListView.setAdapter(adapter);
		mCalllogListView.setOnItemClickListener(this);
	}

	private void loadContactList() {
		mContactCursor = getContentResolver().query(Phone.CONTENT_URI,
				PHONE_CONTACT, Phone.NUMBER + " IS NOT NULL", null,
				Phone.DISPLAY_NAME + " asc");
		ListAdapter adapter = new ContactAdapter(this, mContactCursor);
		mContactListView.setAdapter(adapter);

		mContactListView.setOnItemClickListener(this);
		mContactListView.setScrollContainer(true);
		mContactListView
				.setScrollBarStyle(ScrollView.SCROLLBARS_INSIDE_OVERLAY);
		mContactListView.setFastScrollEnabled(true);
		mContactListView.setTextFilterEnabled(true);
	}

	public static String getStringByType(int type) {
		String numberType;
		switch (type) {
		case Phone.TYPE_MOBILE:
			numberType = "Mobile";
			break;
		case Phone.TYPE_HOME:
			numberType = "Home";
			break;
		case Phone.TYPE_WORK:
			numberType = "Work";
			break;
		default:
			numberType = "Other";
			break;
		}
		return numberType;
	}

	public static class SMSLogsAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private ArrayList<SMSEntry> data;

		public SMSLogsAdapter(Context context, ArrayList<SMSEntry> data) {
			mInflater = LayoutInflater.from(context);
			this.data = data;
		}

		public int getCount() {
			return data.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(
						android.R.layout.simple_list_item_2, null);
				holder = new ViewHolder();
				holder.text1 = (TextView) convertView
						.findViewById(android.R.id.text1);
				holder.text2 = (TextView) convertView
						.findViewById(android.R.id.text2);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			SMSEntry e = data.get(position);
			holder.text1.setText(formatLine1(e));
			holder.text2.setText(e.mBody);
			return convertView;
		}

		private CharSequence formatLine1(SMSEntry e) {
			if (TextUtils.isEmpty(e.mName))
				return e.mNumber;
			return String.format("%s [%s]", e.mName, e.mNumber);
		}

		static class ViewHolder {
			TextView text1;
			TextView text2;
		}
	}

	private static class CallLogsAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private ArrayList<CallLogEntry> data;

		public CallLogsAdapter(Context context, ArrayList<CallLogEntry> data) {
			// Cache the LayoutInflate to avoid asking for a new one each time.
			mInflater = LayoutInflater.from(context);
			this.data = data;
		}

		public int getCount() {
			return data.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(
						android.R.layout.simple_list_item_2, null);
				holder = new ViewHolder();
				holder.text1 = (TextView) convertView
						.findViewById(android.R.id.text1);
				holder.text2 = (TextView) convertView
						.findViewById(android.R.id.text2);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			CallLogEntry e = data.get(position);
			holder.text1.setText(formatLine1(e));
			holder.text2.setText(formatLine2(e));
			return convertView;
		}

		private CharSequence formatLine2(CallLogEntry e) {
			String type = null;

			switch (e.mType) {
			case Calls.INCOMING_TYPE:
				type = "Incoming";
				break;
			case Calls.OUTGOING_TYPE:
				type = "Outgoing";
				break;
			default:
				type = "Missed call";
				break;
			}

			return String.format("%s (%s)", SimpleDateFormat.getInstance()
					.format(new Date(e.mDate)), type);
		}

		private CharSequence formatLine1(CallLogEntry e) {
			if (TextUtils.isEmpty(e.mName))
				return e.mNumber;
			return String.format("%s [%s]", e.mName, e.mNumber);
		}

		static class ViewHolder {
			TextView text1;
			TextView text2;
		}
	}

	static class ContactAdapter extends CursorAdapter implements SectionIndexer {
		private AlphabetIndexer mIndexer;
		private LayoutInflater mInflater;

		public ContactAdapter(Context context, Cursor c) {
			super(context, c);
			mIndexer = new AlphabetIndexer(c, 1, " ABCDEFGHIJKLMNOPQRSTUVWXYZ");
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TextView text1 = (TextView) view.findViewById(android.R.id.text1);
			TextView text2 = (TextView) view.findViewById(android.R.id.text2);
			text1.setText(cursor.getString(1));
			text2.setText(cursor.getString(2) + " ["
					+ getStringByType(cursor.getInt(3)) + "]");
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View v = mInflater.inflate(android.R.layout.simple_list_item_2,
					null);
			bindView(v, context, cursor);
			return v;
		}

		public int getPositionForSection(int section) {
			return mIndexer.getPositionForSection(section);
		}

		public int getSectionForPosition(int position) {
			return mIndexer.getPositionForSection(position);
		}

		public Object[] getSections() {
			return mIndexer.getSections();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> view, View arg1, int position,
			long arg3) {
		if (position < 0) {
			return;
		}

		if (mContactListView.equals(view)) {
			Cursor c = (Cursor) mContactListView.getItemAtPosition(position);
			updateAndFinish(c.getString(1), c.getString(2));
		} else if (mCalllogListView.equals(view)) {
			CallLogEntry e = mCallLogData.get(position);
			updateAndFinish(e.mName, e.mNumber);
		} else if (mSMSListView.equals(view)) {
			SMSEntry e = mSMSData.get(position);
			updateAndFinish(e.mName, e.mNumber);
		}
	}

	protected void updateAndFinish(String name, String number) {
		Intent data = new Intent();

		data.putExtra(NAME, name);
		data.putExtra(NUMBER, number);
		data.putExtra("ContactPicker", 1);

		setResult(RESULT_OK, data);
		finish();
	}

}
