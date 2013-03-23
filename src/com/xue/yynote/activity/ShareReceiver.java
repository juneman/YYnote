package com.xue.yynote.activity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.xue.yynote.R;
import com.xue.yynote.view.MainView;
import com.xue.yynote.view.NotesListView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

public class ShareReceiver extends Activity{
	private NotesListView mListView;
	
	protected void onCreate (Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 Intent intent = getIntent();
		 String action = intent.getAction();
		 String type = intent.getType();
		 this.mListView = (NotesListView) this.findViewById(R.id.notes_list);
		 
		 if (Intent.ACTION_SEND.equals(action) && type != null) {
		        if ("text/plain".equals(type)) {
		            handleSendText(intent); 
		        } else if (type.startsWith("image/")) {
		            try {
						handleSendImage(intent);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
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
	    	Intent newEditIntent = new Intent(this, NoteEditActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt("ID", -2);
			bundle.putString("CONTENT",sharedText);			
			newEditIntent.putExtras(bundle);
			startActivityForResult(newEditIntent, 1);  			
	    }    
	}
	private void handleSendImage(Intent intent) throws FileNotFoundException, IOException {
	    Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
	    if (imageUri != null) {	   
	        Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
	        Bitmap bitmap = null;
	    	if(originalBitmap != null){
	    		bitmap = resizeImage(originalBitmap,200,200);  //压缩bitmap，在两个activity之间传递的图片大小不能超过40k
	    		Intent newEditIntent = new Intent(this, NoteEditActivity.class);
	 			Bundle bundle = new Bundle();
	 			bundle.putInt("ID", -2);
	 			bundle.putParcelable("bitmap",bitmap);
	 			
	 			newEditIntent.putExtras(bundle);
	 			startActivityForResult(newEditIntent, 1);  
	    	}
			
	    }
	}
	private void handleSendMultipleImages(Intent intent) {
	    ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
	    if (imageUris != null) {
	        // Update UI to reflect multiple images being shared
	    }
	}
	private Bitmap resizeImage(Bitmap originalBitmap, int newWidth, int newHeight){  
	    int width = originalBitmap.getWidth();  
	    int height = originalBitmap.getHeight();    
	   
	    float scanleWidth = (float)newWidth/width;  
	    float scanleHeight = (float)newHeight/height;  
	    //创建操作图片用的matrix对象 Matrix  
	    Matrix matrix = new Matrix();  
	    // 缩放图片动作  
	    matrix.postScale(scanleWidth,scanleHeight);  
  
	    // 创建新的图片Bitmap  
	    Bitmap resizedBitmap = Bitmap.createBitmap(originalBitmap,0,0,width,height,matrix,true);  
	    return resizedBitmap;  
	} 
	protected void onRestart(){
		super.onRestart();
		this.finish();
	}
}
