package com.xue.yynote.view;


import com.xue.yynote.R;
import com.xue.yynote.model.NoteItemModel;
import com.xue.yynote.model.ClockModel;
import com.xue.yynote.tools.DBHelper;
import com.xue.yynote.view.NotesListView;
import com.xue.yynote.MainActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;
import java.util.Date;
import java.text.SimpleDateFormat; 

public class NoteItemView extends LinearLayout {
	private NoteItemModel mItemModel;
	private TextView mContent;
	private ImageView mClock;
	private TextView mModifyTime;
	private Button mDelete;
	private MainView mMainView;
	private Button cancelButton;
	
	public NoteItemView(Context context){
		super(context);
		inflate(context, R.layout.activity_note_item, this);
		this.initResource();
	}
	
	public void initResource(){
		this.mContent = (TextView)findViewById(R.id.note_item_content);
		this.mClock = (ImageView)findViewById(R.id.note_item_clock);
		this.mModifyTime = (TextView)findViewById(R.id.note_item_modify_time);
		this.mDelete = (Button)findViewById(R.id.note_item_delete);		
		this.mMainView = ((MainActivity)this.getContext()).getMainView();
	}
	public void setModel(NoteItemModel itemModel){
		this.mItemModel = itemModel;
		this.mContent.setText(this.mItemModel.getContentForItem());
		this.setClockTv();
		this.mModifyTime.setText(this.getFormatModifyTime(itemModel.getModifyDate()));
		
		this.mDelete.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder dialog = new AlertDialog.Builder(NoteItemView.this.getContext());
				
				dialog.setMessage("确定要删除")
				.setTitle("删除提示")
				.setPositiveButton("确定", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {  
						NoteItemView.this.mDelete.setVisibility(View.GONE);
						DBHelper dbHelper = DBHelper.getInstance(NoteItemView.this.getContext());
						dbHelper.delete(DBHelper.TABLE.ALERTS, 
								ClockModel.ID + " = " + NoteItemView.this.mItemModel.getClockId(), null);
						dbHelper.delete(DBHelper.TABLE.NOTES, 
								NoteItemModel.ID + " = " + NoteItemView.this.mItemModel.getId(), null);
						NotesListView mListView = (NotesListView) NoteItemView.this.getParent();
						mListView.setStatus(NotesListView.STATUS_DELETE);
						mListView.refreshAdapter(NoteItemView.this.mItemModel.getId());
						
						hideDeleteCancelButton();
					}
				})
				.setNegativeButton("取消",new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {  
						hideDeleteButton();
						hideDeleteCancelButton();
					}
				});
				dialog.create().show();
			}			
		});
	}
	@SuppressWarnings("deprecation")
	private CharSequence getFormatModifyTime(long time){
		SimpleDateFormat formatter1 = new SimpleDateFormat ("yyyy-MM-dd");
		SimpleDateFormat formatter2 = new SimpleDateFormat ("MM-dd");
		Date dateNow = new Date();
		Date dateModify = new Date(time);
		String format = "";
		if((dateNow.getYear() == dateModify.getYear()) && (dateNow.getMonth() == dateModify.getMonth()) && (dateNow.getDate() == dateModify.getDate())){
			format = "" + "今天 " + dateModify.getHours() + ":" + dateModify.getMinutes();
		}		
		else{
			format = formatter2.format(dateModify);
		}
		return format;
	}
	//private void setContent(String content) {
		// TODO Auto-generated method stub
	//	int index = 0;
	//	while(content.indexOf("[0x64", index) >= 0){
	//		index = content.indexOf("[0x64");
	//		int end = content.indexOf("]", index+1);
	//		content = content.replace(content.substring(index, end+1), "[Pic]");
	//		index += 1;
	//	}
	//	this.mContent.setText(content);
	//}
	public int getModelId() {
		// TODO Auto-generated method stub
		return this.mItemModel.getId();
	}
	
	public NoteItemModel getItemModel(){
		return this.mItemModel;
	}
	public void setSequence(int index){
		this.mItemModel.setSequence(index);
	}	
	public int getSequence(){
		return this.mItemModel.getSequence();
	}
	public void updateSequence(DBHelper dbHelper){
		ContentValues cv = new ContentValues();
		cv.put(NoteItemModel.SEQUENCE, this.mItemModel.getSequence());
		dbHelper.update(DBHelper.TABLE.NOTES, cv, 
				NoteItemModel.ID + " = " + NoteItemView.this.mItemModel.getId(), 
				null);
	}
	
	public void setClockTv(){
		if(this.mItemModel.isClock()){
			this.mClock.setVisibility(View.VISIBLE);
		}else {
			this.mClock.setVisibility(View.GONE);
		}
	}
	public void showDeleteButton() {
		// TODO Auto-generated method stub
		this.mClock.setVisibility(View.GONE);
		this.mModifyTime.setVisibility(View.GONE);
		this.mDelete.setVisibility(View.VISIBLE);
	}
	public void hideDeleteButton(){
		// TODO Auto-generated method stub
		this.mDelete.setVisibility(View.GONE);
		this.setClockTv();
		this.mModifyTime.setVisibility(View.VISIBLE);
	}
	public void hideDeleteCancelButton(){
		this.mMainView.hideDeleteCancelButton();
	}
}
