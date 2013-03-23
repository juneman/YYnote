package com.xue.yynote.view;

import com.xue.yynote.R;
import com.xue.yynote.activity.NoteEditActivity;
import com.xue.yynote.model.NoteItemModel;
import com.xue.yynote.tools.DBHelper;
import com.xue.yynote.activity.ClockReceiver;
import com.xue.yynote.model.ClockModel;
import com.xue.yynote.view.NoteEditView;
import com.xue.yynote.view.clock.ClockView;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.content.ContentValues;
import android.content.Intent;
import android.text.format.DateUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;    
import java.io.File;  
import java.text.SimpleDateFormat;
import java.util.Date;

import android.provider.MediaStore;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;



public class NoteEditView extends LinearLayout implements OnClickListener{
	public static final String TAG = "NoteEditView";
	
	public static final int STATUS_EDIT = 0;
	public static final int STATUS_CREATE = 1;
	private LinearLayout mTitle;
	private TextView mModifyTime;
	private TextView mClockTime;
	private ImageView mClock;
	private ImageView mPicture;
	private ImageView mShare;
	
	private ImageView mSave;
	private LinearLayout mMain;
	public EditText mContent;
	
	private NoteItemModel mNoteItemModel;
	private int status;
	
	public NoteEditView(Context context){
		super(context);
		inflate(context, R.layout.activity_note_edit, this);
		this.initResource();
		int[] a = new int[10];
		
	}

