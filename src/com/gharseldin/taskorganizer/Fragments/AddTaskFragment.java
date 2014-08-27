package com.gharseldin.taskorganizer.Fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.gharseldin.taskorganizer.CameraActivity;
import com.gharseldin.taskorganizer.MainActivity;
import com.gharseldin.taskorganizer.R;
import com.gharseldin.taskorganizer.database.TaskDatabaseManager;
import com.gharseldin.taskorganizer.datastructures.SettingWrapper;
import com.gharseldin.taskorganizer.datastructures.SettingsUnit;
import com.gharseldin.taskorganizer.datastructures.TaskRecord;
import com.gharseldin.taskorganizer.datastructures.UserRecord;

@SuppressLint("NewApi")
public class AddTaskFragment extends Fragment implements OnClickListener {

	private EditText etContent;
	private RadioButton radioNormal;
	private RadioButton radioHigh;
	private ImageView picture;
	private CheckBox emailNotify;
	private CheckBox smsNotify;
	private TextView startDate, dueDate;

	private Button btnAdd, btnUpdate, btnDelete;
	private Calendar setCalendar;
	private Calendar dueCalendar;

	private String setString;
	private String dueString;
	private String imagePath = "";

	private Drawable imageDrawable;
	private Bitmap image;
	private Context ctx;
	private TaskDatabaseManager dbM;
	private UserRecord user;
	private ArrayList<SettingsUnit> profile;

	private int dueMonth = 0;
	private int dueDay = 0;
	private int dueYear = 0;

	public static AddTaskFragment newInstance(int sectionNumber) {
		AddTaskFragment fragment = new AddTaskFragment();
		Bundle args = new Bundle();
		args.putInt("section_number", sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public AddTaskFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_addtask, container,
				false);

		ctx = ((MainActivity) getActivity()).getContext();

		// Initialize the user field
		user = ((MainActivity) getActivity()).getUser();
		profile = ((MainActivity) getActivity()).getProfile();

		// getting a binding to all components on the screen
		etContent = (EditText) rootView.findViewById(R.id.task_description);
		radioNormal = (RadioButton) rootView
				.findViewById(R.id.normal_priority_radio);
		radioHigh = (RadioButton) rootView
				.findViewById(R.id.highl_priority_radio);
		picture = (ImageView) rootView.findViewById(R.id.task_image);
		emailNotify = (CheckBox) rootView
				.findViewById(R.id.enable_email_switch);
		smsNotify = (CheckBox) rootView.findViewById(R.id.enable_sms_switch);
		startDate = (TextView) rootView.findViewById(R.id.start_date);
		startDate.setOnClickListener((android.view.View.OnClickListener) this);
		dueDate = (TextView) rootView.findViewById(R.id.due_date);
		btnAdd = (Button) rootView.findViewById(R.id.add_task);
		btnUpdate = (Button) rootView.findViewById(R.id.update_task);
		btnDelete = (Button) rootView.findViewById(R.id.delete_task);

		// Adding onClick action listener to the 3 buttons and 2 date fields
		btnAdd.setOnClickListener(this);
		btnUpdate.setOnClickListener(this);
		btnDelete.setOnClickListener(this);

		startDate.setOnClickListener(this);
		dueDate.setOnClickListener(this);

		picture.setOnClickListener(this);
		// All this is happening if we come from a scratch field

		// initializing default Date fields
		setNewTaskDefaults();

		return rootView;
	}

	// handles the clicking of the 3 buttons of the screen and the setting of
	// the two dates field
	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.add_task:

			TaskRecord mTask = new TaskRecord();

			mTask.setUserID(user.getUserId());
			mTask.setPicture(imagePath);
			mTask.setContent(etContent.getText().toString());
			mTask.setDateDue(dueDate.getText().toString());
			mTask.setDateSet(startDate.getText().toString());
			mTask.setEmailNotify(emailNotify.isChecked() + "");
			mTask.setSmsNotify(smsNotify.isChecked() + "");
			mTask.setStatus("pending");
			if (radioNormal.isChecked()) {
				mTask.setPriority("Normal");
			} else {
				mTask.setPriority("High");
			}

			// Insert Values in Database

