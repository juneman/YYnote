package com.xue.yynote.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.xue.yynote.model.NoteItemModel;
import com.xue.yynote.tools.DBHelper;
import com.xue.yynote.view.NoteItemView;
import com.xue.yynote.R;
import com.xue.yynote.view.NoteListAdapter;
import com.xue.yynote.activity.NoteEditActivity;
import com.xue.yynote.view.MainView;

public class NotesListView extends ListView{
	public static final String TAG = "NotesListView";
	public static final int STATUS_NORMAL = 0;
	public static final int STATUS_CREATE = 1;
	public static final int STATUS_EDIT = 2;
	public static final int STATUS_DELETE = 3;
	
	private NoteItemView mCurItem;
	private ArrayList<NoteItemModel> mNotesList;
	private NoteListAdapter mAdapter;
	private MainView mMainView;
	private int status;

	public NotesListView(Context context, AttributeSet attrs) {
	    super(context, attrs);
		// TODO Auto-generated constructor stub
	    this.mCurItem = null;
	    this.status = 0;
	}
	public void setMainView(MainView mainView){
		this.mMainView = mainView;
	}
	public void initAdapter(){
		NoteItemModel mNoteItem;
		this.mNotesList = new ArrayList<NoteItemModel>();
		DBHelper dbHelper = DBHelper.getInstance(this.getContext());
		Cursor cursor = dbHelper.query(DBHelper.TABLE.NOTES, NoteItemModel.mColumns, null, null, NoteItemModel.MODIFY_DATE + " desc");
		while(cursor.moveToNext()){
			mNoteItem = new NoteItemModel(this.getContext(), cursor);
			this.mNotesList.add(mNoteItem);
		}
		this.mAdapter = new NoteListAdapter(this.getContext(), R.layout.activity_note_item,
				this.mNotesList);
		Log.d(TAG, "set adapter");
		this.setAdapter(this.mAdapter);
	
		this.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				NotesListView.this.status = STATUS_EDIT;
				NoteItemView item = (NoteItemView) view;
				NotesListView.this.mCurItem = item;
				Intent intent = new Intent(NotesListView.this.getContext(), NoteEditActivity.class);
				intent.putExtra("ID", item.getModelId());
				((Activity) NotesListView.this.getContext()).startActivityForResult(intent,1);
			}		
		});
		
		this.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				NoteItemView item = (NoteItemView) view;
				NotesListView.this.mCurItem = item;
				item.showDeleteButton();
				NotesListView.this.mMainView.showDeleteCancelButton();
				NotesListView.this.status = STATUS_DELETE;
				return false;
			}
			
		});
	}
		
	public void saveSequence() {
		// TODO Auto-generated method stub
		if (this.mAdapter != null){
			DBHelper dbHelper = DBHelper.getInstance(this.getContext());
	    	for (int i = 0; i < this.mAdapter.getCount(); i++)
	    	{
	    		NoteItemView mItemView = (NoteItemView)this.getChildAt(i);
	    		if (mItemView != null && mItemView.getSequence() != i)
	    		{
	    			mItemView.setSequence(i);
	    			mItemView.updateSequence(dbHelper);
	    		}
	    	}
	    	dbHelper.close();
	    }
	}
	
	public void clearDeleteButton(){
		if(this.status == STATUS_DELETE){
			this.mCurItem.hideDeleteButton();
			this.status = STATUS_NORMAL;
		}
	}
	
	public void refreshAdapter(int id) {
		if(status == STATUS_CREATE){
			NoteItemModel mItemModel = new NoteItemModel(this.getContext(), id);
			this.mAdapter.insert(mItemModel, 0);
		}
		if(status == STATUS_EDIT && this.mCurItem != null){
			//int modelId  = this.mCurItem.getItemModel().getId();
			int modelId  = this.mCurItem.getModelId();
			int modelSequence = this.mCurItem.getItemModel().getSequence();
			NoteItemModel mItemModel = new NoteItemModel(this.getContext(), modelId);
			this.mAdapter.remove(this.mCurItem.getItemModel());
			this.mAdapter.insert(mItemModel, modelSequence);
		} else if(status == STATUS_DELETE){
			this.mAdapter.remove(this.mCurItem.getItemModel());
		}
	}
	public void setStatus(int _status) {
		// TODO Auto-generated method stub
		this.status = _status;
	}
	
	public void setCurItem(NoteItemView item){
		this.mCurItem = item;
	}
	
}

class NoteListAdapter extends ArrayAdapter<NoteItemModel> {
	public static final String TAG = "NoteListAdapter";
	
	public NoteListAdapter(Context context, int resource, List<NoteItemModel> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		NoteItemView mItemView;
		if (convertView != null) {
			mItemView = (NoteItemView)convertView;
		}
		else mItemView  = new NoteItemView(this.getContext());
		NoteItemModel mItemModel = getItem(position);
		mItemView.setModel(mItemModel);
		Log.d(TAG, "get view " + position + " id: " + mItemModel.getId());
        return mItemView;
	}
}
