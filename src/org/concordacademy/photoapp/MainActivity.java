package org.concordacademy.photoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
	
	private String tag;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tag = this.getLocalClassName();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void enterApp(View v) {
		// Send an intent to Overview if the viewImages button was clicked
		Log.i(tag, "Starting overview screen");
		Intent intent = new Intent(this, Overview.class);
		startActivity(intent);
	}
}
