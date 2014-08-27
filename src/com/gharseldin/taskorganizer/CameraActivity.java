package com.gharseldin.taskorganizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.gharseldin.taskorganizer.datastructures.SettingWrapper;
import com.gharseldin.taskorganizer.datastructures.UserRecord;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

public class CameraActivity extends Activity implements SurfaceHolder.Callback{

	private Camera mCamera;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private Bitmap bmp;
	private Calendar cal;
	private Date todayDate;
	private SimpleDateFormat sdf;
	private String imageName, todayStr;
	private String imageFileExtension = ".jpeg";
	String imagePath = "";
	private boolean isReleased = false;
	private UserRecord user;
	private SettingWrapper wrap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);

		user = (UserRecord) getIntent().getSerializableExtra(
				"com.gharseldin.taskorganize.user");
		wrap = (SettingWrapper) getIntent().getSerializableExtra(
				"com.gharseldin.taskorganize.settings");
			
		surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		surfaceView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				capture();
			}
		});
		
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(CameraActivity.this);
		
		try {
			mCamera = Camera.open();
			if (mCamera == null) {
				Toast.makeText(CameraActivity.this, "Unable to use Camera",
						Toast.LENGTH_LONG).show();
				super.onBackPressed();
			} else {
				mCamera.setPreviewDisplay(surfaceHolder);
				mCamera.setDisplayOrientation(90); // pictures are rotated -90
													// by default. This is to
													// counter this action
				mCamera.startPreview();
				isReleased = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.setDisplayOrientation(90);
			mCamera.startPreview();
			isReleased = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		try {
			if (mCamera != null && !isReleased) {
				mCamera.stopPreview();
				mCamera.release();
				isReleased = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/** Operation to be performed when picture is clicked */
	private void capture() {
		try {
			mCamera.takePicture(null, null, null, new Camera.PictureCallback() {

				@Override
				public void onPictureTaken(byte[] data, Camera camera) {
					bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
					
					new AddPicture().execute();

					camera.stopPreview();
					if (camera != null) {
						camera.release();
						mCamera = null;
						isReleased = true;
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//creating a dialog to tell the user that we are adding the image
	private ProgressDialog pDialog;
	private final int qualityOfImage = 50;
	
	class AddPicture extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(CameraActivity.this);
			pDialog.setMessage("Adding Photo, Please Wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... uri) {
			String responseString = null;

			// Getting current date & time and preparing image name
			cal = Calendar.getInstance();
			todayDate = cal.getTime();
			sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
			todayStr = sdf.format(todayDate).trim();
			imageName = todayStr;
			imageName = imageName.replace("/", "-");
			imageName = imageName.replace(":", "-");
			imageName = imageName.replace(" PM", "pm");
			imageName = imageName.replace(" AM", "am");
			imageName = imageName.replace(" ", "_");

			OutputStream os = null;
			try {
				File root = new File(Environment.getExternalStorageDirectory()
						+ File.separator + "Clipping" + File.separator
						+ "Clipping_" + File.separator);
				if (!root.exists()) {
					root.mkdirs();
				}
				imageName = imageName + imageFileExtension;

				String pathis = root.getPath() + imageName;
				os = new FileOutputStream(pathis);
				bmp.compress(CompressFormat.JPEG, qualityOfImage, os);
			} catch (IOException e) {
			}
			imagePath = Environment.getExternalStorageDirectory()
					+ File.separator + "Clipping" + File.separator
					+ "Clipping_" + imageName;

			return responseString;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				if (pDialog != null && pDialog.isShowing()) {
					pDialog.dismiss();
				}
				//TODO
				//create an intent and pass the variables to the tasklistFragment
				Log.d("Status",user.getFirstName()+" "+(wrap.getProfile()).size()+" "+ imagePath);
				Intent intent = new Intent(CameraActivity.this,
						MainActivity.class);
				intent.putExtra("com.gharseldin.taskorganize.user", user);
				intent.putExtra("com.gharseldin.taskorganize.settings", wrap);
				intent.putExtra("com.gharseldin.taskorganize.imagePath", imagePath);
				intent.putExtra("type", "Camera");
				setResult(Activity.RESULT_OK, intent);
				CameraActivity.this.finish();
			} catch (Exception e) {
				// Do Nothing
			}
		}
	}

}