	private void initResource(){
		this.mNoteItemModel = null;
		this.mMain = (LinearLayout)findViewById(R.id.note_edit_main);
		this.mContent = (EditText)findViewById(R.id.note_edit_content);
		this.mSave = (ImageView)findViewById(R.id.note_edit_save);
		this.mShare = (ImageView)findViewById(R.id.note_edit_share);
		this.mPicture = (ImageView)findViewById(R.id.note_edit_picture);
		this.mModifyTime = (TextView)findViewById(R.id.note_edit_modify_time);
		this.mClock = (ImageView)findViewById(R.id.note_edit_clock);
		this.mClockTime = (TextView)findViewById(R.id.note_edit_clock_time);
		
		this.mClock.setOnClickListener(this);
		this.mClockTime.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				NoteEditView.this.addClockMenuWithDelete();
			}
			
		});
		
		this.mContent.addTextChangedListener(new TextWatcher(){
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
		        NoteEditView.this.mNoteItemModel.setContent(s.toString());
			}
			
		});
		
		this.mSave.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(NoteEditView.this.getContentLength() > 0){
					NoteEditView.this.finishEdit();
		    		Bundle bundle = new Bundle(); 
		    		bundle.putInt("NOTE_ID", NoteEditView.this.getModelId());
		    		NoteEditActivity mActvity = (NoteEditActivity) NoteEditView.this.getContext();
		    		mActvity.setResult(Activity.RESULT_OK, mActvity.getIntent().putExtras(bundle));
		    		mActvity.finish();
				}
		    //	Log.d(TAG, "note is saved succeed");
			}
			
		});
		this.mShare.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				NoteEditView.this.addShare();
			}
			
		});
		this.mPicture.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				NoteEditView.this.addPicture();
			}			
		});
	}	
	
	protected void addShare() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TITLE, "Share my note...");
		intent.putExtra(Intent.EXTRA_TEXT, "I have an event to share: " + this.mNoteItemModel.getContent() + "\nFrom YYnote");
		this.getContext().startActivity(Intent.createChooser(intent, "Share"));		
	}
	
	protected void addPicture(){
		final CharSequence[] items = { "从相册中选择", "拍照" };  
        AlertDialog dlg = new AlertDialog.Builder(this.getContext()).setTitle("选择应用程序").setItems(items,   
            new DialogInterface.OnClickListener() {   
                public void onClick(DialogInterface dialog,int item) {   
                    //这里item是根据选择的方式,  
                    //在items数组里面定义了两种方式, 拍照的下标为1所以就调用拍照方法         
                    if(item==1){   
                        Intent getImageByCamera= new Intent("android.media.action.IMAGE_CAPTURE");     
                        ((Activity) NoteEditView.this.getContext()).startActivityForResult(getImageByCamera, 2);     
                    }else{   
                        Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);   
                        getImage.addCategory(Intent.CATEGORY_OPENABLE);   
                        getImage.setType("image/*");   
                        ((Activity) NoteEditView.this.getContext()).startActivityForResult(getImage, 1);   
                     }   
                }   
            }).create();   
        dlg.show();   
	}
	
	protected void addClockMenu() {
		// TODO Auto-generated method stub
		final ClockView mClockView = new ClockView(this.getContext());
		mClockView.setClockModel(this.mNoteItemModel.getClockModel());
		Dialog mAlertDialog = new AlertDialog.Builder(this.getContext()).
			setTitle(R.string.clock_dialog_select_time).
			setView(mClockView).
			setPositiveButton(R.string.clock_dialog_ok, new DialogInterface.OnClickListener() {
					
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					NoteEditView.this.addClockFunction();
				}
			}).setNegativeButton(R.string.clock_dialog_cancel, null).create();
		mAlertDialog.show();
	}
	
	protected void addClockMenuWithDelete(){
		final ClockView mClockView = new ClockView(this.getContext());
		mClockView.setClockModel(this.mNoteItemModel.getClockModel());
		Dialog mAlertDialog = new AlertDialog.Builder(this.getContext()).
			setTitle(R.string.clock_dialog_select_time).
			setView(mClockView).
			setPositiveButton(R.string.clock_dialog_ok, new DialogInterface.OnClickListener() {
					
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					NoteEditView.this.addClockFunction();
				}
			}).setNeutralButton(R.string.clock_dialog_delete, new DialogInterface.OnClickListener() {
				
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					NoteEditView.this.deleteClock();
				}
			}).setNegativeButton(R.string.clock_dialog_cancel, null).create();
		mAlertDialog.show();
	}
	
	public void addClockFunction(){
		ClockModel mClockModel = this.mNoteItemModel.getClockModel();
		DBHelper dbHelper = DBHelper.getInstance(this.getContext());
		ContentValues cv = mClockModel.formatContentValues();
		if(this.mNoteItemModel.getClockId() < 0){
			long id = dbHelper.insert(DBHelper.TABLE.ALERTS, cv);
			this.mNoteItemModel.setClockId((int)id);
		}
		else {
			dbHelper.update(DBHelper.TABLE.ALERTS, cv, 
			ClockModel.ID + " = " + NoteEditView.this.mNoteItemModel.getClockId(), 
			null);
		}

		this.mClockTime.setText(this.getFormatClockTime(mClockModel.getTimeInMillis()));
		
		Intent intent = new Intent(this.getContext(), ClockReceiver.class);
		Bundle bundle = new Bundle();
		bundle.putInt("com.xue.yynote.NOTE_ID", this.mNoteItemModel.getId());
		intent.putExtras(bundle);
		Log.d(TAG, "" + this.mNoteItemModel.getId());
		Log.d(TAG, "now   " + new Date().getTime());
		Log.d(TAG, "setted " + mClockModel.getTimeInMillis());
		//intent.setDataAndType(ContentUris.withAppendedId(Notes.CONTENT_NOTE_URI, mClockModel.getId()));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getContext(), 0, intent, 0);
        AlarmManager am = (AlarmManager) this.getContext().getSystemService(Context.ALARM_SERVICE);
        //设置闹钟
        am.set(AlarmManager.RTC_WAKEUP, mClockModel.getTimeInMillis(), pendingIntent);
        
        //设置重复闹钟
        if(mClockModel.getAlertTimes() > 1 && mClockModel.getAlertInterval() > 0){
        	am.setRepeating(AlarmManager.RTC_WAKEUP, mClockModel.getTimeInMillis(), mClockModel.getAlertIntervalInMillis(), pendingIntent);
        }
	}
	protected void deleteClock() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this.getContext(), ClockReceiver.class);
		Bundle bundle = new Bundle();
		bundle.putInt("com.xuewish.xnotes.NOTE_ID", this.mNoteItemModel.getId());
		intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getContext(), 0, intent, 0);
		//获取系统进程
        AlarmManager am = (AlarmManager) this.getContext().getSystemService(Context.ALARM_SERVICE);
        //cancel
        am.cancel(pendingIntent);
        
		DBHelper dbHelper = DBHelper.getInstance(this.getContext());
		dbHelper.delete(DBHelper.TABLE.ALERTS, ClockModel.ID + " = " + this.mNoteItemModel.getClockId(), null);
		
		this.mNoteItemModel.setClockId(-1);
		
		this.mClockTime.setText("");
		this.mClock.setOnClickListener(this);
	}
	
	@SuppressWarnings("deprecation")
	private CharSequence getFormatClockTime(long clockTime) {
		// TODO Auto-generated method stub
		Date dateClock = new Date(clockTime);
		Date dateNow = new Date();
		String format = "";
		if(dateClock.getYear() > dateNow.getYear()){
			format += String.valueOf(dateClock.getYear()) + "-";
			format += "" + (dateClock.getMonth()+1) + "-" + dateClock.getDate();
		}
		else if(dateClock.getMonth() > dateNow.getMonth() || dateClock.getDate() > dateNow.getDate())
			format = "" + (dateClock.getMonth()+1) + "-" + dateClock.getDate();
		else if(dateClock.getMonth() == dateNow.getMonth() && dateClock.getDate() == dateNow.getDate())
			format = "" + dateClock.getHours() + ":" + dateClock.getMinutes();
		else {
			if(dateClock.getYear() < dateNow.getYear())
				format += String.valueOf(dateClock.getYear()) + "-";
			format += "" + (dateClock.getMonth()+1) + "-" + dateClock.getDate();
		}
		return format;
	}
	@SuppressWarnings("deprecation")
	private CharSequence getFormatModifyTime(long time){
		SimpleDateFormat formatter1 = new SimpleDateFormat ("yyyy/MM/dd");
		SimpleDateFormat formatter2 = new SimpleDateFormat ("MM/dd");
		Date dateNow = new Date();
		Date dateModify = new Date(time);
		String format = "";
		if((dateNow.getYear() == dateModify.getYear()) && (dateNow.getMonth() == dateModify.getMonth()) && (dateNow.getDate() == dateModify.getDate())){
			format = "" + dateModify.getHours() + ":" + dateModify.getMinutes();
		}
		else if(dateNow.getYear() == dateModify.getYear()){
			format = formatter2.format(dateModify);
		}
		else{
			format = formatter1.format(dateModify);
		}
		return format;
	}
	public void finishEdit(){
		DBHelper dbHelper = DBHelper.getInstance(this.getContext());
		
		if(this.status == STATUS_CREATE){
			int id = (int) dbHelper.insert(DBHelper.TABLE.NOTES, 
					this.mNoteItemModel.formatContentValuesWithoutId());
			this.mNoteItemModel.setId(id);
			
		}else if(this.status == STATUS_EDIT){
			dbHelper.update(DBHelper.TABLE.NOTES, this.mNoteItemModel.formatContentValuesWithoutId(), 
					NoteItemModel.ID + " = " + this.mNoteItemModel.getId(), null);
			
		}
	}
	public void createNoteEditModel() {
		// TODO Auto-generated method stub
		this.mNoteItemModel = new NoteItemModel();		
		this.status = STATUS_CREATE;
	}
	public void setNoteEditModel(int mNoteId) {
		// TODO Auto-generated method stub
		DBHelper dbHelper = DBHelper.getInstance(this.getContext());
		Cursor cursor = dbHelper.query(DBHelper.TABLE.NOTES, null, NoteItemModel.ID+" = "+mNoteId, null, null);
		cursor.moveToFirst();
		this.mNoteItemModel = new NoteItemModel(cursor);
		this.setContent(this.mNoteItemModel.getContent());
		
		//this.mModifyTime.setText(DateUtils.getRelativeTimeSpanString(this.mNoteItemModel.getModifyDate()));
		//this.mModifyTime.setText(this.getFormatModifyTime(this.mNoteItemModel.getModifyDate()));
		if(this.mNoteItemModel.isClock()){
			ClockModel mClock = this.mNoteItemModel.getClockModel();
			long time = mClock.getTimeInMillis();		
			
			this.mClockTime.setText(this.getFormatClockTime(time));	
			
			this.mClock.setOnClickListener(null);
		}
		this.status = STATUS_EDIT;
	}
	//添加分享进来的文本内容
	public void setContentText(String contentText){
		this.createNoteEditModel();
		this.mNoteItemModel.setContent(contentText);
		this.setContent(contentText);
	}
	
	private void setContent(String content) {
		// TODO Auto-generated method stub
		this.mContent.setText(content);
		int index = 0;
		
		while(content.indexOf("[0x64", index) >= 0){
			index = content.indexOf("[0x64", index);
			int start = index + 5;
			int end = content.indexOf("]", start);
			File root = this.getContext().getFilesDir();
			String filePath = root.getPath() + content.substring(start, end);
			Log.d(TAG, "" + content.subSequence(index, end+1));
			Bitmap bitmap  = BitmapFactory.decodeFile(filePath);
			//根据Bitmap对象创建ImageSpan对象  
	        ImageSpan imageSpan = new ImageSpan(this.getContext(), bitmap);  
	        //创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像  
	        SpannableString spannableString = new SpannableString(content.subSequence(index, end+1));  
	        //  用ImageSpan对象替换face  
	        spannableString.setSpan(imageSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
	        //将选择的图片追加到EditText中光标所在位置
	        Editable edit_text = this.mContent.getEditableText();
	        edit_text.delete(index, end+1);
	        if(index <0 || index >= content.length()){  
	        	edit_text.append(spannableString);  
            }else{  
            	edit_text.insert(index, spannableString);  
            }
	        index+=1;
		}
	}

	public int getContentLength(){
		return this.mNoteItemModel.getContent().length();
	}

	public int getModelId() {
		// TODO Auto-generated method stub
		return this.mNoteItemModel.getId();
	}
	public void onClick(View v) {
		// TODO Auto-generated method stub
		NoteEditView.this.addClockMenu();
	}
}
