package org.baole.fakelog.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.baole.fakelog.helper.DBAdapter;
import org.baole.fakelog.model.FileItems;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class SchedulerService extends Service{
 
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	private DBAdapter adapter = new DBAdapter(this);
	private ArrayList<FileItems> fileItems_SMS ;
	private ArrayList<FileItems> fileItems_Call ;
	
	protected Calendar nowCal;
	protected Calendar ScheduleCal;
	protected int mHourOfDay;
	protected int mMinute;
	protected int mYear;
	protected int mMonthOfYear;
	protected int mDayOfMonth;
	
	private void SpiltDateTime(String dateschedule) {

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
			mMonthOfYear = Integer.parseInt(Date[1])-1;
			mYear = Integer.parseInt(Date[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	public void ReloadShchedule(){		
		
		ScheduleCal = Calendar.getInstance();
		fileItems_Call = new ArrayList<FileItems>();
		fileItems_SMS = new ArrayList<FileItems>();
		try {
			adapter.openRead();
			fileItems_Call = adapter.queryCall();
			fileItems_SMS = adapter.querySMS();
		} catch (SQLException e) {
			
		}finally{
			adapter.close();
		}
		
		

		for(int i = 0 ; i < fileItems_SMS.size() ; i ++){
			SpiltDateTime(fileItems_SMS.get(i).getDateSchedule());
			
			ScheduleCal.set(mYear, mMonthOfYear, mDayOfMonth, mHourOfDay, mMinute);			
			if(ScheduleCal.after(nowCal) == true){
				long date = new GregorianCalendar(mYear, mMonthOfYear, mDayOfMonth,
						mHourOfDay, mMinute).getTimeInMillis();
				Intent intent = new Intent(this, ScheduleReceiver.class);
				AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);		
				
				intent.putExtra(ScheduleReceiver.NAME, fileItems_SMS.get(i).getName());
				intent.putExtra(ScheduleReceiver.NUMBER, fileItems_SMS.get(i).getNumber());
				intent.putExtra(ScheduleReceiver.MESSAGE, fileItems_SMS.get(i).getMessage());
				intent.putExtra(ScheduleReceiver.TYPE, fileItems_SMS.get(i).getType());
				intent.putExtra(ScheduleReceiver.READ,fileItems_SMS.get(i).getRead());
				intent.putExtra(ScheduleReceiver.NOTIF, fileItems_SMS.get(i).getNof());			
			
				intent.setAction("org.baole.fakelog.schedule_sms"+ fileItems_SMS.get(i).getSms_id());
				intent.putExtra("sms_id", fileItems_SMS.get(i).getSms_id());
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						this, fileItems_SMS.get(i).getSms_id(), intent, PendingIntent.FLAG_CANCEL_CURRENT);			
				alarm.set(AlarmManager.RTC_WAKEUP, date, pendingIntent);					
									
			}
		}
		
		
		for(int i = 0 ;i < fileItems_Call.size() ; i ++){
			SpiltDateTime(fileItems_Call.get(i).getDateSchedule());
			
			ScheduleCal.set(mYear, mMonthOfYear, mDayOfMonth, mHourOfDay, mMinute);			
			if(ScheduleCal.after(nowCal) == true){
				long date = new GregorianCalendar(mYear, mMonthOfYear, mDayOfMonth,
						mHourOfDay, mMinute).getTimeInMillis();
				Intent intent = new Intent(this, ScheduleReceiver.class);
				AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);				
				intent.putExtra(ScheduleReceiver.NAME, fileItems_Call.get(i).getName());
				intent.putExtra(ScheduleReceiver.NUMBER, fileItems_Call.get(i).getNumber());
				intent.putExtra(ScheduleReceiver.CALL_TYPE, fileItems_Call.get(i).getType());
				intent.putExtra(ScheduleReceiver.DURATION, fileItems_Call.get(i).getDuration());
				intent.putExtra(ScheduleReceiver.DATE, fileItems_Call.get(i).getDateSchedule());
				intent.putExtra(ScheduleReceiver.NOTIF, fileItems_Call.get(i).getNof());
				intent.setAction("org.baole.fakelog.schedule_call"+ fileItems_Call.get(i).getCall_id());
				intent.putExtra("call_id", fileItems_Call.get(i).getCall_id());
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						this, fileItems_Call.get(i).getCall_id(), intent, PendingIntent.FLAG_CANCEL_CURRENT);			
				alarm.set(AlarmManager.RTC_WAKEUP, date, pendingIntent);				
			}	
		}		
	}
	
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);	

		Bundle bundle = intent.getExtras();
		boolean isProximity = false;
		if (bundle != null) {
			isProximity = bundle.getBoolean(ScheduleReceiver.PROXIMITY);
		}

		
		if (isProximity) {
			ProximityListener.create(getApplicationContext());
			ProximityListener.register(getApplicationContext(), intent);
		} else {
			ReloadShchedule();
			this.stopSelf();
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		nowCal = Calendar.getInstance();	
	}
}
