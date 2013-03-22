package com.xue.yynote;

import java.io.File;
import java.io.IOException;

import com.xue.yynote.view.MainView;
import com.xue.yynote.tools.DBHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

	private MainView mMainView;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.mMainView = new MainView(this);
		this.setContentView(this.mMainView);
		
	}
	protected void onResume(){
		super.onResume();
	}
	//获得activity传来的noteId，刷新列表
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 super.onActivityResult(requestCode, resultCode, data);
		 if (resultCode == RESULT_OK) { 
			 Bundle bundle = data.getExtras();
			 this.mMainView.refreshAdapter(bundle.getInt("NOTE_ID"));
		 }
	}
 
	protected void onPause(){
		super.onPause();
		this.mMainView.saveSequence();
	}
	
	@Override
	protected void onStop()
	{
	    super.onStop();
	    this.mMainView.saveSequence();
		DBHelper.getInstance(this).close();
	}
	
	@Override
	protected void onDestroy()
	{
	    super.onDestroy();
	    this.mMainView.saveSequence();
	    DBHelper.getInstance(this).close();
	}
	public void onBackPressed(){
		if(mMainView.getCancelBtnVisible()==0){
			mMainView.clearDeleteButton();
			mMainView.hideDeleteCancelButton();
		}
		else
			super.onBackPressed();
	}
}