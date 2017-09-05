package org.baole.fakelog.helper;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;

public class ContactHelperSdk5 {//extends ContactHelper {
	protected Context ctx;
	
	public ContactHelperSdk5 (Context context) {
		this.ctx = context;
	}
//
//	public void setActivity(Context ctx) {
//		this.ctx = ctx;
//	}

	private static final String[] PEOPLE_PROJECTION = new String[] {
		Phone._ID, Phone.PHOTO_ID,
		Phone.TYPE, Phone.NUMBER,
		Phone.LABEL, Phone.DISPLAY_NAME};
	
	public Cursor getContactCursor() {
		return ctx.getContentResolver().query(Phone.CONTENT_URI,
				PEOPLE_PROJECTION, Phone.NUMBER + " IS NOT NULL",
				null, Phone.DISPLAY_NAME + " asc");
	}

	public String[] getFieldProjection() {
		return PEOPLE_PROJECTION;
	}

	public Cursor queryFilter(CharSequence constraint) {
		StringBuilder buffer = null;
		String[] args = null;
		
		if (constraint != null) {
			buffer = new StringBuilder();
			buffer.append("UPPER(");
			buffer.append(Phone.DISPLAY_NAME);
			buffer.append(") GLOB ?");
			args = new String[] { constraint.toString().toUpperCase() + "*" };
		}
		
		return ctx.getContentResolver().query(Phone.CONTENT_URI,
				PEOPLE_PROJECTION, buffer == null ? null : buffer
						.toString(), args,
						Phone.DISPLAY_NAME + " asc");
	}	
}
