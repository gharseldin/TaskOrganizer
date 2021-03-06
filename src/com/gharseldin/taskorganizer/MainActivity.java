package com.gharseldin.taskorganizer;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.gharseldin.taskorganizer.Fragments.AddTaskFragment;
import com.gharseldin.taskorganizer.Fragments.CompletedTasksFragment;
import com.gharseldin.taskorganizer.Fragments.TaskListFragment;
import com.gharseldin.taskorganizer.database.TaskDatabaseManager;
import com.gharseldin.taskorganizer.datastructures.SettingWrapper;
import com.gharseldin.taskorganizer.datastructures.SettingsUnit;
import com.gharseldin.taskorganizer.datastructures.UserRecord;

public class MainActivity extends ActionBarActivity implements
		ActionBar.TabListener {

	private TaskDatabaseManager dbM;
	private UserRecord user;
	private SettingWrapper wrap;
	private ArrayList<SettingsUnit> profile;

	private static final int REQUEST_CODE = 15;
	private static final int START_DATE_DIALOG = 888;
	private static final int DUE_DATE_DIALOG = 999;
	
	private int day, month, year;
	private EditText etContent;
	private RadioButton radioNormal;
	private RadioButton radioHigh; // might be used later
	private ImageView picture; // its functionality to be implemented later
	private CheckBox emailNotify;
	private CheckBox smsNotify;
	private TextView startDate, dueDate;

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		user = (UserRecord) getIntent().getSerializableExtra(
				"com.gharseldin.taskorganize.user");
		profile = ((SettingWrapper) getIntent().getSerializableExtra(
				"com.gharseldin.taskorganize.settings")).getProfile();
		wrap = new SettingWrapper(profile);


		
		
		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		// Create a tab with text corresponding to the page title defined by
		// the adapter. Also specify this Activity object, which implements
		// the TabListener interface, as the callback (listener) for when
		// this tab is selected.
		actionBar.addTab(actionBar.newTab().setText("Tasks")
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("+").setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("Progress")
				.setTabListener(this));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case (REQUEST_CODE): {
			if (resultCode == Activity.RESULT_OK) {
				user = (UserRecord) data
						.getSerializableExtra("com.gharseldin.taskorganize.user");
				Log.d("Result back", user.getFirstName());
			}
			break;
		}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		switch (id) {

		case R.id.action_settings:

			Intent settingsIntent = new Intent(MainActivity.this,
					SettingsActivity.class);
			settingsIntent.putExtra("com.gharseldin.taskorganize.user", user);
			startActivity(settingsIntent);
			return true;

			
		case R.id.action_archive:
			Intent ArchiveIntent = new Intent(MainActivity.this,
					ArchiveActivity.class);
			ArchiveIntent.putExtra("com.gharseldin.taskorganize.user", user);
			ArchiveIntent
					.putExtra("com.gharseldin.taskorganize.settings", wrap);
			startActivity(ArchiveIntent);

			return true;
			
		case R.id.action_profile:
			Intent profileIntent = new Intent(MainActivity.this,
					SignupActivity.class);
			profileIntent.putExtra("com.gharseldin.taskorganize.user", user);
			profileIntent
					.putExtra("com.gharseldin.taskorganize.settings", wrap);
			startActivity(profileIntent);

			return true;
		case R.id.action_logout:
			Intent logoutIntent = new Intent(MainActivity.this,
					SignInActivity.class);
			startActivity(logoutIntent);
			return true;
		case R.id.action_about:

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			switch (position) {
			case 0:
				return TaskListFragment.newInstance(position + 1);
			case 1:
				return AddTaskFragment.newInstance(position + 1);
			case 2:
				return CompletedTasksFragment.newInstance(position + 1);
			default:
				return null;
			}
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}
	

	// Returns UserRecord
	public UserRecord getUser() {
		return user;
	}

	// Returns Settings profile
	public ArrayList<SettingsUnit> getProfile() {
		return profile;
	}
	
	public Context getContext(){
		return MainActivity.this;
	}
}
