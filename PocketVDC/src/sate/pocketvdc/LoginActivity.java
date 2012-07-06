package sate.pocketvdc;

import java.util.ArrayList;
import java.util.List;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * This is the activity for feature 1 in the dashboard application. It displays
 * some text and provides a way to get back to the home activity.
 * 
 */

public class LoginActivity extends DashboardActivity {
	// getting regions from strings file
	private String[] regions;
	private Spinner gridSpinner;
	private Spinner regionSpinner;

	private List<Grid> gridArrayList;
	private static final String LOGIN_ACTIVITY_LOG_TAG = "Login Activity";
	private SharedPreferences settings;
	private boolean recievedSettings;
	private boolean getCheckedRemember;
	private String getUserNameValue;
	String getPasswordValue;
	private Editor myEditor;
	private EditText userText, passText;
	private static final String USERNAME_PREF = "username";
	private static final String PASS_PREF = "password";
	private String encryptedUserName, encryptedPassword;

	/**
	 * onCreate: This method is ran when the activity starts up. It builds
	 * things such as lists, adapters, encrypts strings, saves settings, and
	 * more.
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setTitleFromActivityLabel(R.id.title_text);

		userText = (EditText) findViewById(R.id.nameTextBox);
		passText = (EditText) findViewById(R.id.passwordTextBox);
		// ///////PUT VALUES FROM SHARED PREFERENCES IN
		// USERNAME AND PASSWORD TEXTBOXES
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		getUserNameValue = settings.getString(USERNAME_PREF, "");
		getPasswordValue = settings.getString(PASS_PREF, "");

		getCheckedRemember = settings.getBoolean("saveNamePasswordPref", false);

		if (getUserNameValue.length() != 0 && getPasswordValue.length() != 0
				&& getCheckedRemember == true) {
			// to display text we first need to get it from shared preferences
			// and then decrypt it.

			Toast.makeText(getApplicationContext(), getUserNameValue,
					Toast.LENGTH_LONG).show();
			try {
				userText.setText(Secure.decrypt(getUserNameValue));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				passText.setText(Secure.decrypt(getPasswordValue));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {

			settings.edit().remove("username").commit();
			settings.edit().remove("password").commit();
			userText.setText("");
			passText.setText("");
		}

		try {
			gridArrayList = (ArrayList<Grid>) Grid.readGrids(this);
		} catch (Exception e) {
			Log.e(LOGIN_ACTIVITY_LOG_TAG, "Failed to load grids");
			e.printStackTrace();
		}

		Button submitButton = (Button) findViewById(R.id.submitButton);
		Button cancelButton = (Button) findViewById(R.id.cancelButton);

		// getting arrays from strings file
		regions = getResources().getStringArray(R.array.regions_array);

		this.gridSpinner = (Spinner) findViewById(R.id.gridSpinner);
		this.regionSpinner = (Spinner) findViewById(R.id.regionSpinner);

		// creating adapters for both spinners
		final ArrayAdapter<Grid> dataAdapter = new ArrayAdapter<Grid>(this,
				android.R.layout.simple_spinner_item, gridArrayList);
		ArrayAdapter<String> regionAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, regions);

		// drop down layout style with radio button type.
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		regionAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapters to spinners
		gridSpinner.setAdapter(dataAdapter);
		regionSpinner.setAdapter(regionAdapter);

		/**
		 * The submitButton activity takes care of encrypting and saving the
		 * user's information in the prefs file. It will also check with the
		 * server if the information entered by the user is correct or not.
		 */
		submitButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent changeAdd = new Intent();
				setResult(RESULT_OK, changeAdd);

				if (userText.getText().toString().length() > 0
						&& passText.getText().toString().length() > 0) // TAKE
																		// CARE
																		// OF
																		// LISRVIEW/DROPDOWN
				{
					// getting value again because this is different from
					// initial onCreate().
					getUserNameValue = userText.getText().toString();
					getPasswordValue = passText.getText().toString();
					// get data from settings activity. Null is a produces
					// value.

					recievedSettings = settings.getBoolean("saveNamePasswordPref", false);

					if (recievedSettings == true) {
						myEditor = settings.edit();

						try {
							encryptedUserName = Secure
									.encrypt(getUserNameValue);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						try {
							encryptedPassword = Secure
									.encrypt(getPasswordValue);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						
						myEditor.putString("username", encryptedUserName);
						myEditor.putString("password", encryptedPassword);
						myEditor.commit();
					} else {
						// delete string if not checked.
						// don't put anything into textboxes at start.
						settings.edit().remove("username").commit();
						settings.edit().remove("password").commit();

					}

					Toast.makeText(getApplicationContext(), "Loading...",
							Toast.LENGTH_LONG).show();
					// make an intent to start the virtual world
					// activity..................like in addGridActivity/screen
					// switch!
					Intent goToInWorld = new Intent(view
							.getContext(),
							PocketVDCActivity.class);
					//goToEdit.putExtra(CASE_TO_EDIT,
						//	selectedGrid);
					startActivity(goToInWorld);
					// cancelled and went back to home screen

					

					finish();
				} else {
					Toast.makeText(getApplicationContext(),
							"Sorry, you have to complete all the fields",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		/**
		 * The gridSpinner onItemSelectedListener handles the user's clicks and
		 * gets what grid was selected so they can be transported to the virtual
		 * world.
		 */
		gridSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long rowId) {
				String g = gridSpinner.getItemAtPosition(position).toString();
				Toast.makeText(LoginActivity.this, g, Toast.LENGTH_SHORT)
						.show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});

		/**
		 * The regionSpinner onItemSelectedListener handles the user's touches
		 * and senses what region they select.
		 */
		regionSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long rowId) {
				String r = regionSpinner.getItemAtPosition(position).toString();
				Toast.makeText(LoginActivity.this, r, Toast.LENGTH_SHORT)
						.show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});

		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent changeAdd = new Intent();
				setResult(RESULT_OK, changeAdd);
				// cancelled and went back to home screen

				finish();
			}
		});

	}

} // end class
