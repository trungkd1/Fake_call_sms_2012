package org.baole.fakelog;

import org.baole.ad.AdUtil;
import org.baole.fakelog.helper.NotificationHelper;
import org.baole.fakelog.model.Configuration;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class VoiceManagerActivity extends Activity {
	private RecordAdapter Adapter;
	private ListView mListRecorder;
	MediaPlayer mPlayer = null;
	private ArrayList<RecordItem> array_list;
	private final static int AUDIO_RECORD = 1;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.list_content_record);
		mListRecorder = (ListView) findViewById(R.id.list_content_record);
		mListRecorder.setEmptyView(findViewById(R.id.empty));

		loadAudioItems();

		findViewById(R.id.button_add).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (AdUtil.hasAd()) {
							NotificationHelper.createGoProDialog(
									VoiceManagerActivity.this,
									FakeLogActivity.PRO_PKG,
									R.string.voice_support_msg).show();
							// Intent pro = new Intent(Intent.ACTION_VIEW, Uri
							// .parse(String.format(
							// "market://details?id=%s",
							// "org.baole.fakelogpro")));
							// startActivity(pro);
						} else {
							startActivityForResult(new Intent(
									VoiceManagerActivity.this,
									NewVoiceActivity.class), AUDIO_RECORD);
						}
					}
				});
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mPlayer != null && mPlayer.isPlaying()) {
			try {
				mPlayer.release();
				mPlayer = null;
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public class RecordItem {
		public String name;

		public String path;
		public boolean isPlaying;

		public RecordItem(String name, String path) {
			this.name = name;
			this.path = path;
			isPlaying = false;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == AUDIO_RECORD) {
			loadAudioItems();
			setResult(RESULT_OK);
		}
	}

	private void loadAudioItems() {
		File mDirectory = new File(Configuration.AUDIO_DIR);

		File[] file = mDirectory.listFiles();
		array_list = new ArrayList<RecordItem>();

		try {
			if (file != null) {
				for (int i = 0; i < file.length; i++) {
					array_list.add(new RecordItem(file[i].getName(), file[i]
							.getPath()));
				}
			}

		} catch (Exception ex) {
			// Log.e("ERROR", ex.getMessage());
		}

		Adapter = new RecordAdapter(this, array_list);
		mListRecorder.setAdapter(Adapter);

	}

	class RecordAdapter extends BaseAdapter implements OnClickListener,
			OnCompletionListener {

		private LayoutInflater mInflater;
		private ArrayList<RecordItem> items;
		RecordItem mPlayingItem = null;

		public RecordAdapter(Context context, ArrayList<RecordItem> item) {
			mInflater = LayoutInflater.from(context);
			this.items = item;

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(
						org.baole.fakelog.R.layout.item_record, null);
				holder = new ViewHolder();
				holder.textRecordName = (TextView) convertView
						.findViewById(R.id.textRecordName);
				holder.textRecordDuration = (TextView) convertView
						.findViewById(R.id.textRecordDuration);
				holder.imagePlay = (ImageView) convertView
						.findViewById(R.id.imagePlay);
				holder.imageDelete = (ImageView) convertView
						.findViewById(R.id.image_delete);

				holder.imagePlay.setOnClickListener(this);
				holder.imageDelete.setOnClickListener(this);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			RecordItem item = items.get(position);
			holder.textRecordName.setText(item.name);
			holder.imagePlay.setTag(item);
			holder.imageDelete.setTag(item);

			if (item.isPlaying == true) {
				holder.imagePlay.setImageResource(R.drawable.thumbpause);
			} else {
				holder.imagePlay.setImageResource(R.drawable.thumbplay);
			}

			int length = -1;
			try {
				MediaPlayer mp = new MediaPlayer();
				FileInputStream fs = new FileInputStream(item.path);
				FileDescriptor fd = fs.getFD();
				mp.setDataSource(fd);
				mp.prepare(); // might be optional
				length = mp.getDuration();
				mp.release();
			} catch (Throwable e) {
				length = -1;
			}

			if (length > 0) {
				length = length / 1000;
				holder.textRecordDuration.setText(String.format("%02d:%02d",
						length / 60, length % 60));
			} else {
				holder.textRecordDuration.setText("");
			}

			return convertView;
		}

		class ViewHolder {
			TextView textRecordName;
			TextView textRecordDuration;
			ImageView imagePlay;
			ImageView imageDelete;

		}

		@Override
		public void onClick(View v) {

			if (v.getId() == R.id.image_delete) {
				final RecordItem item = (RecordItem) v.getTag();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						VoiceManagerActivity.this);
				builder.setMessage("Are you sure you want to delete this item?")
						.setCancelable(false)
						.setPositiveButton(android.R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

										(new File(item.path)).delete();
										array_list.remove(item);
										setResult(RESULT_OK);
										notifyDataSetChanged();
									}
								})
						.setNegativeButton(android.R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
				builder.create().show();
			} else {
				RecordItem item = (RecordItem) v.getTag();

				if (item.isPlaying) {
					stop(mPlayingItem);
				} else {
					stop(mPlayingItem);
					mPlayingItem = item;
					play(mPlayingItem);
				}
				notifyDataSetChanged();
			}

		}

		private void play(RecordItem item) {
			if (item != null) {
				item.isPlaying = true;
				try {
					mPlayer = new MediaPlayer();
					mPlayer.setOnCompletionListener(this);
					mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
					mPlayer.setDataSource(item.path);
					mPlayer.prepare();
					mPlayer.start();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void stop(RecordItem item) {
			if (item != null) {
				item.isPlaying = false;
				if (mPlayer != null) {
					try {
						mPlayer.release();
						mPlayer = null;
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		public void onCompletion(MediaPlayer mp) {
			stop(mPlayingItem);
			notifyDataSetChanged();
		}

	}

}
