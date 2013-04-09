package com.xue.yynote.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.xue.yynote.tools.DBHelper;
import com.xue.yynote.model.ClockModel;

public class NoteItemModel {
	public static final String TAG="NoteItemModel";
	public static final String ID = "_id";
	//public static final String BG_COLOR_ID = "bg_color_id";
	public static final String CLOCK_ID = "clock_id";
	public static final String CREATE_DATE = "create_date";
	public static final String MODIFY_DATE = "modify_date";
	public static final String CONTENT = "content";
	public static final String AUDIO = "audio";
	public static final String SEQUENCE = "sequence";
	
	public static final String[] mColumns = {
		ID,
		CLOCK_ID,
		CREATE_DATE,
		MODIFY_DATE,
		CONTENT,
		AUDIO,
		SEQUENCE
	};
	
	private static final int ID_COLUMN = 0;
	private static final int CLOCK_ID_COLUMN = 1;
	private static final int CREATE_DATE_COLUMN = 2;
	private static final int MODIFY_DATE_COLUMN = 3;
	private static final int CONTENT_COLUMN = 4;
	private static final int AUDIO_COLUMN = 5;
	private static final int SEQUENCE_COLUMN = 6;
	
	private Context mContext;
	private int mId;
	//private int mBgColorId;
	private int mClockId;
	private long mCreateDate;
	private long mModifyDate;
	private String mContent;
	private String mAudio;
	private int mSequence;
	private ClockModel mClockModel;
	private ArrayList<String> images;
	public NoteItemModel(Context context){
		// TODO Auto-generated constructor stub
		//Random rand = new Random();
		this.mContext = context;
		this.mId = -1;
	//	this.mBgColorId = rand.nextInt(BgColor.COLOR_COUNT) + 1;
		this.mClockId =  -1;
		this.mCreateDate  = new Date().getTime();
		this.mModifyDate = new Date().getTime();
		this.mContent =  "";
		this.mAudio = "";
		this.mSequence = 0;
		this.mClockModel = null;
		this.findImages(this.mContent);
	}
	
	public NoteItemModel(Context context, int id){
		this.mContext = context;
		DBHelper dbHelper = DBHelper.getInstance(context);
		Cursor cursor = dbHelper.query(DBHelper.TABLE.NOTES, null, NoteItemModel.ID+" = "+id, null, null);

		cursor.moveToFirst();
		this.mId = cursor.getInt(ID_COLUMN);
//	/	this.mBgColorId = cursor.getInt(BG_COLOR_ID_COLUMN);
		this.mClockId = cursor.getInt(CLOCK_ID_COLUMN);
		this.mCreateDate = cursor.getLong(CREATE_DATE_COLUMN);
		this.mModifyDate = cursor.getLong(MODIFY_DATE_COLUMN);
		this.mContent = cursor.getString(CONTENT_COLUMN);
		this.mAudio =cursor.getString(AUDIO_COLUMN);
		this.mSequence = cursor.getInt(SEQUENCE_COLUMN);
		this.mClockModel = this.getClockModel(this.mClockId);
		this.findImages(this.mContent);
	}
	
	public NoteItemModel(Context context, Cursor cursor) {
		this.mContext = context;
		// TODO Auto-generated constructor stub
		this.mId = cursor.getInt(ID_COLUMN);
	///	this.mBgColorId = cursor.getInt(BG_COLOR_ID_COLUMN);
		this.mClockId = cursor.getInt(CLOCK_ID_COLUMN);
		this.mCreateDate = cursor.getLong(CREATE_DATE_COLUMN);
		this.mModifyDate = cursor.getLong(MODIFY_DATE_COLUMN);
		this.mContent = cursor.getString(CONTENT_COLUMN);
		this.mAudio =cursor.getString(AUDIO_COLUMN);
		this.mSequence = cursor.getInt(SEQUENCE_COLUMN);
		this.mClockModel = this.getClockModel(this.mClockId);
		this.findImages(this.mContent);
	}
	
	public NoteItemModel(Context context, ContentValues values){
		this.mContext = context;
		// TODO Auto-generated constructor stub
		this.mId = values.getAsInteger(ID);
///		this.mBgColorId = values.getAsInteger(BG_COLOR_ID);
		this.mClockId = values.getAsInteger(CLOCK_ID);
		this.mCreateDate = values.getAsLong(CREATE_DATE);
		this.mModifyDate = values.getAsLong(MODIFY_DATE);
		this.mContent = values.getAsString(CONTENT);
		this.mAudio = values.getAsString(AUDIO);
		this.mSequence = values.getAsInteger(SEQUENCE);
		this.mClockModel = this.getClockModel(this.mClockId);
		this.findImages(this.mContent);
	}
	
