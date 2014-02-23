package org.concordacademy.photoapp;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridLayout;
import android.widget.ImageView;

public class Overview extends Activity {

	public String TAG = "Overview";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_overview);
		Intent i = new Intent(
				Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		startActivityForResult(i, 1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.overview, menu);
		return true;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
		if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			
			LayoutInflater inflater = LayoutInflater.from(this);
			
			GridLayout container = (GridLayout) findViewById(R.id.GridLayout1);
			ImageView imageView = (ImageView) inflater.inflate(R.layout.photo_frame,null);
			ImageView imageView2 = (ImageView) inflater.inflate(R.layout.photo_frame,null);
			imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
			
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					onImageClick(arg0);
				}
	        	
	        });
			
			imageView2.setImageBitmap(BitmapFactory.decodeFile(picturePath));
			imageView.setVisibility(0);
			imageView2.setVisibility(0);
			GridLayout.LayoutParams layout = new GridLayout.LayoutParams();
			layout.setGravity(Gravity.LEFT | Gravity.TOP);
			layout.width = 150;
			layout.height = 115;
			GridLayout.LayoutParams layout2 = new GridLayout.LayoutParams(layout);
			layout.rowSpec = GridLayout.spec(0);
			layout2.rowSpec = GridLayout.spec(1);
			container.addView(imageView,layout);
			container.addView(imageView2,layout2);
			Log.i(TAG, Float.toString(imageView2.getX()));
			Log.i(TAG, Float.toString(imageView2.getY()));
		}
    }
	
	public void onImageClick(View v) {
		Intent intent = new Intent(getBaseContext(), PhotoActivity.class);
		
		startActivity(intent);
		Bitmap bitmap = ((BitmapDrawable)((ImageView) v).getDrawable()).getBitmap();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); 
		byte[] b = baos.toByteArray();
		
		intent.putExtra("image", b);
		
		startActivity(intent);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
	    super.onNewIntent(intent);
	    setIntent(intent);
	}
}
