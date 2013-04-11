package com.xue.yynote.tools;

import com.xue.yynote.model.ClockModel;
import com.xue.yynote.model.NoteItemModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	public static final String TAG = "DbHelperModel";
	public static final String NOTE_DB_NAME = "notes.db";
	public static final String ALERT_DB_NAME = "alerts.db";

	private static final int DB_VERSION = 4;

	public interface TABLE {
		public static final String NOTES = "notes";
		public static final String ALERTS = "alerts";
	}

	private static final String CREATE_NOTES_TABLE_SQL = "CREATE TABLE "
			+ TABLE.NOTES
			+ " ("
			+ NoteItemModel.ID
			+ " INTEGER PRIMARY KEY,"
			+
			// NoteItemModel.BG_COLOR_ID + " INTEGER NOT NULL DEFAULT 0," +
			NoteItemModel.CLOCK_ID + " INTEGER NOT NULL DEFAULT 0,"
			+ NoteItemModel.CREATE_DATE + " INTEGER NOT NULL DEFAULT 0,"
			+ NoteItemModel.MODIFY_DATE + " INTEGER NOT NULL DEFAULT 0,"
			+ NoteItemModel.CONTENT + " TEXT　NOT NULL DEFAULT '',"
			+ NoteItemModel.AUDIO + " TEXT　NOT NULL DEFAULT '',"
			+ NoteItemModel.SEQUENCE + " INTEGER NOT NULL DEFAULT 0" + ")";

	private static final String CREATE_ALERTS_TABLE_SQL = "CREATE TABLE "
			+ TABLE.ALERTS + " (" + ClockModel.ID + " INTEGER PRIMARY KEY,"
			+ ClockModel.TIME + " INTEGER NOT NULL DEFAULT 0,"
			+ ClockModel.ALERT_INTERVAL + " INTEGER　NOT NULL DEFAULT 0,"
			+ ClockModel.ALERT_TIMES + " INTEGER　NOT NULL DEFAULT 0,"
			+ ClockModel.ALERT_COUNT + " INTEGER　NOT NULL DEFAULT 0" + ")";

	public static DBHelper instance;

	private SQLiteDatabase db;

	public DBHelper(Context context) {
		super(context, NOTE_DB_NAME, null, DB_VERSION);
		this.db = this.getWritableDatabase();
	}

	public static DBHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DBHelper(context);
		}
		return instance;
	}

	public static DBHelper getInstance() {
		return instance;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	private void initTables(SQLiteDatabase db) {
		db.execSQL(CREATE_NOTES_TABLE_SQL);
		db.execSQL(CREATE_ALERTS_TABLE_SQL);
		Log.d(TAG, CREATE_ALERTS_TABLE_SQL);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		initTables(db);
	}

	public void runSql(String sql) {
		db = getWritableDatabase();
		db.execSQL(sql);
	}

	public long insert(String tableName, ContentValues values) {
		if (this.db.isOpen() && this.db.isReadOnly()) {
			db.close();
			db = this.getWritableDatabase();
		}
		long id = db.insert(tableName, null, values);
		return id;
	}

	public Cursor query(String tableName, String[] columns, String selection,
			String[] selectionArgs, String orderBy) {
		if (this.db.isOpen() && !this.db.isReadOnly()) {
			db.close();
			db = this.getReadableDatabase();
		}
		Cursor cursor = db.query(tableName, columns, selection, selectionArgs,
				null, null, orderBy);
		return cursor;
	}

	public boolean update(String tableName, ContentValues values,
			String whereClause, String[] whereArgs) {
		if (this.db.isOpen() && this.db.isReadOnly())
			db = this.getWritableDatabase();
		int rows = db.update(tableName, values, whereClause, whereArgs);
		if (rows < 0)
			return false;
		return true;
	}

	public boolean delete(String tableName, String whereClause,
			String[] whereArgs) {
		if (this.db.isOpen() && this.db.isReadOnly())
			db = this.getWritableDatabase();
		int rows = db.delete(tableName, whereClause, whereArgs);
		if (rows < 1)
			return false;
		return true;
	}

	public Cursor getAll() {
		return null;
	}

	public void close() {
		if (DBHelper.instance != null) {
			this.db.close();
			this.db = null;
			DBHelper.instance = null;
		}
	}
}
