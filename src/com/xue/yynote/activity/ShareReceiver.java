package com.xue.yynote.activity;

import java.util.ArrayList;

import com.xue.yynote.R;
import com.xue.yynote.view.MainView;
import com.xue.yynote.view.NotesListView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class ShareReceiver extends Activity{
	private NotesListView mListView;
	
	protected void onCreate (Bundle savedInstanceState) {
		 Intent intent = getIntent();
		 String action = intent.getAction();
		 String type = intent.getType();
		 this.mListView = (NotesListView) this.findViewById(R.id.notes_list);
		 
		 if (Intent.ACTION_SEND.equals(action) && type != null) {
		        if ("text/plain".equals(type)) {
		            handleSendText(intent); 
		        } else if (type.startsWith("image/")) {
		            handleSendImage(intent); 
		        }
		    } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
		        if (type.startsWith("image/")) {
		            handleSendMultipleImages(intent); 
		        }
		    } else {
		       
		    }
		 
	}
	private void handleSendText(Intent intent) {
	    String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
	    if (sharedText != null) {
	    /*	Intent newEditIntent = new Intent(this, NoteEditActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt("ID", -1);
			newEditIntent.putExtras(bundle);
			startActivityForResult(newEditIntent, 1);  */
			 Log.d("sharetext", "text");
			//this.mListView.setStatus(NotesListView.STATUS_CREATE);
	    }
	    
	}
	private void handleSendImage(Intent intent) {
	    Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
	    if (imageUri != null) {
	        Log.d("shareimg", "IMAGE");
	    }
	}
	private void handleSendMultipleImages(Intent intent) {
	    ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
	    if (imageUris != null) {
	        // Update UI to reflect multiple images being shared
	    }
	}
}
