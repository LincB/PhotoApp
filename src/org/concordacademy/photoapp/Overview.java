package org.concordacademy.photoapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class Overview extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_overview);
		
		Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() + "AppIcon57x57.png");
		
		// Create the ImageView
	    ImageView image = new ImageView(this);

	    // Layout Parameters
	    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
	            FrameLayout.LayoutParams.MATCH_PARENT,
	            FrameLayout.LayoutParams.MATCH_PARENT);

	    image.setScaleType(ImageView.ScaleType.MATRIX);
	    //image.setImageBitmap(bitmap);
	    image.setImageResource(R.drawable.ic_launcher);
	    //image.setOnTouchListener((OnTouchListener) this);    

	    // Get the root layout and add our ImageView
	    FrameLayout layout = (FrameLayout) findViewById(R.id.FrameLayout1);
	    layout.addView(image, 0, params);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.overview, menu);
		return true;
	}

}
