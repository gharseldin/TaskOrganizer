package com.gharseldin.taskorganizer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gharseldin.taskorganizer.database.SettingsDatabaseManager;
import com.gharseldin.taskorganizer.database.UserDatabaseManager;
import com.gharseldin.taskorganizer.datastructures.SettingWrapper;
import com.gharseldin.taskorganizer.datastructures.SettingsUnit;
import com.gharseldin.taskorganizer.datastructures.UserRecord;

public class SignInActivity extends Activity {

	private EditText username, password;
	private TextView welcomeMessage, tvForgotPassword, tvNewUser;
	private UserRecord user;
	private ArrayList<SettingsUnit> profile;
	private Button btnLogin;

	private UserDatabaseManager dbM;
	private SettingsDatabaseManager dBm;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

	}

	public void logIn(View v) {
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		welcomeMessage = (TextView) findViewById(R.id.quote_of_the_day);
		tvForgotPassword = (TextView) findViewById(R.id.forgot_passowrd);
		tvNewUser = (TextView) findViewById(R.id.new_user);
		btnLogin = (Button) findViewById(R.id.login);

		try {
			dbM = new UserDatabaseManager(SignInActivity.this);
			dBm = new SettingsDatabaseManager(SignInActivity.this);
			// first Check its there, if not create it
			try {
				if (!dbM.checkDataBase()) {
					dbM.createDataBase();
				}
				dbM.openDB();
			} catch (Exception e) {
				e.printStackTrace();
			}

			// create a new user record from log in credentials to check for
			// correctness
			user = new UserRecord(username.getText().toString().trim().toLowerCase(), password
					.getText().toString());

			// if present this method returns true and updates user record
			if (dbM.chkLogin(user)) {

				welcomeMessage.setTextColor(Color.WHITE);
				welcomeMessage.setText("Welcome Back \n" + user.getFirstName()
						+ " " + user.getLastName());
				btnLogin.setVisibility(View.GONE);
				username.setVisibility(View.GONE);
				password.setVisibility(View.GONE);
				tvForgotPassword.setVisibility(View.GONE);
				tvNewUser.setVisibility(View.GONE);

				Thread.sleep(2000);

				// Retrieve DataBase Information
				try {
					dBm.openDb();
					if (dBm.checkUserSettingsRecord(user.getUserId())) {
						profile = dBm.getUserSettingsRecord(user.getUserId());
						// set the settings according to the saved profile.
						Log.d("User Settings", "No problem with fetching profile");
					} else {
						setDefaultSettings();
					}
					dBm.closeDB();
				} catch (SQLException ex) {
					ex.printStackTrace();
					dBm.closeDB();
					Log.d("User Settings", "problem with fetching profile");
					// any error in retrieving the user's Setting profile just
					// returns
					// him to the default settings screen
					setDefaultSettings();
				}
				

				Intent intent = new Intent(SignInActivity.this,
						MainActivity.class);

				//Warpping Settings profile to send
				SettingWrapper wrap = new SettingWrapper(profile);
				
				Log.d("User Settings", "wrapping successful");
				// sending a full User record with settings profile to the next
				// activity
				intent.putExtra("com.gharseldin.taskorganize.user", user)
						.putExtra("com.gharseldin.taskorganize.settings", wrap);
				startActivity(intent);
				
				Log.d("User Settings", "sending successful");

			} else {
				// inform user on the screen that entered credentials are
				// invalid
				welcomeMessage
						.setText("Invalid username or password!\nplease try again!");
				welcomeMessage.setTextColor(Color.rgb(102, 0, 0));
			}
		} catch (Exception ex) {
			welcomeMessage.setTextColor(Color.rgb(102, 0, 0));
			welcomeMessage
					.setText("Application experiencing some problems!\nplease try again later");
			ex.printStackTrace();
		}
	}

	public void signUp(View v) {
		Intent intent = new Intent(SignInActivity.this, SignupActivity.class);
		startActivity(intent);
	}

	private void setDefaultSettings() {
		SettingsUnit[] unit = new SettingsUnit[6];
		profile = new ArrayList<SettingsUnit>();

		// Order from this point on is very important
		// My profile is acting like a stack of units
		unit[0] = new SettingsUnit();
		unit[0].setUserId(user.getUserId());
		unit[0].setKey("sms_alert");
		unit[0].setValue("5");

		profile.add(unit[0]);

		unit[1] = new SettingsUnit();
		unit[1].setUserId(user.getUserId());
		unit[1].setKey("email_alert");
		unit[1].setValue("5");

		profile.add(unit[1]);

		unit[2] = new SettingsUnit();
		unit[2].setUserId(user.getUserId());
		unit[2].setKey("enable_automatic_login");
		unit[2].setValue("false");

		profile.add(unit[2]);

		unit[3] = new SettingsUnit();
		unit[3].setUserId(user.getUserId());
		unit[3].setKey("enable_welcome_screen");
		unit[3].setValue("true");

		profile.add(unit[3]);

		unit[4] = new SettingsUnit();
		unit[4].setUserId(user.getUserId());
		unit[4].setKey("archive_expiry");
		unit[4].setValue("90");

		profile.add(unit[4]);

		unit[5] = new SettingsUnit();
		unit[5].setUserId(user.getUserId());
		unit[5].setKey("tracking_period");
		unit[5].setValue("30");

		profile.add(unit[5]);
	}
}