			Log.d("Insert Picture", "Inserting " + imagePath);
			try {
				dbM = new TaskDatabaseManager((MainActivity) getActivity());
				dbM.openDB();

				dbM.insertRecord(mTask);
				dbM.closeDB();

			} catch (Exception e) {
				e.printStackTrace();
			}

			Intent refreshIntent = new Intent(ctx, MainActivity.class);
			refreshIntent.putExtra("com.gharseldin.taskorganize.user", user);
			refreshIntent.putExtra("com.gharseldin.taskorganize.settings",
					new SettingWrapper(profile));

			startActivity(refreshIntent);

			// Clear the screen back to defaults
			setNewTaskDefaults();
			break;

		

		// Try to implement calling the dialogs using DialogFragment

		// in this case we will need to open a date picker and store the item
		case R.id.start_date:

			// Will keep Start Day functionality idle for now. I don't think we
			// will need to change that manually

			break;

		case R.id.due_date:
			DialogFragment newFragment = new DatePickerFragment() {

				@Override
				public Dialog onCreateDialog(Bundle savedInstanceState) {
					// Use the current date as the default date in the picker
					final Calendar c = Calendar.getInstance();
					int year = c.get(Calendar.YEAR);
					int month = c.get(Calendar.MONTH);
					int day = c.get(Calendar.DAY_OF_MONTH);

					// Create a new instance of DatePickerDialog and return it
					return new DatePickerDialog(getActivity(), this, year,
							month, ++day);
				}

				@Override
				public void onDateSet(DatePicker view, int year, int month,
						int day) {
					month++;

					// verify that the due date is not before the set date
					if (year < dueYear) {
						dueDate.setText(dueMonth + " / " + dueDay + " / "
								+ dueYear);
					} else {
						if (month < dueMonth && year == dueYear) {
							dueDate.setText(dueMonth + " / " + dueDay + " / "
									+ dueYear);
						} else if (day <= dueDay && month <= dueMonth) {
							dueDate.setText(dueMonth + " / " + dueDay + " / "
									+ dueYear);
						} else {
							dueDate.setText(month + " / " + day + " / " + year);
						}
					}
				}
			};
			newFragment.show(getActivity().getSupportFragmentManager(),
					"datePicker");
			break;

		case R.id.task_image:
			
			Intent cameraIntent = new Intent(ctx, CameraActivity.class);
			cameraIntent.putExtra("com.gharseldin.taskorganize.user", user);
			cameraIntent.putExtra("com.gharseldin.taskorganize.settings",
					new SettingWrapper(profile));

			startActivityForResult(cameraIntent, 5);
			Log.d("Camera", "Pressed");
		}

	}

	private void setNewTaskDefaults() {

		setCalendar = Calendar.getInstance();
		dueCalendar = Calendar.getInstance();

		dueMonth = (dueCalendar.get(Calendar.MONTH)) + 1;
		dueDay = setCalendar.get(Calendar.DAY_OF_MONTH);
		dueYear = setCalendar.get(Calendar.YEAR);
		setString = dueMonth + " / " + dueDay + " / " + dueYear;
		// set a default due date one day ahead (Tomorrow)
		dueDay++;
		dueString = dueMonth + " / " + dueDay + " / " + dueYear;

		// Initializing default Task Content
		etContent.setText("");
		radioNormal.setChecked(true);
		emailNotify.setChecked(false);
		smsNotify.setChecked(false);
		startDate.setText(setString);
		dueDate.setText(dueString);
	}

	@Override
	public void onResume() {

		super.onResume();
		setUpImageView();
	}

	private void setUpImageView() {

		try {
			if (imagePath.length() != 0) {
				File f = new File(imagePath);
				if (f.exists()) {
					image = shrinkBitmap(f.getPath(), 800, 800);
					imageDrawable = new BitmapDrawable(getResources(), image);
					picture.setImageDrawable(imageDrawable);
					// imageView.setBackgroundColor(Color.WHITE);
				} else {
					// if a path exists but a file does not then decide what to
					// view
				}
			} else {
				// if there is no path for image decide what to view
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case (5): {
			if (resultCode == Activity.RESULT_OK) {
				imagePath = data
						.getStringExtra("com.gharseldin.taskorganize.imagePath");
				Log.d("Update Picture", imagePath);
			}
			break;
		}
		}
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
