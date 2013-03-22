package com.xue.yynote.activity;

import java.io.IOException;

import com.xue.yynote.R;
import com.xue.yynote.model.NoteItemModel;
import com.xue.yynote.tools.DBHelper;
import com.xue.yynote.activity.NoteEditActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.app.Activity;
import android.app.AlertDialog;

public class ClockAlertActivity extends Activity implements OnClickListener, OnDismissListener{
	private int mNoteId;
    private String mSnippet;
    private static final int SNIPPET_PREW_MAX_LEN = 60;
    MediaPlayer mPlayer;
    
    
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

        Bundle bundle = this.getIntent().getExtras();

        try {
            //mNoteId = Long.valueOf(intent.getData().getPathSegments().get(1));
        	mNoteId = bundle.getInt("com.xue.yynote.NOTE_ID");
            mSnippet = this.getContentById(mNoteId);
            mSnippet = mSnippet.length() > SNIPPET_PREW_MAX_LEN ? mSnippet.substring(0,
                    SNIPPET_PREW_MAX_LEN) + getResources().getString(R.string.note_snippet_info)
                    : mSnippet;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return;
        }

        mPlayer = new MediaPlayer();
        this.showActionDialog();
       // this.playAlarmSound();
        
    }
    private String getContentById(int id){
    	DBHelper dbHelper = DBHelper.getInstance(this);
    	Log.d("TAG", "" + id);
    	Cursor cursor = dbHelper.query(DBHelper.TABLE.NOTES, new String[]{NoteItemModel.CONTENT}, NoteItemModel.ID + " = " + id, null, null);
    	cursor.moveToFirst();
    	String content = cursor.getString(0);
    	return content;
    }
    private boolean isScreenOn() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        return pm.isScreenOn();
    }
    private void playAlarmSound() {
        Uri url = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM);

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
        if (isScreenOn()) {
            dialog.setNegativeButton(R.string.clock_dialog_known, null);
        }
        dialog.show().setOnDismissListener(this);
    }
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                Intent intent = new Intent(this, NoteEditActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.putExtra("ID", mNoteId);
                startActivity(intent);
                break;
            default:
            	this.finish();
                break;
        }
    }
    public void onDismiss(DialogInterface dialog) {
        stopAlarmSound();
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
