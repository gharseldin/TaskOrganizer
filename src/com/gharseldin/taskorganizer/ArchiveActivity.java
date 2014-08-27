package com.gharseldin.taskorganizer;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.gharseldin.taskorganizer.adapters.ArchiveAdapter;
import com.gharseldin.taskorganizer.database.TaskDatabaseManager;
import com.gharseldin.taskorganizer.datastructures.SettingWrapper;
import com.gharseldin.taskorganizer.datastructures.SettingsUnit;
import com.gharseldin.taskorganizer.datastructures.TaskRecord;
import com.gharseldin.taskorganizer.datastructures.UserRecord;

public class ArchiveActivity extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_archive);

		// get the user data from the passed intent
		UserRecord user = (UserRecord) getIntent().getSerializableExtra(
				"com.gharseldin.taskorganize.user");

		// get the user settings profile from the passed intent
		SettingWrapper wrap = (SettingWrapper) getIntent()
				.getSerializableExtra("com.gharseldin.taskorganize.settings");

		ArrayList<SettingsUnit> profile = wrap.getProfile();
		ListView archiveList = (ListView) findViewById(R.id.archive_list);

		// create a TaskDataBaseManager object to retrieve the tasks to be
		// viewed from the tasks table

		TaskDatabaseManager AdbM = new TaskDatabaseManager(ArchiveActivity.this);

		try {
			// Open the database
			AdbM.openDB();
			int count = AdbM.getUserTasksCount(user.getUserId());
			TaskRecord[] userTasks = AdbM.getUserTaks(count, user.getUserId());

			// TODO
			// perform the a process of filtration of data according to the
			// dates specified in the settings file using an ArrayList then
			// return the values back into array after manipulating the complete
			// status in the array by concatinating the complete date to the
			// complete string
			// pass to the adapter

			// pass the TaskRecord Array to the adapter to populate the view
			ArchiveAdapter adapt = new ArchiveAdapter(ArchiveActivity.this,
					userTasks);
			archiveList.setAdapter(adapt);

		} catch (Exception ex) {

			ex.printStackTrace();

		}
	}
}
