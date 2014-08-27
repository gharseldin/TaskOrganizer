package com.gharseldin.taskorganizer.Fragments;

import java.util.ArrayList;
import java.util.Calendar;

import android.R.color;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.gharseldin.taskorganizer.MainActivity;
import com.gharseldin.taskorganizer.R;
import com.gharseldin.taskorganizer.adapters.TaskListAdapter;
import com.gharseldin.taskorganizer.database.TaskDatabaseManager;
import com.gharseldin.taskorganizer.datastructures.SettingsUnit;
import com.gharseldin.taskorganizer.datastructures.TaskRecord;
import com.gharseldin.taskorganizer.datastructures.UserRecord;

@SuppressLint("NewApi")
public class TaskListFragment extends Fragment {

	private TaskDatabaseManager dbM;
	private TaskRecord[] tasks;
	private UserRecord user;
	private ListView list;
	private Context ctx;
	private ArrayList<SettingsUnit> profile;
	private TaskListAdapter adapter;
	private View rootView;
	private Spinner spinner;
	private Calendar today;
	private int trackingPeriod;
	private ArrayList<TaskRecord> trackedTasks;

	public static TaskListFragment newInstance(int sectionNumber) {

		TaskListFragment fragment = new TaskListFragment();
		Bundle args = new Bundle();
		args.putInt("section_number", sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public TaskListFragment() {
	}

	@Override
	public void onResume() {

		super.onResume();

		Log.d("TaskListFragment", "onResume() Called");
		// adapter.notifyDataSetChanged();
		list.invalidateViews();
		list.refreshDrawableState();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.activity_tasklist, container,
				false);

		list = (ListView) rootView.findViewById(R.id.list);
		spinner = (Spinner) rootView.findViewById(R.id.view_options);

		// To get the user Data from the parent (MainActivity) view
		user = ((MainActivity) getActivity()).getUser();
		profile = ((MainActivity) getActivity()).getProfile();
		ctx = (MainActivity) getActivity();

		// Today's date
		today = Calendar.getInstance();
		Log.d("Date", "Today is " + today.toString());

		// setting tracking period from settings profile.
		for (SettingsUnit x : profile) {
			if (x.getKey().equals("tracking_period")) {
				trackingPeriod = Integer.parseInt(x.getValue());
			}
		}
		Log.d("Logging Period", "Logging period = " + trackingPeriod);
		// Getting all the Tasks according to the Tracking settings
		try {

			dbM = new TaskDatabaseManager(rootView.getContext());
			dbM.openDB();

			// a query should be made first to get the count of relevant tasks
			// to that person
			int count = dbM.getUserTasksCount(user.getUserId());

			tasks = new TaskRecord[count];
			tasks = dbM.getUserTaks(count, user.getUserId());

			trackedTasks = getTasksToTrackNow(tasks);

			dbM.closeDB();
		} catch (Exception e) {
			e.printStackTrace();
		}

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				ctx, R.array.list_view, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		spinner.setBackgroundColor(color.darker_gray);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1,
					int position, long id) {
				switch (position) {

				case 0: // All
					Log.d("Spinner", parent.getItemAtPosition(position)
							.toString());

					// gets everything
					trackedTasks = getTasksToTrackNow(tasks);
					list.setAdapter(new TaskListAdapter(getActivity(),
							trackedTasks, user, profile));

					break;
				case 1: // High Priority
					Log.d("Spinner", parent.getItemAtPosition(position)
							.toString());

					ArrayList<TaskRecord> highPriorityTasks = new ArrayList<TaskRecord>();
					for (TaskRecord x : trackedTasks) {
						if (x.getPriority().equals("High")) {
							highPriorityTasks.add(x);
						}
					}
					list.setAdapter(new TaskListAdapter(getActivity(),
							highPriorityTasks, user, profile));
					break;

				case 2: // past Due

					Log.d("Spinner", parent.getItemAtPosition(position)
							.toString());

					// compare dates
					ArrayList<TaskRecord> lateTasks = new ArrayList<TaskRecord>();
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
							Log.d("Date", "Date Due = " + x.getDateDue()
									+ " offset = " + tDate.compareTo(today));
							lateTasks.add(x);
						}
					}
					Log.d("pastDue",
							"lateTasks Array size = " + lateTasks.size());
					list.setAdapter(new TaskListAdapter(getActivity(),
							lateTasks, user, profile));

					break;

				case 3: // Complete
					Log.d("Spinner", parent.getItemAtPosition(position)
							.toString());
					ArrayList<TaskRecord> completeTasks = new ArrayList<TaskRecord>();
					for (TaskRecord x : trackedTasks) {
						if (x.getStatus().equals("complete")) {
							completeTasks.add(x);
						}
					}
					list.setAdapter(new TaskListAdapter(getActivity(),
							completeTasks, user, profile));
					break;

				case 4: // incomplete
					Log.d("Spinner", parent.getItemAtPosition(position)
							.toString());
					ArrayList<TaskRecord> inCompleteTasks = new ArrayList<TaskRecord>();
					for (TaskRecord x : trackedTasks) {
						if (x.getStatus().equals("pending")) {
							inCompleteTasks.add(x);
						}
					}
					list.setAdapter(new TaskListAdapter(getActivity(),
							inCompleteTasks, user, profile));

					break;

				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		// TODO
		// Check here before you pass anything to an adapter on what the
		// spinner wants
		list.setAdapter(new TaskListAdapter(getActivity(), trackedTasks, user,
				profile));
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
			// or less that tracking period then add it to the arraylist
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
