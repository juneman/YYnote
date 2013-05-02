package com.xue.yynote.activity;

import java.io.IOException;

import com.xue.yynote.R;
import com.xue.yynote.model.ClockModel;
import com.xue.yynote.model.NoteItemModel;
import com.xue.yynote.tools.DBHelper;
import com.xue.yynote.activity.NoteEditActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;

public class ClockAlertActivity extends Activity implements OnClickListener,
		OnDismissListener {
	private int mNoteId;
	private String mSnippet;
	private static final int SNIPPET_PREW_MAX_LEN = 60;
	MediaPlayer mPlayer;
	private Vibrator mVibrator;
	private NoteItemModel mNoteModel;
	private ClockModel mClockModel;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		final Window win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

		if (!isScreenOn()) {
			win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
					| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
					| WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
					| WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);
		}

		Intent intent = this.getIntent();

		try {
			mNoteId = Integer.valueOf(intent.getData().getPathSegments().get(1));
			Log.d("ClockAlert", "note_id: " + mNoteId);
			mNoteModel = new NoteItemModel(this, mNoteId);
			mClockModel = this.mNoteModel.getClockModel();

			mSnippet = this.mNoteModel.getContentForItem();
			mSnippet = mSnippet.length() > SNIPPET_PREW_MAX_LEN ? mSnippet
					.substring(0, SNIPPET_PREW_MAX_LEN)
					+ getResources().getString(R.string.note_snippet_info)
					: mSnippet;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return;
		}

		mPlayer = new MediaPlayer();
		this.mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		this.showActionDialog();
		this.playAlarmSound();

		this.mVibrator.vibrate(new long[] { 1000L, 1000L, 1000L, 1000L }, 0);

	}

	private boolean isScreenOn() {
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		return pm.isScreenOn();
	}

	private void playAlarmSound() {
		Uri url = RingtoneManager.getActualDefaultRingtoneUri(this,
				RingtoneManager.TYPE_ALARM);

		int silentModeStreams = Settings.System.getInt(getContentResolver(),
				Settings.System.MODE_RINGER_STREAMS_AFFECTED, 0);

		if ((silentModeStreams & (1 << AudioManager.STREAM_ALARM)) != 0) {
			mPlayer.setAudioStreamType(silentModeStreams);
		} else {
			mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
		}
		try {
			mPlayer.setDataSource(this, url);
			mPlayer.prepare();
			mPlayer.setLooping(true);
			mPlayer.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void showActionDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(R.string.app_name);
		dialog.setMessage(mSnippet);

		dialog.setPositiveButton(R.string.clock_dialog_go, this);
		if (!this.mClockModel.isClockFinished()) {
			dialog.setNeutralButton(R.string.clock_dialog_delete, this);
		}
		if (isScreenOn()) {
			dialog.setNegativeButton(R.string.clock_dialog_known, null);
		}
		dialog.show().setOnDismissListener(this);
		this.mClockModel.addAlertCount();
	}

	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			Intent intent = new Intent(this, NoteEditActivity.class);
			intent.setAction(Intent.ACTION_VIEW);
			intent.putExtra("ID", mNoteId);
			startActivity(intent);
			break;
		case DialogInterface.BUTTON_NEUTRAL:
			// TODO Auto-generated method stub
			this.deleteClock();
			break;
		default:
			break;
		}
	}

	public void deleteClock() {
		Intent intent = new Intent(this, ClockReceiver.class);
		Bundle bundle = new Bundle();
		bundle.putInt("com.xuewish.xnotes.NOTE_ID", this.mNoteModel.getId());
		intent.putExtras(bundle);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
				intent, 0);
		// 获取系统进程
		AlarmManager am = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);
		// cancel
		am.cancel(pendingIntent);
		DBHelper dbHelper = DBHelper.getInstance(this);
		dbHelper.delete(DBHelper.TABLE.ALERTS, ClockModel.ID + " = "
				+ this.mNoteModel.getClockId(), null);

		this.mNoteModel.setClockId(-1);
		dbHelper.update(DBHelper.TABLE.NOTES,
				this.mNoteModel.formatContentValuesWithoutId(),
				NoteItemModel.ID + " = " + this.mNoteModel.getId(), null);
	}

	public void onDismiss(DialogInterface dialog) {
		stopAlarmSound();
		this.mVibrator.cancel();
		if (this.mClockModel.isClockFinished())
			this.deleteClock();
		finish();
	}

	private void stopAlarmSound() {
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
		}
	}

}
