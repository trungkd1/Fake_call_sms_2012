package org.baole.fakelog;

import java.io.File;
import java.io.IOException;

import org.baole.fakelog.model.Configuration;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewVoiceActivity extends Activity implements OnClickListener {

	private static final String LOG_TAG = "AudioRecordTest";
	private String mFileNametemp = null;
	private MediaRecorder mRecorder = null;
	private MediaPlayer mPlayer = null;
	private Button buttonPlay;
	private Button buttonSave;
	private Button buttonRecord;

	public NewVoiceActivity() {

		mFileNametemp = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		mFileNametemp += "/Voice.3gp";

	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.record_activity);

		buttonPlay = (Button) findViewById(R.id.buttonPlay);
		buttonSave = (Button) findViewById(R.id.buttonSave);
		buttonRecord = (Button) findViewById(R.id.buttonRecord);

		findViewById(R.id.buttonPlay).setOnClickListener(this);
		findViewById(R.id.buttonSave).setOnClickListener(this);
		findViewById(R.id.buttonCancel2).setOnClickListener(this);
		findViewById(R.id.buttonRecord).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.buttonPlay) {
            startPlaying();
        } else if (v.getId() == R.id.buttonSave) {
            showDialog();
        } else if (v.getId() == R.id.buttonCancel2) {
            cancel();
        } else if (v.getId() == R.id.buttonRecord) {
            if ((buttonRecord.getText()).equals("STOP RECORDING")) {
				stopRecording();
				buttonRecord.setText("Click here to Record");
				buttonPlay.setEnabled(true);
				buttonSave.setEnabled(true);
			} else {
				startRecording();
				buttonRecord.setText("STOP RECORDING");

			}
        } else {
        }

	}

	private void startPlaying() {

		if (mPlayer != null && mPlayer.isPlaying()) {
			return;
		}
		
		mPlayer = new MediaPlayer();
		buttonPlay.setText(R.string.stop);
		try {
			mPlayer.setDataSource(mFileNametemp);
			mPlayer.prepare();
			mPlayer.start();
			mPlayer.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					mPlayer.release();
					mPlayer = null;
					buttonPlay.setText(R.string.play_record);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void startRecording() {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(mFileNametemp);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mRecorder.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}

		mRecorder.start();
	}

	private void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}

		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}

	private void saveRecord(String name) {
		if (TextUtils.isEmpty(name)) {
			Toast.makeText(this, R.string.name_is_empty, Toast.LENGTH_LONG)
					.show();
			return;
		}
		File mAudioRecorderDirectory = new File(Configuration.AUDIO_DIR);
		mAudioRecorderDirectory.mkdirs();
		boolean res = false;
		try {
			if (mAudioRecorderDirectory.isDirectory()) {
				File mFiletemp = new File(mFileNametemp);
				mFiletemp.renameTo(new File(Configuration.AUDIO_DIR + name
						+ ".3gp"));
				res = true;
				Toast.makeText(this, R.string.add_new_audio, Toast.LENGTH_LONG)
						.show();
				setResult(RESULT_OK);
				finish();
			} else {
				res = false;
			}
		} catch (Throwable e1) {
			res = false;
		}

		if (!res) {
			Toast.makeText(this, R.string.cannot_save_audio, Toast.LENGTH_LONG)
					.show();
		}

		deleteFileRecord();
	}

	private void showDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		// alert.setTitle("Title");
		alert.setMessage("Name");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						saveRecord(input.getText().toString());
					}
				});

		alert.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});
		alert.show();
	}

	private void cancel() {
		deleteFileRecord();
		finish();
	}

	private void deleteFileRecord() {
		try {
			File mFiletemp = new File(mFileNametemp);
			mFiletemp.delete();
		} catch (Throwable e) {
		}
	}
}
