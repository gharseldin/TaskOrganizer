package com.gharseldin.taskorganizer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.gharseldin.taskorganizer.database.SettingsDatabaseManager;
import com.gharseldin.taskorganizer.datastructures.SettingWrapper;
import com.gharseldin.taskorganizer.datastructures.SettingsUnit;
import com.gharseldin.taskorganizer.datastructures.UserRecord;

public class SettingsActivity extends Activity implements
		OnSeekBarChangeListener {

	private UserRecord user;
	private ArrayList<SettingsUnit> profile;

	private SeekBar sbEmail, sbSMS;
	private TextView tvEmailAlert, tvSMSAlert , tvTrackTimeTitle, tvArchiveLengthTitle;
	private EditText etTrackingPeriod, etArchiveLength;
	private CheckBox cbQuoteOfDay, cbAutomaticLogin;
	private boolean newUser = true;

	private SettingsDatabaseManager dBm;

	// TODO might want to implement the automatic login as a remember me
	// function

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		user = (UserRecord) getIntent().getSerializableExtra(
				"com.gharseldin.taskorganize.user");

		dBm = new SettingsDatabaseManager(SettingsActivity.this);

		sbEmail = (SeekBar) findViewById(R.id.email_alert_settings);
		sbSMS = (SeekBar) findViewById(R.id.sms_alert_settings);
		tvTrackTimeTitle = (TextView)findViewById(R.id.track_time_text);
		tvArchiveLengthTitle = (TextView)findViewById(R.id.archive_expire_text);
		tvEmailAlert = (TextView) findViewById(R.id.email_alert_number);
		tvSMSAlert = (TextView) findViewById(R.id.sms_alert_number);
		etTrackingPeriod = (EditText) findViewById(R.id.track_time_settings);
		etArchiveLength = (EditText) findViewById(R.id.archive_expire_settings);
		cbQuoteOfDay = (CheckBox) findViewById(R.id.quote_settings);
		cbAutomaticLogin = (CheckBox) findViewById(R.id.automatic_login_settings);

		sbEmail.setMax(30);
		sbSMS.setMax(30);
		sbEmail.setOnSeekBarChangeListener(this);
		sbSMS.setOnSeekBarChangeListener(this);

		// TODO All entries should updated after accessing the database. If no
		// record of setting was found then use the default

		try {
			dBm.openDb();
			if (dBm.checkUserSettingsRecord(user.getUserId())) {
				profile = dBm.getUserSettingsRecord(user.getUserId());
				// set the settings according to the saved profile.
				setRetrievedSettings(profile);
				newUser = false;
			} else {
				setDefaultSettings();
			}
			dBm.closeDB();
		} catch (SQLException ex) {
			ex.printStackTrace();
			dBm.closeDB();
			// any error in retrieving the user's Setting profile just returns
			// him to the default settings screen
			setDefaultSettings();
		}

	}

	private void setRetrievedSettings(ArrayList<SettingsUnit> p) {

		// get the records from top to bottom so do it the other way round
		sbSMS.setProgress(Integer.parseInt(p.get(5).getValue()));
		sbEmail.setProgress(Integer.parseInt(p.get(4).getValue()));
		updateSMSAlert();
		updateEmailAlert();

		cbAutomaticLogin.setChecked(Boolean.parseBoolean(p.get(3)
				.getValue()));
		cbQuoteOfDay.setChecked(Boolean
				.parseBoolean(p.get(2).getValue()));
		etArchiveLength.setText(Integer.parseInt(p.get(1).getValue())+"");
		etTrackingPeriod.setText(Integer.parseInt(p.get(0).getValue())+"");

	}

	private void setDefaultSettings() {
		// default settings
		etTrackingPeriod.setText("30");
		etArchiveLength.setText("90");
		sbEmail.setProgress(5);
		sbSMS.setProgress(5);
		cbQuoteOfDay.setChecked(true);
		cbAutomaticLogin.setChecked(false);
		updateSettingsProfileFromScreen();	
	}

	private void updateSMSAlert() {
		tvSMSAlert.setText(sbSMS.getProgress() + "");
	}

	private void updateEmailAlert() {
		tvEmailAlert.setText(sbEmail.getProgress() + "");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			SettingWrapper wrap = new SettingWrapper(profile);
			Intent upIntent = new Intent(this, MainActivity.class);
			upIntent.putExtra("com.gharseldin.taskorganize.user", user)
			.putExtra("com.gharseldin.taskorganize.settings", wrap);
			startActivity(upIntent);
			// Intent upIntent = NavUtils.getParentActivityIntent(this);
			// upIntent.putExtra("com.gharseldin.taskorganize.user", user);
			// NavUtils.navigateUpTo(this, upIntent);

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void saveSettings(View v) {

		int trackingNumner;
		int archiveNumber;
		trackingNumner = Integer
				.parseInt(etTrackingPeriod.getText().toString());
		archiveNumber = Integer.parseInt(etArchiveLength.getText().toString());

		if (trackingNumner < 1 || trackingNumner > 30 || archiveNumber < 1
				|| archiveNumber > 90) {
			if (trackingNumner < 1 || trackingNumner > 30) {
				// show an alert for tracking number
				tvTrackTimeTitle.setText(R.string.track_time_text_error);
			}else{
				//if user corrected one of two false values
				tvTrackTimeTitle.setText(R.string.track_time_text);
				
			}
			if (archiveNumber < 1 || archiveNumber > 90) {
				// show an alert for archive number
				tvArchiveLengthTitle.setText(R.string.archive_expire_text_error);
			}else{
				//if user corrected one of two false values
				tvArchiveLengthTitle.setText(R.string.archive_expire_text);
			}
			return;
		} else{	//all Settings values are within allowable limits
			
			tvTrackTimeTitle.setText(R.string.track_time_text);
			tvArchiveLengthTitle.setText(R.string.archive_expire_text);
			//create a profile and send it to an insert or update function in the database
			if(newUser){
				updateSettingsProfileFromScreen();
				try{
					dBm.openDb();
					dBm.insertUserSettings(profile);
					newUser=false;
					Toast.makeText(SettingsActivity.this, "User Settings updated Successfully",
							Toast.LENGTH_SHORT).show();
				}catch (Exception ex){
					ex.printStackTrace();
					Log.d("Settings Databse", "Could not insert a new Settings record");
				}
			}else{ //this was an existing user we will just update the values
				updateSettingsProfileFromScreen();
				try{
					dBm.openDb();
					dBm.updateUserSettings(profile);

					Toast.makeText(SettingsActivity.this, "User Settings updated Successfully",
							Toast.LENGTH_SHORT).show();
				}catch (Exception ex){
					ex.printStackTrace();
					Log.d("Settings Databse", "Could not insert a new Settings record");
				}
			}
			// update the profile and pass it along to mainActivity along with the user


		}
		
	}
	
	private void updateSettingsProfileFromScreen(){
		
		SettingsUnit[] unit =  new SettingsUnit[6];
		profile = new ArrayList<SettingsUnit>();
		
		
		//Order from this point on is very important
		//My profile is acting like a stack of units
		unit[0] = new SettingsUnit();
		unit[0].setUserId(user.getUserId());
		unit[0].setKey("sms_alert");
		unit[0].setValue((sbSMS.getProgress()+""));
		
		profile.add(unit[0]);
		
		unit[1] = new SettingsUnit();
		unit[1].setUserId(user.getUserId());
		unit[1].setKey("email_alert");
		unit[1].setValue((sbEmail.getProgress()+""));
		
		profile.add(unit[1]);
		
		unit[2] = new SettingsUnit();
		unit[2].setUserId(user.getUserId());
		unit[2].setKey("enable_automatic_login");
		unit[2].setValue((cbAutomaticLogin.isChecked()+""));
		
		profile.add(unit[2]);
		
		unit[3] = new SettingsUnit();
		unit[3].setUserId(user.getUserId());
		unit[3].setKey("enable_welcome_screen");
		unit[3].setValue((cbQuoteOfDay.isChecked()+""));
		
		profile.add(unit[3]);
		
		unit[4] = new SettingsUnit();
		unit[4].setUserId(user.getUserId());
		unit[4].setKey("archive_expiry");
		unit[4].setValue((etArchiveLength.getText().toString()));
		
		profile.add(unit[4]);
		
		unit[5] = new SettingsUnit();
		unit[5].setUserId(user.getUserId());
		unit[5].setKey("tracking_period");
		unit[5].setValue((etTrackingPeriod.getText().toString()));

		profile.add(unit[5]);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		updateSMSAlert();
		updateEmailAlert();
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}
}
