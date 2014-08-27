package com.gharseldin.taskorganizer;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gharseldin.taskorganizer.Fragments.DatePickerFragment;
import com.gharseldin.taskorganizer.database.TaskDatabaseManager;
import com.gharseldin.taskorganizer.datastructures.SettingWrapper;
import com.gharseldin.taskorganizer.datastructures.SettingsUnit;
import com.gharseldin.taskorganizer.datastructures.TaskRecord;
import com.gharseldin.taskorganizer.datastructures.UserRecord;

@SuppressLint("NewApi")
public class UpdateTaskActivity extends Activity implements OnClickListener {

	private EditText etContent;
	private RadioButton radioNormal;
	private RadioButton radioHigh;
	private ImageView picture;
	private CheckBox emailNotify;
	private CheckBox smsNotify;
	private TextView startDate, dueDate;

	private Button btnUpdate, btnDelete;
	private Calendar setCalendar;
	private Calendar dueCalendar;

	private String setString;
	private String dueString;

	private Context ctx;
	private TaskDatabaseManager dbM;
	private UserRecord user;
	private ArrayList<SettingsUnit> profile;
	private TaskRecord task;

	private int dueMonth = 0;
	private int dueDay = 0;
	private int dueYear = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_updatetask);

		// Initialize the user fields
		user = (UserRecord) getIntent().getSerializableExtra(
				"com.gharseldin.taskorganize.user");
		SettingWrapper wrap = (SettingWrapper) getIntent()
				.getSerializableExtra("com.gharseldin.taskorganize.settings");
		task = (TaskRecord) getIntent().getSerializableExtra(
				"com.gharseldin.taskorganize.task");
		profile = wrap.getProfile();

		// getting a binding to all components on the screen
		etContent = (EditText) findViewById(R.id.update_task_description);
		radioNormal = (RadioButton) findViewById(R.id.update_normal_priority_radio);
		radioHigh = (RadioButton) findViewById(R.id.update_highl_priority_radio);
		picture = (ImageView) findViewById(R.id.update_task_image);
		emailNotify = (CheckBox) findViewById(R.id.update_enable_email_switch);
		smsNotify = (CheckBox) findViewById(R.id.update_enable_sms_switch);
		startDate = (TextView) findViewById(R.id.update_start_date);
		dueDate = (TextView) findViewById(R.id.update_due_date);
		btnUpdate = (Button) findViewById(R.id.update_update_task);
		btnDelete = (Button) findViewById(R.id.update_delete_task);

		// initializing the screen of the task as is
		etContent.setText(task.getContent());

		// initialize the priority radio button
		if (task.getPriority().equals("High")) {
			radioHigh.setChecked(true);
		} else {
			radioNormal.setChecked(true);
		}

		// Initialize check boxes
		if (task.getEmailNotify().equals("true")) {
			emailNotify.setChecked(true);
		} else {
			emailNotify.setChecked(false);
		}
		if (task.getSmsNotify().equals("true")) {
			smsNotify.setChecked(true);
		} else {
			smsNotify.setChecked(false);
		}

		// initializing date fields
		startDate.setText(task.getDateSet());
		dueDate.setText(task.getDateDue());

		// initialize the image
		if (task.getPicture() != null) {
			String path = task.getPicture();
			Log.d("Picture path in Update", path);
			Drawable img = setUpImageView(path);
			if (img != null) {
				picture.setImageDrawable(img);
			} else {
			}
		} else {
		}
		// parse the date string
		String duedate = task.getDateDue();
		int firstSeperator = duedate.indexOf("/");
		int secondSeperator = duedate.lastIndexOf("/");

		dueMonth = Integer.parseInt(duedate.substring(0, firstSeperator - 1));
		dueDay = Integer.parseInt(duedate.substring(firstSeperator + 2,
				secondSeperator - 1));
		dueYear = Integer.parseInt(duedate.substring(secondSeperator + 2));

		// TODO
		// Initialize picture from database

		// Adding onClick action listener to the 3 buttons and 2 date fields
		btnUpdate.setOnClickListener(this);
		btnDelete.setOnClickListener(this);
		dueDate.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.update_update_task:

			TaskRecord mTask = task;
			mTask.setContent(etContent.getText().toString());
			mTask.setDateDue(dueDate.getText().toString());
			mTask.setEmailNotify(emailNotify.isChecked() + "");
			mTask.setSmsNotify(smsNotify.isChecked() + "");
			if (radioNormal.isChecked()) {
				mTask.setPriority("Normal");

			} else {
				mTask.setPriority("High");
			}

			try {
				dbM = new TaskDatabaseManager(UpdateTaskActivity.this);
				dbM.openDB();

				dbM.updateRecord(mTask);
				dbM.closeDB();

			} catch (Exception e) {
				e.printStackTrace();
			}

			Intent refreshUpdateIntent = new Intent(UpdateTaskActivity.this,
					MainActivity.class);
			refreshUpdateIntent.putExtra("com.gharseldin.taskorganize.user",
					user);
			refreshUpdateIntent.putExtra(
					"com.gharseldin.taskorganize.settings", new SettingWrapper(
							profile));
			Log.d("Testing", user.getFirstName());
			startActivity(refreshUpdateIntent);
			break;

		case R.id.update_due_date:
			break;

		case R.id.update_delete_task:

			// Creating alert Dialog with two Buttons

			AlertDialog.Builder alertDialog = new AlertDialog.Builder(
					UpdateTaskActivity.this);

			// Setting Dialog Title
			alertDialog.setTitle("Confirm Delete...");

			// Setting Dialog Message
			alertDialog.setMessage("Are you sure you want delete this Task?");

			// Setting Icon to Dialog
			alertDialog.setIcon(R.drawable.deleteicon);

			// Setting Positive "Yes" Button
			alertDialog.setPositiveButton("YES",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// Write your code here to execute after dialog
							try {
								dbM = new TaskDatabaseManager(
										UpdateTaskActivity.this);
								dbM.openDB();

								dbM.deleteRecord(task);
								dbM.closeDB();

							} catch (Exception e) {
								e.printStackTrace();
							}

							Toast.makeText(UpdateTaskActivity.this,
									"Your task has been deleted successfully",
									Toast.LENGTH_SHORT).show();
							Intent refreshUpdateIntent = new Intent(
									UpdateTaskActivity.this, MainActivity.class);
							refreshUpdateIntent.putExtra(
									"com.gharseldin.taskorganize.user", user);
							refreshUpdateIntent.putExtra(
									"com.gharseldin.taskorganize.settings",
									new SettingWrapper(profile));
							Log.d("Testing", user.getFirstName());
							startActivity(refreshUpdateIntent);
						}
					});
			// Setting Negative "NO" Button
			alertDialog.setNegativeButton("NO",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// Write your code here to execute after dialog

							dialog.cancel();
						}
					});

			// Showing Alert Message
			alertDialog.show();
			break;
		// DialogFragment newFragment = new DatePickerFragment() {
		//
		// @Override
		// public Dialog onCreateDialog(Bundle savedInstanceState) {
		//
		// // Create a new instance of DatePickerDialog and return it
		// return new DatePickerDialog(UpdateTaskActivity.this, this, dueYear,
		// dueMonth, dueDay);
		// }
		//
		//
		// @Override
		// public void onDateSet(DatePicker view, int year, int month,
		// int day) {
		// month++;
		//
		// //verify that the due date is not before the set date
		// if(year<dueYear){
		// dueDate.setText(dueMonth + " / " + dueDay + " / " + dueYear);
		// }else{
		// if(month<dueMonth&&year==dueYear){
		// dueDate.setText(dueMonth + " / " + dueDay + " / " + dueYear);
		// }
		// else if(day<=dueDay&&month<=dueMonth){
		// dueDate.setText(dueMonth + " / " + dueDay + " / " + dueYear);
		// }else{
		// dueDate.setText(month + " / " + day + " / " + year);
		// }
		// }
		// }
		// };
		// newFragment.show((UpdateTaskActivity.this).getSupportFragmentManager(),
		// "update due date");
		// break;

		}
	}

	private Drawable setUpImageView(String imagePath) {

		try {
			if (imagePath.length() != 0) {
				File f = new File(imagePath);
				if (f.exists()) {
					Bitmap image = shrinkBitmap(f.getPath(), 800, 800);
					Drawable imageDrawable = new BitmapDrawable(getResources(),
							image);
					return imageDrawable;
					// imageView.setBackgroundColor(Color.WHITE);
				} else {
					// if a path exists but a file does not then decide what to
					// view
					return null;
				}
			} else {
				// if there is no path for image decide what to view
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;
	}

	/** Shrinking bitmap image to avoid outOfMemory exception */
	public Bitmap shrinkBitmap(String bitmapPath, int width, int height) {

		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
		bmpFactoryOptions.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath, bmpFactoryOptions);

		int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight
				/ (float) height);
		int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth
				/ (float) width);

		if (heightRatio > 1 || widthRatio > 1) {
			if (heightRatio > widthRatio) {
				bmpFactoryOptions.inSampleSize = heightRatio;
			} else {
				bmpFactoryOptions.inSampleSize = widthRatio;
			}
		}

		bmpFactoryOptions.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(bitmapPath, bmpFactoryOptions);
		return bitmap;
	}

}
