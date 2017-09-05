package org.baole.fakelog.model;

import android.app.PendingIntent;

public class PendingItems {
	private PendingIntent pendingIntent;
	private int ID;
	
	public PendingIntent getPendingIntent() {
		return pendingIntent;
	}
	public void setPendingIntent(PendingIntent pendingIntent) {
		this.pendingIntent = pendingIntent;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	
	public PendingItems(int ID, PendingIntent pendingIntent){
		this.ID = ID;
		this.pendingIntent = pendingIntent;
	}
}
