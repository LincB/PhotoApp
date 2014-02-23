package org.concordacademy.photoapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

public class PhotoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			Log.wtf("NULL", "NULL");
		}
		byte[] b = extras.getByteArray("image");

		Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
		
		ImageView img = (ImageView) findViewById(R.id.imageView1);
		img.setImageBitmap(bmp);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.photo, menu);
		return true;
	}
	
	

}
