package com.gharseldin.taskorganizer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gharseldin.taskorganizer.database.UserDatabaseManager;
import com.gharseldin.taskorganizer.datastructures.SettingWrapper;
import com.gharseldin.taskorganizer.datastructures.UserRecord;

public class SignupActivity extends Activity {

	private EditText etUsername, etPassword, etPasswordchk, etFirstName,
			etLastName, etEmail, etPhone;
	private Button btnSignUp, btnClear, btnUpdate;
	private ImageView ivTaskImage;
	private UserRecord user;
	private SettingWrapper wrap;
	private UserDatabaseManager dbM;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);

		// Creating handles to screen content

		ivTaskImage = (ImageView) findViewById(R.id.task_image);
		btnClear = (Button) findViewById(R.id.clear_form);
		btnSignUp = (Button) findViewById(R.id.create_user);
		btnUpdate = (Button) findViewById(R.id.update_record);
		etUsername = (EditText) findViewById(R.id.signup_username);
		etPassword = (EditText) findViewById(R.id.signup_password);
		etPasswordchk = (EditText) findViewById(R.id.signup_password_confirmation);
		etFirstName = (EditText) findViewById(R.id.signup_first_name);
		etLastName = (EditText) findViewById(R.id.signup_last_name);
		etEmail = (EditText) findViewById(R.id.signup_email);
		etPhone = (EditText) findViewById(R.id.signup_phone);

		
		//creating databaseManager object;
		try{
			dbM = new UserDatabaseManager(SignupActivity.this);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		user = (UserRecord) getIntent().getSerializableExtra(
				"com.gharseldin.taskorganize.user");
		wrap = (SettingWrapper) getIntent().getSerializableExtra(
				"com.gharseldin.taskorganize.settings");

		


		if (user != null) {
			etUsername.setFocusable(false);
			etUsername.setFocusableInTouchMode(false);
			etUsername.setClickable(false);
			btnSignUp.setVisibility(View.GONE);
			btnClear.setVisibility(View.GONE);
			btnUpdate.setVisibility(View.VISIBLE);
			etUsername.setText(user.getUsername());
			etPassword.setText(user.getPassword());
			etPasswordchk.setText(user.getPassword());
			etFirstName.setText(user.getFirstName());
			etLastName.setText(user.getLastName());
			etEmail.setText(user.getEmail());
			etPhone.setText(user.getPhone());
		} else {
			btnSignUp.setVisibility(View.VISIBLE);
			btnClear.setVisibility(View.VISIBLE);
			btnUpdate.setVisibility(View.GONE);
		}

	}

	// Method called when Update is clicked
	public void update(View v) {

		
		//Lock username field, since user is not allowed to change username
		etUsername.setFocusable(false);
		etUsername.setFocusableInTouchMode(false);
		etUsername.setClickable(false);
		
		// Check passwords fields for mismatch and print error in red

		if (!(etPassword.getText().toString().equals(etPasswordchk.getText()
				.toString()))){
			
			Toast.makeText(SignupActivity.this, "Password Mismatch",
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		//if user is to be allowed to change username then try to fix the following. 
		//check to see if username was changed to be like one that already exists
//		
//		UserRecord dummyUser = new UserRecord(etUsername.getText().toString(), "dummy");
//		try{
//			dbM = new UserDatabaseManager(SignupActivity.this);
//			if (dbM.chkRecord(dummyUser)) {
//				Toast.makeText(SignupActivity.this,
//						"Username Already exists...", Toast.LENGTH_SHORT)
//						.show();
//				etUsername.selectAll();
//				return;
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}

		// Initialize the new values that might have changed

		user.setUsername(etUsername.getText().toString());
		user.setPassword(etPassword.getText().toString());
		user.setFirstName(etFirstName.getText().toString());
		user.setLastName(etLastName.getText().toString());
		user.setEmail(etEmail.getText().toString());
		user.setPhone(etPhone.getText().toString());

		try {
			dbM.openDB();
			dbM.updateRecord(user);
			Log.d("Update", "Record Updated Successfully");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// TODO then go to the MainActivity Page with the new user information
		Intent mainIntent = new Intent(SignupActivity.this, MainActivity.class);
		mainIntent.putExtra("com.gharseldin.taskorganize.user", user);
		mainIntent.putExtra("com.gharseldin.taskorganize.settings", wrap);
		startActivity(mainIntent);
	}

	// Method called when Sign Up button pressed
	public void signUp(View v) {

		if (!(etPassword.getText().toString().equals(etPasswordchk.getText()
				.toString()))){
			
			Toast.makeText(SignupActivity.this, "Password Mismatch",
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (etUsername.getText().toString().trim().length()==0){
			Toast.makeText(SignupActivity.this, "Invalid Username, Username cannot be empty",
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		//TODO
		//Implement validation of email address
		//"^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$"
		
		
		//TODO
		//Implement validation of phone number
		//"0-9"
		
		// initialize a UserRecord with the data from screen
		UserRecord user = new UserRecord();
		user.setUsername(etUsername.getText().toString().trim().toLowerCase());
		user.setPassword(etPassword.getText().toString().trim());
		user.setFirstName(etFirstName.getText().toString().trim());
		user.setLastName(etLastName.getText().toString().trim());
		user.setEmail(etEmail.getText().toString().trim());
		user.setPhone(etPhone.getText().toString().trim());

		try {
			
			try {
				/* Copying database from storage to device memory */
				if (!dbM.checkDataBase()) {
					dbM.createDataBase();
				}
				dbM.openDB();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Check uniqueness of record

			if (dbM.chkRecord(user)) {
				Toast.makeText(SignupActivity.this,
						"Username Already exists...", Toast.LENGTH_SHORT)
						.show();
				etUsername.selectAll();
				return;
			} else {
				dbM.insertRecord(user);
				// TODO Go to the Login screen and write a not that you have
				// successfully created the user and that you need to login now
				Toast.makeText(SignupActivity.this, "Successfully added...",
						Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Toast.makeText(SignupActivity.this, "Unable to add...",
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

		// Go to the login Screen
		Intent logoutIntent = new Intent(SignupActivity.this,
				SignInActivity.class);
		startActivity(logoutIntent);
	}

	// Method called when Clear button pressed
	public void clear(View v) {
		Log.d("Clear", "enetering the clear method");
		etUsername.setText("");
		Log.d("Clear", "Clear 1");
		etPassword.setText("");
		Log.d("Clear", "Clear 2");
		etPasswordchk.setText("");
		Log.d("Clear", "Clear 3");
		etFirstName.setText("");
		Log.d("Clear", "Clear 4");
		etLastName.setText("");
		Log.d("Clear", "Clear 5");
		etEmail.setText("");
		Log.d("Clear", "Clear 6");
		etPhone.setText("");
		Log.d("Clear", "Clear 7");


	}

}
