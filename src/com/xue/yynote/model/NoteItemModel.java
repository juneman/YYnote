package com.xue.yynote.model;

import java.util.Date;
import java.util.Random;

import android.content.ContentValues;
import android.database.Cursor;
import com.xue.yynote.tools.DBHelper;
import com.xue.yynote.model.ClockModel;

public class NoteItemModel {
	public static final String ID = "_id";
	//public static final String BG_COLOR_ID = "bg_color_id";
	public static final String CLOCK_ID = "clock_id";
	public static final String CREATE_DATE = "create_date";
	public static final String MODIFY_DATE = "modify_date";
	public static final String CONTENT = "content";
	public static final String SEQUENCE = "sequence";
	
	public static final String[] mColumns = {
		ID,
		CLOCK_ID,
		CREATE_DATE,
		MODIFY_DATE,
		CONTENT,
		SEQUENCE
	};
	
	private static final int ID_COLUMN = 0;
	private static final int CLOCK_ID_COLUMN = 1;
	private static final int CREATE_DATE_COLUMN = 2;
	private static final int MODIFY_DATE_COLUMN = 3;
	private static final int CONTENT_COLUMN = 4;
	private static final int SEQUENCE_COLUMN = 5;
	
	private int mId;
	//private int mBgColorId;
	private int mClockId;
	private long mCreateDate;
	private long mModifyDate;
	private String mContent;
	private int mSequence;
	private ClockModel mClockModel;
	
	public NoteItemModel(){
		// TODO Auto-generated constructor stub
		//Random rand = new Random();
		this.mId = -1;
	//	this.mBgColorId = rand.nextInt(BgColor.COLOR_COUNT) + 1;
		this.mClockId =  -1;
		this.mCreateDate  = new Date().getTime();
		this.mModifyDate = new Date().getTime();
		this.mContent =  "";
		this.mSequence = 0;
		this.mClockModel = null;
	}
	
	public NoteItemModel(int id){
		DBHelper dbHelper = DBHelper.getInstance();
		Cursor cursor = dbHelper.query(DBHelper.TABLE.NOTES, null, NoteItemModel.ID+" = "+id, null, null);

		cursor.moveToFirst();
		this.mId = cursor.getInt(ID_COLUMN);
//	/	this.mBgColorId = cursor.getInt(BG_COLOR_ID_COLUMN);
		this.mClockId = cursor.getInt(CLOCK_ID_COLUMN);
		this.mCreateDate = cursor.getLong(CREATE_DATE_COLUMN);
		this.mModifyDate = cursor.getLong(MODIFY_DATE_COLUMN);
		this.mContent = cursor.getString(CONTENT_COLUMN);
		this.mSequence = cursor.getInt(SEQUENCE_COLUMN);
		this.mClockModel = this.getClockModel(this.mClockId);
	}
	
	public NoteItemModel(Cursor cursor) {
		// TODO Auto-generated constructor stub
		this.mId = cursor.getInt(ID_COLUMN);
	///	this.mBgColorId = cursor.getInt(BG_COLOR_ID_COLUMN);
		this.mClockId = cursor.getInt(CLOCK_ID_COLUMN);
		this.mCreateDate = cursor.getLong(CREATE_DATE_COLUMN);
		this.mModifyDate = cursor.getLong(MODIFY_DATE_COLUMN);
		this.mContent = cursor.getString(CONTENT_COLUMN);
		this.mSequence = cursor.getInt(SEQUENCE_COLUMN);
		this.mClockModel = this.getClockModel(this.mClockId);
	}
	
	public NoteItemModel(ContentValues values){
		// TODO Auto-generated constructor stub
		this.mId = values.getAsInteger(ID);
///		this.mBgColorId = values.getAsInteger(BG_COLOR_ID);
		this.mClockId = values.getAsInteger(CLOCK_ID);
		this.mCreateDate = values.getAsLong(CREATE_DATE);
		this.mModifyDate = values.getAsLong(MODIFY_DATE);
		this.mContent = values.getAsString(CONTENT);
		this.mSequence = values.getAsInteger(SEQUENCE);
		this.mClockModel = this.getClockModel(this.mClockId);
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
	
	public void setContent(String content){
		this.mContent =  content;
	}

	public boolean isClock(){
		return this.mClockModel == null ? false:true;
	}
	
}
