package com.xue.yynote.view;

import java.io.File;
import java.io.IOException;

import com.xue.yynote.R;
import com.xue.yynote.model.NoteItemModel;
import com.xue.yynote.tools.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.widget.Button;
import android.widget.LinearLayout;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;

public class NoteEditAudioView extends LinearLayout implements OnClickListener {
	public static final String TAG = "NoteEditAudioView";
	public static final int STATUS_UNSTART = 0;
	public static final int STATUS_RECORDING = 1;
	public static final int STATUS_RECORDED = 2;
	public static final int STATUS_PLAYING = 3;
	private Button mAudioBtn;
	private int status;
	private MediaRecorder mRecorder;
	private MediaPlayer mPlayer;
	private String mRecordFile;

	public NoteEditAudioView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		inflate(context, R.layout.activity_note_edit_audio, this);
		this.initResource();
	}

	private void initResource() {
		// TODO Auto-generated method stub
		this.mAudioBtn = (Button) this
				.findViewById(R.id.activity_note_edit_audio_btn);
		this.mAudioBtn.setOnClickListener(this);
		this.status = STATUS_UNSTART;
		this.mRecordFile = this.getContext()
				.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
				.getAbsolutePath();
		this.mRecordFile += "/audio_" + System.currentTimeMillis() + ".3gp";
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (this.status == STATUS_UNSTART) {
			this.mAudioBtn.setText(R.string.note_edit_audio_stop);
			this.startRecord();
			this.status = STATUS_RECORDING;
		} else if (this.status == STATUS_RECORDING) {
			this.mAudioBtn.setText(R.string.note_edit_audio_play);
			this.stopRecord();
			this.status = STATUS_RECORDED;
		} else if (this.status == STATUS_RECORDED) {
			this.mAudioBtn.setText(R.string.note_edit_audio_stop);
			this.startPlay();
			this.status = STATUS_PLAYING;
		} else if (this.status == STATUS_PLAYING) {
			this.mAudioBtn.setText(R.string.note_edit_audio_play);
			this.stopPlay();
			this.status = STATUS_RECORDED;
		}
	}

	public void startRecord() {
		// TODO Auto-generated method stub
		this.mRecorder = new MediaRecorder();
		this.mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		this.mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		this.mRecorder.setOutputFile(mRecordFile);
		this.mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		try {
			mRecorder.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.mRecorder.start();
	}

	public void stopRecord() {
		// TODO Auto-generated method stub
		if (mRecorder != null) {
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
		}
	}

	public void saveAudio(int noteId) {
		// TODO Auto-generated method stub
		ContentValues cv = new ContentValues();
		cv.put(NoteItemModel.AUDIO, this.mRecordFile);
		DBHelper dbHelper = DBHelper.getInstance(this.getContext());
		dbHelper.update(DBHelper.TABLE.NOTES, cv, NoteItemModel.ID + " = "
				+ noteId, null);
	}

	public void unSaveAudio() {
		// TODO Auto-generated method stub
		File f = new File(this.mRecordFile);
		f.delete();
	}

	private void startPlay() {
		// TODO Auto-generated method stub
		this.mPlayer = new MediaPlayer();
		this.mPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				NoteEditAudioView.this.mAudioBtn
						.setText(R.string.note_edit_audio_play);
				NoteEditAudioView.this.stopPlay();
				NoteEditAudioView.this.status = STATUS_RECORDED;
			}

		});
		try {
			mPlayer.setDataSource(mRecordFile);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void stopPlay() {
		// TODO Auto-generated method stub
		mPlayer.release();
		mPlayer = null;
	}

	public String getFile() {
		// TODO Auto-generated method stub
		return this.mRecordFile;
	}

}
