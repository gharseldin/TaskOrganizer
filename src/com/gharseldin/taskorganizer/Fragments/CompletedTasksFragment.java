package com.gharseldin.taskorganizer.Fragments;

import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gharseldin.taskorganizer.MainActivity;
import com.gharseldin.taskorganizer.R;
import com.gharseldin.taskorganizer.database.TaskDatabaseManager;
import com.gharseldin.taskorganizer.datastructures.SettingsUnit;
import com.gharseldin.taskorganizer.datastructures.TaskRecord;
import com.gharseldin.taskorganizer.datastructures.UserRecord;

@SuppressLint("NewApi")
public class CompletedTasksFragment extends Fragment {

	private TextView tvComplete, tvIncomplete, tvLate;
	private ImageView percentageImage;
	private TaskDatabaseManager dbM;
	private UserRecord user;
	private ArrayList<SettingsUnit> profile;
	private Context ctx;
	private Calendar today;
	private int tasksComplete, tasksIcomplete, tasksLate;
	private int trackingPeriod;
	private int percentage;

	public static CompletedTasksFragment newInstance(int sectionNumber) {
		CompletedTasksFragment fragment = new CompletedTasksFragment();
		Bundle args = new Bundle();
		args.putInt("section_number", sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public CompletedTasksFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_complete, container,
				false);

		tvComplete = (TextView) rootView
				.findViewById(R.id.complete_complete_number);
		tvIncomplete = (TextView) rootView
				.findViewById(R.id.complete_pending_number);
		tvLate = (TextView) rootView.findViewById(R.id.complete_late_number);
		percentageImage = (ImageView) rootView.findViewById(R.id.imageView1);

		// initialize counters to zero
		tasksComplete = 0;
		tasksIcomplete = 0;
		tasksLate = 0;

		// To get the user Data from the parent (MainActivity) view
		user = ((MainActivity) getActivity()).getUser();
		profile = ((MainActivity) getActivity()).getProfile();
		ctx = (MainActivity) getActivity();

		// Today's date
		today = Calendar.getInstance();
		Log.d("Date", "Today is " + today.toString());

		// setting tracking period from settings profile.
		for(SettingsUnit x : profile){
		
			if(x.getKey().equals("tracking_period")){
				trackingPeriod = Integer.parseInt(x.getValue());
			}
		}
		// Getting all the Tasks according to the Tracking settings
		try {

			dbM = new TaskDatabaseManager(rootView.getContext());
			dbM.openDB();

			// a query should be made first to get the count of relevant tasks
			// to that person
			int count = dbM.getUserTasksCount(user.getUserId());

			TaskRecord[] tasks = new TaskRecord[count];
			tasks = dbM.getUserTaks(count, user.getUserId());

			ArrayList<TaskRecord> trackedTasks = getTasksToTrackNow(tasks);

			// Initialize the completed number of tracked Tasks
			for (TaskRecord x : trackedTasks) {
				if (x.getStatus().equals("complete")) {
					tasksComplete++;
				}
			}

			// Initialize the incomplete number of tracked Tasks
			for (TaskRecord x : trackedTasks) {
				if (x.getStatus().equals("pending")) {
					tasksIcomplete++;
				}
			}

			// Initialize the late number of tracked Tasks
			for (TaskRecord x : trackedTasks) {
				Calendar tDate = Calendar.getInstance();

				String rawDate = x.getDateDue();

				int firstSeperator = rawDate.indexOf("/");
				int secondSeperator = rawDate.lastIndexOf("/");

				int dueMonth = Integer.parseInt(rawDate.substring(0,
						firstSeperator - 1));
				int dueDay = Integer.parseInt(rawDate.substring(
						firstSeperator + 2, secondSeperator - 1));
				int dueYear = Integer.parseInt(rawDate
						.substring(secondSeperator + 2));

				tDate.set(dueYear, --dueMonth, dueDay);

				if (tDate.compareTo(today) < 0) {
					Log.d("Date", "Date Due = " + x.getDateDue() + " offset = "
							+ tDate.compareTo(today));
					tasksLate++;
				}
			}

			dbM.closeDB();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// set screen values
		tvComplete.setText("" + tasksComplete);
		tvIncomplete.setText("" + tasksIcomplete);
		tvLate.setText("" + tasksLate);

		// setting the percentage value to the closest 10%
		double temp = (double) tasksComplete
				/ ((double) tasksComplete + (double) tasksIcomplete);
		temp *= 10;
		percentage = (int) (10 * Math.floor(temp));
		switch (percentage) {
		case 0:
			percentageImage.setImageResource(R.drawable.zero_percent);
			break;

		case 10:
			percentageImage.setImageResource(R.drawable.ten_percent);
			break;

		case 20:
			percentageImage.setImageResource(R.drawable.twenty_percent);
			break;

		case 30:
			percentageImage.setImageResource(R.drawable.thirty_percent);
			break;

		case 40:
			percentageImage.setImageResource(R.drawable.forty_percent);
			break;

		case 50:
			percentageImage.setImageResource(R.drawable.fifty_percent);
			break;

		case 60:
			percentageImage.setImageResource(R.drawable.sixty_percent);
			break;

		case 70:
			percentageImage.setImageResource(R.drawable.seventy_percent);
			break;

		case 80:
			percentageImage.setImageResource(R.drawable.eighty_percent);
			break;

		case 90:
			percentageImage.setImageResource(R.drawable.ninty_percent);
			break;

		case 100:
			percentageImage.setImageResource(R.drawable.hundred_percent);
			break;

		}
		Log.d("Percentage", "percentage as a double " + temp);
		return rootView;
	}

	private ArrayList<TaskRecord> getTasksToTrackNow(TaskRecord[] raw) {
		ArrayList<TaskRecord> build = new ArrayList<TaskRecord>();
		Calendar taskDate = Calendar.getInstance();

		for (int i = 0; i < raw.length; i++) {

			String rawDate = raw[i].getDateDue();

			int firstSeperator = rawDate.indexOf("/");
			int secondSeperator = rawDate.lastIndexOf("/");

			int dueMonth = Integer.parseInt(rawDate.substring(0,
					firstSeperator - 1));
			int dueDay = Integer.parseInt(rawDate.substring(firstSeperator + 2,
					secondSeperator - 1));
			int dueYear = Integer.parseInt(rawDate
					.substring(secondSeperator + 2));

			taskDate.set(dueYear, --dueMonth, dueDay);
			Log.d("Date fields retrieved " + i, taskDate.toString());

			// if the difference between the due date of this task is the same
			// or less that tracking period then add it to the Arraylist
			if (Math.abs((taskDate.getTimeInMillis() - today.getTimeInMillis()) / 86400000) <= trackingPeriod) {
				Log.d("element" + i + "Added to array ",
						""
								+ (taskDate.getTimeInMillis() - today
										.getTimeInMillis()) / 86400000);
				build.add(raw[i]);
			}
		}
		return build;
	}
}
