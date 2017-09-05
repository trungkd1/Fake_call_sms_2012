package org.baole.fakelog.helper;

import java.sql.SQLException;
import java.util.ArrayList;

import org.baole.fakelog.model.FileItems;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
	public static final String NAME = "name";
	public static final String MESSAGE = "message";
	public static final String NUMBER = "number";
	public static final String DATESCHEDULE = "dateschedule";
	public static final String READ = "read";
	public static final String TYPE = "type";
	public static final String DURATION = "duration";
	public static final String CALL_ID = "call_id";
	public static final String SMS_ID = "sms_id";
	public static final String NOF = "nof";
	public static final String VOICE = "voice";
	private static final String TAG = "DBAdapter";

	private static final String DATABASE_NAME = "Database.sqlite";
	private static final String TABLE_CALL = "tblCall";
	private static final String TABLE_SMS = "tblSMS";
	private static final int DATABASE_VERSION = 1;

	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public DBAdapter(Context ctx) {
		this.context = ctx;
		// this.context.deleteDatabase(DATABASE_NAME);
		DBHelper = new DatabaseHelper(context);
	}

	public DBAdapter openWrite() throws SQLException {
		db = DBHelper.getWritableDatabase();

		return this;
	}

	public DBAdapter openRead() throws SQLException {
		db = DBHelper.getReadableDatabase();

		return this;
	}

	public void close() {
		DBHelper.close();
	}

	public boolean RemoveSMS(int sms_id) {
		return db.delete(TABLE_SMS, SMS_ID + "=" + sms_id, null) > 0;
	}

	public boolean RemoveCall(int call_id) {
		return db.delete(TABLE_CALL, CALL_ID + "=" + call_id, null) > 0;
	}

	public boolean InsertOrUpdateSMS(FileItems fileitem) {
		ContentValues initialValues = new ContentValues();
		try {
			if (fileitem.getSms_id() > 0) {
				initialValues.put(NAME, fileitem.getName());
				initialValues.put(NUMBER, fileitem.getNumber());
				initialValues.put(MESSAGE, fileitem.getMessage());
				initialValues.put(DATESCHEDULE, fileitem.getDateSchedule());
				initialValues.put(READ, fileitem.getRead());
				initialValues.put(TYPE, fileitem.getType());
				initialValues.put(NOF, fileitem.getNof());

				return db.update(TABLE_SMS, initialValues, SMS_ID + "="
						+ fileitem.getSms_id(), null) > 0;
			} else {
				initialValues.put(NAME, fileitem.getName());
				initialValues.put(NUMBER, fileitem.getNumber());
				initialValues.put(MESSAGE, fileitem.getMessage());
				initialValues.put(DATESCHEDULE, fileitem.getDateSchedule());
				initialValues.put(READ, fileitem.getRead());
				initialValues.put(TYPE, fileitem.getType());
				initialValues.put(NOF, fileitem.getNof());
				return db.insert(TABLE_SMS, null, initialValues) > 0;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public boolean InsertOrUpdateCall(FileItems fileitem) {
		ContentValues initialValues = new ContentValues();
		if (fileitem.getCall_id() > 0) {
			initialValues.put(NAME, fileitem.getName());
			initialValues.put(NUMBER, fileitem.getNumber());
			initialValues.put(DATESCHEDULE, fileitem.getDateSchedule());
			initialValues.put(DURATION, fileitem.getDuration());
			initialValues.put(TYPE, fileitem.getType());
			initialValues.put(NOF, fileitem.getNof());
			return db.update(TABLE_CALL, initialValues, CALL_ID + "="
					+ fileitem.getCall_id(), null) > 0;
		} else {
			initialValues.put(NAME, fileitem.getName());
			initialValues.put(NUMBER, fileitem.getNumber());
			initialValues.put(DATESCHEDULE, fileitem.getDateSchedule());
			initialValues.put(DURATION, fileitem.getDuration());
			initialValues.put(TYPE, fileitem.getType());
			initialValues.put(NOF, fileitem.getNof());
			initialValues.put(VOICE, fileitem.getVoice());
			return db.insert(TABLE_CALL, null, initialValues) > 0;
		}
	}

	public int QuerySMS_ID() {
		String query = "select max(sms_id) sms_id from " + TABLE_SMS;
		Cursor c = db.rawQuery(query, null);
		int sms_id = 0;
		try {
			if (c != null && c.moveToFirst())
				do {
					sms_id = Integer.parseInt(c.getString(c
							.getColumnIndex("sms_id")));
				} while (c.moveToNext());
		} finally {
			c.close();

		}
		return sms_id;
	}

	public int QueryCALL_ID() {
		String query = "select max(call_id) call_id from " + TABLE_CALL;
		Cursor c = db.rawQuery(query, null);
		int call_id = 0;
		try {
			if (c != null && c.moveToFirst())
				do {
					call_id = Integer.parseInt(c.getString(c
							.getColumnIndex("call_id")));
				} while (c.moveToNext());
		} finally {
			c.close();

		}
		return call_id;
	}

	public ArrayList<FileItems> querySMS() {
		String query = "Select * from " + TABLE_SMS;

		Cursor c = db.rawQuery(query, null);

		ArrayList<FileItems> arraylist = new ArrayList<FileItems>();
		try {
			if (c != null && c.moveToFirst())
				do {
					int sms_id = Integer.parseInt(c.getString(c
							.getColumnIndex("sms_id")));
					String name = c.getString(c.getColumnIndex("name"));
					String number = c.getString(c.getColumnIndex("number"));
					String message = c.getString(c.getColumnIndex("message"));
					String dateSchedule = c.getString(c
							.getColumnIndex("dateschedule"));
					int read = Integer.parseInt(c.getString(c
							.getColumnIndex("read")));
					int type = Integer.parseInt(c.getString(c
							.getColumnIndex("type")));
					int nof = Integer.parseInt(c.getString(c
							.getColumnIndex("nof")));
					arraylist.add(new FileItems(sms_id, name, number, message,
							dateSchedule, read, type, nof));
				} while (c.moveToNext());

		} finally {
			c.close();
		}
		return arraylist;

	}

	public ArrayList<FileItems> queryCall() {
		String query = "Select * from " + TABLE_CALL;

		Cursor c = db.rawQuery(query, null);

		ArrayList<FileItems> arraylist = new ArrayList<FileItems>();
		try {
			if (c != null && c.moveToFirst())
				do {
					int call_id = Integer.parseInt(c.getString(c
							.getColumnIndex("call_id")));
					String name = c.getString(c.getColumnIndex("name"));
					String number = c.getString(c.getColumnIndex("number"));
					String dateSchedule = c.getString(c
							.getColumnIndex("dateschedule"));
					int duration = Integer.parseInt(c.getString(c
							.getColumnIndex("duration")));
					int type = Integer.parseInt(c.getString(c
							.getColumnIndex("type")));
					int nof = Integer.parseInt(c.getString(c
							.getColumnIndex("nof")));
					String record = c.getString(c.getColumnIndex("voice"));

					arraylist.add(new FileItems(call_id, name, number,
							dateSchedule, duration, type, nof, record));
				} while (c.moveToNext());

		} finally {
			c.close();
		}
		return arraylist;

	}

	public class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);

		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			String TABLE_SMS = "CREATE TABLE IF NOT EXISTS tblSMS( sms_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
					+ "name VARCHAR,number VARCHAR, message VARCHAR,dateschedule VARCHAR,read INTEGER,type INTEGER,nof INTEGER)";
			db.execSQL(TABLE_SMS);

			String TABLE_CALL = "CREATE TABLE IF NOT EXISTS tblCALL( call_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
					+ "name VARCHAR,number VARCHAR,dateschedule VARCHAR,duration INTEGER,type INTEGER,nof INTEGER,voice VARCHAR)";
			db.execSQL(TABLE_CALL);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("drop table if EXISTS " + TABLE_CALL);
			db.execSQL("drop table if EXISTS " + TABLE_SMS);
			onCreate(db);

		}

	}

}
