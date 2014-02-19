package org.concordacademy.photoapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.widget.GridLayout;
import android.widget.ImageView;

public class Overview extends Activity {

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
			imageView2.setImageBitmap(BitmapFactory.decodeFile(picturePath));
			imageView.setVisibility(0);
			imageView2.setVisibility(0);
			GridLayout.LayoutParams layout = new GridLayout.LayoutParams();
			layout.setGravity(Gravity.LEFT | Gravity.TOP);
			layout.width = 150;
			layout.height = 115;
			layout.rowSpec = GridLayout.spec(0);
			container.addView(imageView,layout);
			container.addView(imageView2,layout);
		
		}
    }
}
