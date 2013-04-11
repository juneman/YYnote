package com.xue.yynote.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class ShareReceiver extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

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
			Intent newEditIntent = new Intent(this, NoteEditActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt("ID", -2);
			bundle.putString("CONTENT", sharedText);
			newEditIntent.putExtras(bundle);
			startActivity(newEditIntent);
		}
	}

	private void handleSendImage(Intent intent) {
		Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
		if (imageUri != null) {
			// Bitmap originalBitmap =
			// MediaStore.Images.Media.getBitmap(this.getContentResolver(),
			// imageUri);
			intent.setClass(this, NoteEditActivity.class);
			// bitmap = resizeImage(originalBitmap,200,200);
			// //压缩bitmap，在两个activity之间传递的图片大小不能超过40k
			intent.putExtra("ID", -2);
			intent.putExtra("CONTENT", intent.getStringExtra(Intent.EXTRA_TEXT));
			startActivity(intent);
		}

	}

	private void handleSendMultipleImages(Intent intent) {
		ArrayList<Uri> imageUris = intent
				.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
		if (imageUris != null) {
			// Update UI to reflect multiple images being shared
			// Bitmap originalBitmap =
			// MediaStore.Images.Media.getBitmap(this.getContentResolver(),
			// imageUri);
			intent.setClass(this, NoteEditActivity.class);
			// bitmap = resizeImage(originalBitmap,200,200);
			// //压缩bitmap，在两个activity之间传递的图片大小不能超过40k
			intent.putExtra("ID", -3);
			intent.putExtra("CONTENT", intent.getStringExtra(Intent.EXTRA_TEXT));
			startActivity(intent);
		}
	}

	protected void onRestart() {
		super.onRestart();
		this.finish();
	}
}
