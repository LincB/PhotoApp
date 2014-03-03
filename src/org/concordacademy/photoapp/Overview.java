package org.concordacademy.photoapp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.GridLayout;
import android.widget.ImageView;

public class Overview extends Activity {

	public String TAG = "Overview";
	ArrayList<String> picturePaths;
	int maxColumns = 5;
	int row = 0;
	int column = 0;

	// onCreate is used as refreshing the screen and images later in the program
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_overview);
		
		// Refresh the images
		refreshImages();
	}
	
	// Refresh the images that are being displayed (re-read them from the file)
	public void refreshImages() {
		// Open the file
		File file = new File(getFilesDir().getAbsoluteFile() + "/photos.txt");
		Log.i(TAG, file.getAbsolutePath());
		// If there is no file, create a new one
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			// Open an inputStream for reading the file
			FileInputStream inStream = openFileInput("photos.txt");
			BufferedReader readFile = new BufferedReader(new InputStreamReader(inStream));
			picturePaths = new ArrayList<String>();
			String receiveString = "";
			// Read in each line
			while ((receiveString = readFile.readLine()) != null) {
				// Add the line to picturePaths and add an Image with the path specified
				picturePaths.add(receiveString);
				Log.i("picture", receiveString);
				addImage(receiveString);
				receiveString = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Send an intent to the gallery app
	public void sendIntentToGallery() {
		Intent i = new Intent(
				Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		// Send the intent with id 1
		startActivityForResult(i, 1);
	}

	// Check if something on the menu was pressed
	public boolean onOptionsItemSelected(MenuItem item) {
		// If the gallery button was pressed, send an intent to the gallery
		if (item.getItemId() == R.id.gallery) {
			sendIntentToGallery();
		// If the camera button was pressed, send an intent to the camera
		} else if (item.getItemId() == R.id.camera) {
			sendIntentToCamera();
		// If the delete button was pressed, delete the file containing image paths
		} else if(item.getItemId() == R.id.delete) {
			File file = new File(getFilesDir().getAbsoluteFile() + "/photos.txt");
			file.delete();
			
			// Refresh the images by using onCreate (needed to refresh screen)
			onCreate(null);
		}

		return true;
	}

	// Send an intent to the camera
	public void sendIntentToCamera() {
		Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		
		// Send the intent with id 2
		startActivityForResult(i, 2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.overview, menu);
		return true;
	}

	// Called when the user has returned to the photoapp from another app (gallery or camera)
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// If there is data
		if (resultCode == RESULT_OK && null != data) {
			// If the intent we are coming back from is the gallery...
			if (requestCode == 1) {
				// Get the image that the gallery app says was selected by the user
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();

				try {
					// Write the image's path to the file so that it will be saved
					BufferedOutputStream toFile = new BufferedOutputStream(openFileOutput("photos.txt", MODE_APPEND));
					toFile.write(picturePath.getBytes());
					toFile.write('\n');
					toFile.flush();
					toFile.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				// Add the image to the screen
				addImage(picturePath);
			// If the app that we are coming from is the camera...
			} else if (requestCode == 2) {
				// Get the image the camera took from the data in the intent
				Bitmap photo = (Bitmap) data.getExtras().get("data");
				File photoFolder = new File(Environment.getExternalStorageDirectory(), "/");
				photoFolder.mkdir();
				Log.i(TAG, photoFolder.getAbsolutePath());
				FileOutputStream out = null;
				Calendar c = Calendar.getInstance();
				String date = String.valueOf(c.get(Calendar.MONTH))
						+ String.valueOf(c.get(Calendar.DAY_OF_MONTH))
						+ String.valueOf(c.get(Calendar.YEAR))
						+ String.valueOf(c.get(Calendar.HOUR_OF_DAY))
						+ String.valueOf(c.get(Calendar.MINUTE))
						+ String.valueOf(c.get(Calendar.SECOND));
				File photoFileName = new File(photoFolder, date.toString() + ".jpg");
				try
				{
					// Write the bitmap to a file
					photoFileName.createNewFile();
					out = new FileOutputStream(photoFileName);
					photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
					out.flush();
					out.close();
					out = null;
					MediaStore.Images.Media.insertImage(getContentResolver(), photo, photoFileName.toString() , "");
					
					// Write the image's path to the file to be saved
					BufferedOutputStream toFile = new BufferedOutputStream(openFileOutput("photos.txt", MODE_APPEND));
					toFile.write(photoFileName.getAbsolutePath().getBytes());
					toFile.write('\n');
					toFile.flush();
					toFile.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				// Add the image to the screen
				addImage(photo, photoFileName.getAbsolutePath());
				Log.d("PATH", photoFileName.getAbsolutePath());
			}
		}
	}

	// Add an image with a string as an argument (overloaded with other addImage)
	public void addImage(String fileName) {
		addImage(BitmapFactory.decodeFile(fileName), fileName);
	}

	// Add an image with a bitmap as the argument
	public void addImage(Bitmap b, final String path) {
		// Find the gridlayout
		LayoutInflater inflater = LayoutInflater.from(this);
		GridLayout container = (GridLayout) findViewById(R.id.GridLayout1);
		// Create a new imageView
		ImageView imageView = (ImageView) inflater.inflate(R.layout.photo_frame,null);
		// Set the imageView's picture
		imageView.setImageBitmap(b);

		// Set the onClick listener
		// This will make the image that was clicked larger
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onImageClick(arg0);
			}
		});
		
		// Set the onLongClick listener
		// This will delete the image that was "longClicked"
		imageView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Log.wtf("THING", "Clicked");
				
				// Create an ArrayList to contain everything in the text file
				ArrayList<String> textFileContent = new ArrayList<String>();
				
				try {
					// Prepare to read from the file
					FileInputStream inStream = openFileInput("photos.txt");
					
					BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));

					String currentLine;
					String lineToRemove = path;

					// Iterate through the lines of the file
					while ((currentLine = reader.readLine()) != null) {
						
						// Add each line to the ArrayList
						textFileContent.add(currentLine);
						
						Log.i("LINE TO REMOVE", lineToRemove);
						Log.i("CURRENT LINE", currentLine);
						if (currentLine.trim().equals(lineToRemove)) {
							// If the currentLine is the line we need to remove, remove it from the ArrayList
							textFileContent.remove(currentLine);
						}
						
					}
					
					// Delete the old file (needs to be re written)
					File file = new File(getFilesDir().getAbsoluteFile() + "/photos.txt");
					file.delete();
					
					// Create the file again (this time it is empty)
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					// Prepare to write the new file
					FileOutputStream outStream = openFileOutput("photos.txt", MODE_APPEND);
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
					
					// Iterate through textFileContent
					for (int c = 0; c < textFileContent.size(); c++) {
						// Write all of textFileContent to the new file (everything that was in the old 
						// file except what needed to be deleted
						writer.write(textFileContent.get(c) + "\n");
						Log.d("WROTE LINE", textFileContent.get(c));
					}
					
					// Close the streams
					writer.close();
					reader.close();
				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Refresh the display
				onCreate(null);
				return false;
			}
		});
		
		// Set the image to be visible
		imageView.setVisibility(0);
		// Add all the layout parameters (this is what the XML does but in java this time)
		GridLayout.LayoutParams layout = new GridLayout.LayoutParams();
		layout.setGravity(Gravity.LEFT | Gravity.TOP);
		layout.width = 150;
		layout.height = 115;
		layout.setMargins(5, 5, 5, 5);
		layout.columnSpec = GridLayout.spec(column);
		layout.rowSpec = GridLayout.spec(row);
		if (column >= maxColumns - 1) {
			column = 0;
			row++;
		} else{
			column++;
		}
		container.addView(imageView,layout);
	}

	// If the image is clicked
	public void onImageClick(View v) {
		// Send an intent to PhotoActivity
		Intent intent = new Intent(getBaseContext(), PhotoActivity.class);
		
		// Get the bitmap of the image that was clicked and send that as an extra
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
