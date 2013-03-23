package com.xue.yynote.activity;

import com.xue.yynote.R;
import com.xue.yynote.view.NoteEditView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap; 
import android.graphics.Matrix;
import android.graphics.BitmapFactory; 
import android.net.Uri;
import java.io.ByteArrayOutputStream;    
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.provider.MediaStore; 
import android.content.ContentResolver;
import android.text.style.ImageSpan;
import android.text.SpannableString;
import android.text.Spannable;
import android.text.Editable;

public class NoteEditActivity extends Activity{
	public static final String TAG = "NoteEditActivity";
	private NoteEditView mNoteEditView;
	private static final int PHOTO_SUCCESS = 1;  
	private static final int CAMERA_SUCCESS = 2; 
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.mNoteEditView = new NoteEditView(this);
		this.setContentView(mNoteEditView);
		Bundle bundle = getIntent().getExtras();
		int mNoteId = bundle.getInt("ID");
		if(mNoteId >= 0){
			this.mNoteEditView.setNoteEditModel(mNoteId);
		}else if(mNoteId == -1){
			this.mNoteEditView.createNoteEditModel();	
		}
		else if(mNoteId == -2){
			String mNoteContent = bundle.getString("CONTENT");
			Bitmap mNoteBitmap = bundle.getParcelable("bitmap");
			if(mNoteContent != null){
				this.mNoteEditView.setContentText(mNoteContent);
			}
			else if(mNoteBitmap != null){
				this.mNoteEditView.createNoteEditModel();	//创建model			
				showBitmapImg(mNoteBitmap);
			}			
		}
	}	

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {  
	    ContentResolver resolver = getContentResolver();   
	    if (resultCode == RESULT_OK) {
	    	Bitmap bitmap = null; 
	    	Bitmap originalBitmap = null;
	    	if(requestCode == PHOTO_SUCCESS && intent != null){
	    		Uri originalUri = intent.getData();
	            try {   
	                originalBitmap = BitmapFactory.decodeStream(resolver.openInputStream(originalUri));  
	                 
	            } catch (FileNotFoundException e) {  
	                e.printStackTrace();  
	            }
	    	}else if(requestCode == CAMERA_SUCCESS && intent != null){
	    		Bundle extras = intent.getExtras();   
	    		originalBitmap = (Bitmap) extras.get("data");
	    		
	    	}
	    	if(originalBitmap != null){ 
	    			bitmap = resizeImage(originalBitmap, 200, 200);
	    			showBitmapImg(bitmap);	             
	            }else{  
	                Toast.makeText(NoteEditActivity.this, "获取图片失败", Toast.LENGTH_SHORT).show();  
	            }
	    }  
	}
	
	public void showBitmapImg(Bitmap bitmap){
		String filePath = this.savePicture(bitmap);
		//根据Bitmap对象创建ImageSpan对象  
        ImageSpan imageSpan = new ImageSpan(NoteEditActivity.this, bitmap);  
        //创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像  
        SpannableString spannableString = new SpannableString("[0x64" + filePath + "]");  
        //  用ImageSpan对象替换face  
        spannableString.setSpan(imageSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
        //将选择的图片追加到EditText中光标所在位置  
        int index = mNoteEditView.mContent.getSelectionStart(); //获取光标所在位置  
        Editable edit_text = mNoteEditView.mContent.getEditableText();  
        if(index <0 || index >= edit_text.length()){  
            edit_text.append(spannableString);  
        }else{  
            edit_text.insert(index, spannableString);  
        } 
	}
	private String savePicture(Bitmap bitmap) {
		// TODO Auto-generated method stub
		File root = this.getFilesDir();
		String name = "/pic_" + System.currentTimeMillis();
		try {
			File f = new File(root.getPath() + name);
			f.createNewFile();
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, new FileOutputStream(f.getPath()));
			return name;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
   
	public void onBackPressed(){
	/*	if(this.mNoteEditView.getContentLength() > 0){
    		this.mNoteEditView.finishEdit();
    		Bundle bundle = new Bundle(); 
    		bundle.putInt("NOTE_ID", this.mNoteEditView.getModelId()); 
    		NoteEditActivity.this.setResult(RESULT_OK, NoteEditActivity.this.getIntent().putExtras(bundle));
		}
    	Log.d(TAG, "note is saved succeed");
    	*/
    	super.onBackPressed();
	}
	
}
