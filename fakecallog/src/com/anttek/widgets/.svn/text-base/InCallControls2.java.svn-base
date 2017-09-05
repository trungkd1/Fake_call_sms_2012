/**
 * Copyright (C) 2010 Regis Montoya (aka r3gis - www.r3gis.fr)
 * Copyright (C) 2008 The Android Open Source Project
 * 
 * This file is part of CSipSimple.
 *
 *  CSipSimple is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  CSipSimple is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CSipSimple.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.anttek.widgets;

import org.baole.fakelog.FakeCallPageActivity;
import org.baole.fakelog.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.anttek.widgets.SlidingTab.OnTriggerListener;

public class InCallControls2 extends FrameLayout implements OnTriggerListener,
		OnClickListener {

	private static final String THIS_FILE = "InCallControls";
	OnTriggerListener onTriggerListener;
	private SlidingTab slidingTabWidget;
	private RelativeLayout inCallButtons;
	private boolean useSlider;
	private LinearLayout alternateLockerWidget;

	private boolean supportMultipleCalls = false;

	/**
	 * Interface definition for a callback to be invoked when a tab is triggered
	 * by moving it beyond a target zone.
	 */
	public interface OnTriggerListener {
		/**
		 * When user clics on take call
		 */
		int TAKE_CALL = 1;
		/**
		 * When user clics on take call
		 */
		int DECLINE_CALL = TAKE_CALL + 1;

		/**
		 * Called when the user make an action
		 * 
		 * @param whichAction
		 *            what action has been done
		 */
		void onTrigger(int whichAction);
	}

	public InCallControls2(Context context, AttributeSet attrs) {
		super(context, attrs);

		supportMultipleCalls = true;
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.in_call_controls2, this, true);
		useSlider = true;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		slidingTabWidget = (SlidingTab) findViewById(R.id.takeCallUnlocker);
		inCallButtons = (RelativeLayout) findViewById(R.id.inCallButtons);

		// settingsButton = (ImageButton) findViewById(R.id.settingsButton);

		// Finalize object style
		slidingTabWidget.setLeftHintText(R.string.take_call);
		slidingTabWidget.setRightHintText(R.string.decline_call);
		inCallButtons.setVisibility(GONE);
		setCallLockerVisibility(VISIBLE);
		inCallButtons.setVisibility(GONE);

		// Attach objects
		slidingTabWidget.setOnTriggerListener(this);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

		final int parentWidth = r - l;
		final int parentHeight = b - t;
		final int top = parentHeight * 3 / 4 - slidingTabWidget.getHeight() / 2;
		final int bottom = parentHeight * 3 / 4 + slidingTabWidget.getHeight()
				/ 2;
		slidingTabWidget.layout(0, top, parentWidth, bottom);

	}

	private void setCallLockerVisibility(int visibility) {
		if (useSlider) {
			slidingTabWidget.setVisibility(visibility);
		} else {
			alternateLockerWidget.setVisibility(visibility);
		}
	}

	/**
	 * Registers a callback to be invoked when the user triggers an event.
	 * 
	 * @param listener
	 *            the OnTriggerListener to attach to this view
	 */
	public void setOnTriggerListener(OnTriggerListener listener) {
		onTriggerListener = listener;
	}

	private void dispatchTriggerEvent(int whichHandle) {
		if (onTriggerListener != null) {
			onTriggerListener.onTrigger(whichHandle);
		}
	}

	@Override
	public void onTrigger(View v, int whichHandle) {
		Log.d(THIS_FILE, "Call controls receive info from slider "
				+ whichHandle);

		switch (whichHandle) {
		case LEFT_HANDLE:
			Log.d(THIS_FILE, "We take the call");

			dispatchTriggerEvent(OnTriggerListener.TAKE_CALL);
			break;
		case RIGHT_HANDLE:
			Log.d(THIS_FILE, "We clear the call");
			dispatchTriggerEvent(OnTriggerListener.DECLINE_CALL);
		default:
			break;
		}
		slidingTabWidget.resetView();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		// case R.id.settingsButton:
		// dispatchTriggerEvent(OnTriggerListener.MEDIA_SETTINGS);
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(THIS_FILE, "Hey you hit the key : " + keyCode);
		switch (keyCode) {
		case KeyEvent.KEYCODE_CALL:
			dispatchTriggerEvent(OnTriggerListener.TAKE_CALL);
			return true;
		case KeyEvent.KEYCODE_ENDCALL:
			// case KeyEvent.KEYCODE_POWER:
			dispatchTriggerEvent(OnTriggerListener.DECLINE_CALL);
			return true;
		default:
			break;
		}

		return super.onKeyDown(keyCode, event);
	}

	public void applyTheme(FakeCallPageActivity t) {
		// Apply backgrounds

		// To buttons

		// To sliding tab
		slidingTabWidget.setLeftTabDrawables(
				t.getDrawableResource("ic_jog_dial_answer"),
				t.getDrawableResource("jog_tab_target_green"),
				t.getDrawableResource("jog_tab_bar_left_answer"),
				t.getDrawableResource("jog_tab_left_answer"));

		slidingTabWidget.setRightTabDrawables(
				t.getDrawableResource("ic_jog_dial_decline"),
				t.getDrawableResource("jog_tab_target_red"),
				t.getDrawableResource("jog_tab_bar_right_decline"),
				t.getDrawableResource("jog_tab_right_decline"));

	}

}
