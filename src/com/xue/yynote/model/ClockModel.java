package com.xue.yynote.model;

import com.xue.yynote.tools.DBHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class ClockModel {
	public static final String TAG = "AlertModel";

	public static final String ID = "_id";
	public static final String TIME = "time";
	public static final String ALERT_INTERVAL = "alert_interval";
	public static final String ALERT_TIMES = "alert_times";
	public static final String ALERT_COUNT = "alert_count";

	public static final String[] mColumns = { ID, TIME, ALERT_INTERVAL,
			ALERT_TIMES, ALERT_COUNT };

	public static final int BASE_YEAR = 2013;

	public static final int ID_COLUMN = 0;
	public static final int TIME_COLUMN = 1;
	public static final int ALERT_INTERVAL_COLUMN = 2;
	public static final int ALERT_TIMES_COLUMN = 3;
	public static final int ALERT_COUNT_COLUMN = 4;

	private int mId;
	private long mTime;
	private int mAlertInterval;
	private int mAlertTimes;
	private int mAlertCount;

	public ClockModel() {
		this.mId = -1;
		this.mTime = System.currentTimeMillis();
		this.mAlertInterval = 0;
		this.mAlertTimes = 1;
		this.mAlertCount = 1;
	}

	public ClockModel(Cursor cursor) {
		this.mId = cursor.getInt(ID_COLUMN);
		this.mTime = cursor.getLong(TIME_COLUMN);
		this.mAlertInterval = cursor.getInt(ALERT_INTERVAL_COLUMN);
		this.mAlertTimes = cursor.getInt(ALERT_TIMES_COLUMN);
		this.mAlertCount = cursor.getInt(ALERT_COUNT_COLUMN);
	}

	public ContentValues formatContentValues() {
		ContentValues cv = new ContentValues();
		cv.put(TIME, this.mTime);
		cv.put(ALERT_INTERVAL, this.mAlertInterval);
		cv.put(ALERT_TIMES, this.mAlertTimes);
		cv.put(ALERT_COUNT, this.mAlertCount);
		return cv;
	}

	public int getId() {
		return this.mId;
	}

	public void setId(int id) {
		if (id < 0)
			return;
		this.mId = id;
	}

	public long getTimeInMillis() {
		return this.mTime + 60 * 1000 * (this.mAlertCount - 1)
				* this.mAlertInterval;
	}

	public void setTimeInMillis(long time) {
		if (time < 0)
			this.mTime = 0;
		else
			this.mTime = time;
	}

	public int getAlertInterval() {
		return this.mAlertInterval;
	}

	public long getAlertIntervalInMillis() {
		return this.mAlertInterval * 60 * 1000;
	}

	public void setAlertInterval(int alertInterval) {
		if (alertInterval < 0)
			this.mAlertInterval = 0;
		else
			this.mAlertInterval = alertInterval;
	}

	public int getAlertTimes() {
		return this.mAlertTimes;
	}

	public void setAlertTimes(int alertTimes) {
		if (alertTimes < 1)
			this.mAlertTimes = 1;
		else
			this.mAlertTimes = alertTimes;
	}

	public boolean isClockFinished() {
		if (this.mAlertCount < this.mAlertTimes) {
			return false;
		} else
			return true;
	}

	public void addAlertCount() {
		this.mAlertCount += 1;
		DBHelper dbHelper = DBHelper.getInstance();
		ContentValues cv = this.formatContentValues();
		dbHelper.update(DBHelper.TABLE.ALERTS, cv, ClockModel.ID + " = "
				+ this.mId, null);
	}
}