	public ContentValues formatContentValuesWithoutId() {
		// TODO Auto-generated method stub
		this.mModifyDate = System.currentTimeMillis();
		ContentValues cv = new ContentValues();
	///	cv.put(BG_COLOR_ID, this.mBgColorId);
		cv.put(CLOCK_ID, this.mClockId);
		cv.put(CREATE_DATE, this.mCreateDate);
		cv.put(MODIFY_DATE, this.mModifyDate);
		cv.put(CONTENT, this.mContent);
		cv.put(AUDIO, this.mAudio);
		cv.put(SEQUENCE, this.mSequence);
		return cv;
	}
	
	private ClockModel getClockModel(int alertId){
		DBHelper dbHelper = DBHelper.getInstance();
		Cursor cursor = dbHelper.query(DBHelper.TABLE.ALERTS, null, ClockModel.ID + " = " + alertId, null, "");
		if(cursor.getCount() > 0){
			cursor.moveToFirst();
			return new ClockModel(cursor);
		}
		return null;
	}
	
	public int getId() {
		// TODO Auto-generated method stub
		return this.mId;
	}
	
	public void setId(int id){
		this.mId = id;
	}

	public int getClockId(){
		return this.mClockId;
	}
	
	public void setClockId(int clockId){
		this.mClockId = clockId;
		if(clockId >= 0){
			if(this.mClockModel != null){
				this.mClockModel.setId(clockId);
			}else {
				this.mClockModel = this.getClockModel(this.mClockId);
			}
		} else {
			this.mClockModel = null;
		}
	}
	
	public ClockModel getClockModel(){
		if(this.mClockModel == null)
			this.mClockModel = new ClockModel();
		
		return this.mClockModel;
	}
	
	public void setClockModel(ClockModel clockModel){
		this.mClockModel = clockModel;
	}
	
	public int getSequence(){
		return this.mSequence;
	}
	
	public void setSequence(int sequence){
		this.mSequence = sequence;
	}
	
	public long getModifyDate(){
		return this.mModifyDate;
	}
	
	public void setModifyDate(long modifyDate){
		this.mModifyDate = modifyDate;
	}
	
	public String getContent(){
		return this.mContent;
	}
	
	public String getContentForItem(){
		int index = 0;
		String content = this.mContent;
		while(content.indexOf("[0x64", index) >= 0){
			index = content.indexOf("[0x64");
			int end = content.indexOf("]", index+1);
			content = content.replace(content.substring(index, end+1), "[Pic]");
			index += 1;
		}
		return content;
	}
	
	public void setContent(String content){
		this.mContent =  content;
	}
	
	public String getAudio(){
		return this.mAudio;
	}

	public void setAudio(String audio){
		this.mAudio = audio;
	}
	
	public boolean isClock(){
		return this.mClockId < 0 ? false:true;
	}
	
	public void findImages(String content){
		int index = 0;
		this.images = new ArrayList<String>();
		while(content.indexOf("[0x64", index) >= 0){
			index = content.indexOf("[0x64", index);
			int start = index + 5;
			int end = content.indexOf("]", start);
			//File root = this.mContext.getFilesDir();
			//String filePath = root.getPath() + content.substring(start, end);
			Log.d(TAG, "" + content.subSequence(index, end+1));
			this.images.add((String) content.subSequence(index, end+1)); 
	        index+=1;
		}
	}
	
	public ArrayList<String> getImages(){
		return this.images;
	}

	public Bitmap getBitmapByTag(String image) {
		// TODO Auto-generated method stub
		File root = this.mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		String filePath = root.getAbsolutePath() + image.substring(5, image.length()-1);
		return BitmapFactory.decodeFile(filePath);
	}

	public Uri getImageUri(String image) {
		// TODO Auto-generated method stub
		File root = this.mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		String filePath = root.getAbsolutePath() + image.substring(5, image.length()-1);
		Log.i(TAG, Uri.fromFile(new File(filePath)).toString());
		return Uri.fromFile(new File(filePath));
	}

	public Uri getAudioUri() {
		// TODO Auto-generated method stub
		if(this.mAudio.length() > 0){
			return Uri.fromFile(new File(this.mAudio));
		}
		else return null; 
	}
}
