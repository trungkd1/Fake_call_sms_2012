package org.baole.fakelog;

import org.baole.ad.AdUtil;
import org.baole.fakelog.model.Configuration;
import org.baole.fakelog.model.FileItems;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

public class ImageSwitcherCall extends Activity implements
		OnItemSelectedListener, ViewFactory {
	private Intent intent;
	private int mPosition;
	private ImageSwitcher mSwitcher;
	private Gallery g;
	private int Start;
	private TextView text;
	private static String[] mVersion;
	private Configuration mConfig;
	private static final int ICE_CREAM_SANDWICH = 14;
	int SDK_INT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.imageswitcher);
		reload();
		intent = new Intent();
		mSwitcher = (ImageSwitcher) findViewById(R.id.imgswitcher);
		Start = 0;
		mSwitcher.setFactory(this);
		mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_in));
		mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_out));
		Resources res = getResources();
		SDK_INT = Build.VERSION.SDK_INT;
		mVersion = res.getStringArray(R.array.version);
		text = (TextView) findViewById(R.id.textView1);
		text.setText("Selected :" + mVersion[mConfig.mId_image]);
		g = (Gallery) findViewById(R.id.gallery);
		g.setAdapter(new ImageAdapter(this));
		g.setOnItemSelectedListener(this);
		g.setSelection(11);
		// g.setSelection(mConfig.mId_image);

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		if ((FileItems.id_image > 0 && position == 0 && Start == 0)) {
			mSwitcher.setImageResource(mImageIds[mConfig.mId_image]);
			Start = 1;
			g.setSelection(mConfig.mId_image);
		} else {
			mSwitcher.setImageResource(mImageIds[position]);
			g.setSelection(position);
		}

		boolean block = true;
		boolean isAndroid_4_0 = false;
		if (AdUtil.hasAd()) {
			if (position == 0 || position == 1 || position == 2
					|| position == 7) {
				block = false;
			}
		} else {
			block = false;
			if (position == 11) {
				if (SDK_INT >= ICE_CREAM_SANDWICH) {
					isAndroid_4_0 = true;
				}
			}
		}

		if (block) {
			Toast.makeText(getApplicationContext(),
					R.string.fake_screen_pro_notification, Toast.LENGTH_SHORT)
					.show();
		} else {

			if (position == 11) {
				if (SDK_INT >= ICE_CREAM_SANDWICH) {
					text.setText("Selected: " + mVersion[position]);
					mPosition = position;
				} else {
					Toast.makeText(getApplicationContext(),
							R.string.fake_screen_pro_notification,
							Toast.LENGTH_SHORT).show();
				}
			} else {
				text.setText("Selected: " + mVersion[position]);
				mPosition = position;
			}

		}

	}

	private void reload() {
		mConfig = Configuration.getInstance();
		mConfig.init(this);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	@Override
	protected void onDestroy() {
		mConfig.mId_image = mPosition;
		mConfig.saveConfig();
		intent.putExtra("position", mPosition);
		setResult(RESULT_OK, intent);
		super.onDestroy();
	}

	@Override
	public View makeView() {
		ImageView i = new ImageView(this);
		try {
			i.setBackgroundColor(0xFF000000);
			i.setScaleType(ImageView.ScaleType.FIT_CENTER);
			i.setLayoutParams(new ImageSwitcher.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

			return i;
		} finally {
			Log.e("MAKE VIEW", "run");
			i = null;
		}

	}

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ImageAdapter(Context c) {
			this.mContext = c;
		}

		@Override
		public int getCount() {
			return mThumbIds.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView image;

			if (convertView == null) {
				image = new ImageView(mContext);
				image.setAdjustViewBounds(true);
				image.setLayoutParams(new Gallery.LayoutParams(45, 75));
				image.setScaleType(ScaleType.FIT_CENTER);
				image.setBackgroundResource(R.drawable.picture_frame);
				convertView = image;
			} else {
				image = (ImageView) convertView;
			}

			image.setImageResource(mThumbIds[position]);
			return convertView;
		}
	}

	public Integer[] mThumbIds = { R.drawable.thumb1, R.drawable.thumb2,
			R.drawable.thumb3, R.drawable.thumb4, R.drawable.thumb5,
			R.drawable.thumb6, R.drawable.thumb7, R.drawable.thumb8,
			R.drawable.thumb9, R.drawable.thumb10, R.drawable.thumb11,
			R.drawable.thumb12 };

	public static Integer[] mImageIds = { R.drawable.receive1,
			R.drawable.receive2, R.drawable.receive3, R.drawable.receive4,
			R.drawable.receive5, R.drawable.receive_sony,
			R.drawable.receive_sony, R.drawable.receive_sony,
			R.drawable.receive_sony, R.drawable.receive_sony,
			R.drawable.receive_sony, R.drawable.receive6 };
}
