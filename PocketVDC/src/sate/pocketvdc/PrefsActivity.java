package sate.pocketvdc;

import test.edge.opengles.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PrefsActivity extends PreferenceActivity
{


	@Override
	/**
	 * The onCreate method handles thing when starting this activity, 
	 * mainly display the activity_settings.xml.
	 */
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		//I know this method is deprecated, there's no easy way to do it that I've found
		//without using this method or possibly making the program incompatible with anything but 4.0
		addPreferencesFromResource(R.layout.activity_settings);
	}
}