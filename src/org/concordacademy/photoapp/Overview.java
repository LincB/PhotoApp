package org.concordacademy.photoapp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridLayout;
import android.widget.ImageView;

public class Overview extends Activity {

	public String TAG = "Overview";
	private int numImages = 0;
	ArrayList<String> picturePaths;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_overview);
		File file = new File(getFilesDir().getAbsoluteFile() + "/photos.txt");
		Log.i(TAG, file.getAbsolutePath());
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
				
		try {
			BufferedReader readFile = new BufferedReader(new InputStreamReader(openFileInput("photos.txt")));
			picturePaths = new ArrayList<String>();
			String receiveString = "";
			while ((receiveString = readFile.readLine()) != null) {
				picturePaths.add(receiveString);
				Log.wtf("picture", receiveString);
				addImage(receiveString);
				receiveString = "";
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendIntentToGallery() {
		Intent i = new Intent(
				Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		startActivityForResult(i, 1);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.gallery) {
			sendIntentToGallery();
		} else if (item.getItemId() == R.id.camera) {
			sendIntentToCamera();
		} else if(item.getItemId() == R.id.delete) {
			File file = new File(getFilesDir().getAbsoluteFile() + "/photos.txt");
			file.delete();
			onCreate(null);
		}

		return true;
	}

	public void sendIntentToCamera() {
		Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

		startActivityForResult(i, 2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.overview, menu);
		return true;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK && null != data) {
			if (requestCode == 1) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
	
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
	
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();
	
				try {
					BufferedOutputStream toFile = new BufferedOutputStream(openFileOutput("photos.txt", MODE_APPEND));
					toFile.write(picturePath.getBytes());
					toFile.write('\n');
					toFile.flush();
					toFile.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
	
				addImage(picturePath);
			} else if (requestCode == 2) {
				//Uri selectedImage = data.getData();
				
				Bitmap photo = (Bitmap) data.getExtras().get("data");
				
				addImage(photo);
				
				/*String[] filePathColumn = {MediaStore.Images.Media.DATA};

				Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				//file path of captured image
				String filePath = cursor.getString(columnIndex); 
				Log.i(TAG, filePath);
				cursor.close();*/
			}
			
		}

	}
	
	public void addImage(String fileName) {
		
		LayoutInflater inflater = LayoutInflater.from(this);
		GridLayout container = (GridLayout) findViewById(R.id.GridLayout1);
		ImageView imageView = (ImageView) inflater.inflate(R.layout.photo_frame,null);
		imageView.setImageBitmap(BitmapFactory.decodeFile(fileName));
					
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onImageClick(arg0);
			}
        });
		
		if (numImages > 5) {
			
		}
		
		imageView.setVisibility(0);
		GridLayout.LayoutParams layout = new GridLayout.LayoutParams();
		layout.setGravity(Gravity.LEFT | Gravity.TOP);
		layout.width = 150;
		layout.height = 115;
		layout.setMargins(5, 5, 5, 5);
		layout.columnSpec = GridLayout.spec(0);
		container.addView(imageView,layout);
		
		numImages += 1;
	}
	
	public void addImage(Bitmap b) {
		LayoutInflater inflater = LayoutInflater.from(this);
		GridLayout container = (GridLayout) findViewById(R.id.GridLayout1);
		ImageView imageView = (ImageView) inflater.inflate(R.layout.photo_frame,null);
		imageView.setImageBitmap(b);
					
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onImageClick(arg0);
			}
        });
		
		if (numImages > 5) {
			
		}
		
		imageView.setVisibility(0);
		GridLayout.LayoutParams layout = new GridLayout.LayoutParams();
		layout.setGravity(Gravity.LEFT | Gravity.TOP);
		layout.width = 150;
		layout.height = 115;
		layout.setMargins(5, 5, 5, 5);
		layout.columnSpec = GridLayout.spec(0);
		container.addView(imageView,layout);
		
		numImages += 1;
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
