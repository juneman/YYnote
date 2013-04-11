package com.xue.yynote.view;

import com.xue.yynote.R;
import com.xue.yynote.activity.NoteEditActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainView extends LinearLayout {
	public static final String TAG = "MainView";
	private NotesListView mListView;
	private Button mNewButton;
	private Button mDeleteCancelButton;

	public MainView(Context context) {
		super(context);
		inflate(context, R.layout.activity_main, this);
		this.initResource();
	}

	private void initResource() {
		this.mListView = (NotesListView) this.findViewById(R.id.notes_list);
		this.mListView.setMainView(this);
		this.mListView.initAdapter();

		this.mNewButton = (Button) this.findViewById(R.id.new_note_btn);
		this.mNewButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainView.this.getContext(),
						NoteEditActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("ID", -1);
				intent.putExtras(bundle);
				((Activity) MainView.this.getContext()).startActivityForResult(
						intent, 1);
				MainView.this.mListView.setStatus(NotesListView.STATUS_CREATE);
			}

		});
		this.mDeleteCancelButton = (Button) this.findViewById(R.id.cancle_btn);
		this.mDeleteCancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainView.this.mListView.clearDeleteButton();
				MainView.this.hideDeleteCancelButton();
			}

		});
	}

	public int getCancelBtnVisible() {
		return this.mDeleteCancelButton.getVisibility();
	}

	public void clearDeleteButton() {
		MainView.this.mListView.clearDeleteButton();
	}

	public void showDeleteCancelButton() {
		this.mNewButton.setVisibility(View.GONE);
		this.mDeleteCancelButton.setVisibility(View.VISIBLE);
	}

	public void hideDeleteCancelButton() {
		this.mDeleteCancelButton.setVisibility(View.GONE);
		this.mNewButton.setVisibility(View.VISIBLE);
	}

	public void saveSequence() {
		// TODO Auto-generated method stub
		this.mListView.saveSequence();
	}

	public void refreshAdapter(int noteId) {
		// TODO Auto-generated method stub
		this.mListView.refreshAdapter(noteId);
	}

	public void setStatus(int status) {
		// TODO Auto-generated method stub
		this.mListView.setStatus(status);
	}

}
