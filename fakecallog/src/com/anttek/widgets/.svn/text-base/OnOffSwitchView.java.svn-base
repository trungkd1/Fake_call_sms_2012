package com.anttek.widgets;

import org.baole.fakelog.R;

import vn.lmchanh.lib.widget.gesture.GestureArgs;
import vn.lmchanh.lib.widget.gesture.GestureRootLinearLayout;
import vn.lmchanh.lib.widget.gesture.GestureSource;
import vn.lmchanh.lib.widget.gesture.GestureTarget;
import vn.lmchanh.lib.widget.gesture.listener.GestureListener;
import vn.lmchanh.lib.widget.gesture.listener.SimpleGestureListener;
import vn.lmchanh.lib.widget.gesture.slide2action.SlideObserver;
import vn.lmchanh.lib.widget.gesture.slide2action.SlideObserver.DIRECTION;
import vn.lmchanh.lib.widget.gesture.widget.GestureImageView;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class OnOffSwitchView extends FrameLayout {
	// =================================================================================

	private SlideObserver mSlideObserver;
	private TextView mOnOffStatusText;
	private GestureImageView mOnImage;
	private GestureImageView mOffImage;
	private OnStatusSwitchListener mListener;

	private String onMsg;
	private String offMsg;

	// =================================================================================

	public OnOffSwitchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.initialize();
	}

	// =================================================================================

	private void initialize() {
		LayoutInflater.from(this.getContext()).inflate(
				R.layout.layout_on_off_switch, this);

		GestureRootLinearLayout layout = (GestureRootLinearLayout) 
		this.findViewById(R.id.layout_on_off);
		
		this.mOnOffStatusText = (TextView)layout.findViewById(R.id.text_on_off_status);
		this.mSlideObserver = new SlideObserver(this.getContext());
		layout.setGestureObverser(mSlideObserver);

		this.mOnImage = (GestureImageView) layout.findViewById(R.id.ges_img_on);
		this.mOffImage = (GestureImageView) layout.findViewById(R.id.ges_img_off);

		SimpleGestureListener listener = new SimpleGestureListener() {
			@Override
			public void onGestureExcute(GestureSource source,
					GestureTarget target, GestureArgs args) {
				if (target.getId() == R.id.ges_img_on) {
					mListener.onSwitch(false);
				} else if (target.getId() == R.id.ges_img_off) {
					mListener.onSwitch(true);
				}
			}
		};

		this.mOnImage.setGestureListener(listener);
		this.mOffImage.setGestureListener(listener);

		this.mSlideObserver.setGestureListener(new GestureListener() {
			@Override
			public void onGestureStart(GestureSource source, GestureArgs args) {
				((View) source).setVisibility(View.INVISIBLE);
			}

			@Override
			public void onGestureEnd(GestureSource source) {
				((View) source).setVisibility(View.VISIBLE);

			}

			@Override
			public void onGestureExcute(GestureSource source, int x, int y,
					GestureArgs args, GestureTarget target) {
			}
		});
	}

	public void setStatusSwitchListener(OnStatusSwitchListener listener) {
		this.mListener = listener;
	}

	public void setOnMsg(String msg) {
		this.onMsg = msg;
	}

	public void setOffMsg(String msg) {
		this.offMsg = msg;
	}

	public void setUpOn() {
		mOffImage.setVisibility(INVISIBLE);
		mOnImage.setBackgroundResource(R.color.transparent);
		mSlideObserver.setSlideTarget(mOnImage);
		mSlideObserver.setDirection(DIRECTION.RIGHT_TO_LEFT);
		mOnOffStatusText.setText(onMsg);
	}

	public void setUpOff() {
		mOnImage.setVisibility(INVISIBLE);
		mOffImage.setBackgroundResource(R.color.transparent);
		mSlideObserver.setSlideTarget(mOffImage);
		mSlideObserver.setDirection(DIRECTION.LEFT_TO_RIGHT);
		mOnOffStatusText.setText(offMsg);
	}
	
	
	public void setUp() {
		this.mOnImage.setOnTouchListener(this.mOnOffTouchListener);
		this.mOffImage.setOnTouchListener(this.mOnOffTouchListener);
	}

	
	private void handle(boolean on){
		if(on){
			setUpOn();
		}else{
			setUpOff();
		}
		
	}

	private OnTouchListener mOnOffTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				mSlideObserver.startGesture(v, (GestureSource) v, null);
			}
			if(v.getId() == R.id.ges_img_on){
				handle(false);
			} else if (v.getId() == R.id.ges_img_off){
				handle(true);

			}
		return false;
		}
	};

	// =================================================================================

	public interface OnStatusSwitchListener {
		void onSwitch(boolean on);
	}

	// =================================================================================
}
