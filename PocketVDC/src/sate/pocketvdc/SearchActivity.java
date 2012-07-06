

package sate.pocketvdc;



/**
 * This is the Search activity in the dashboard application.
 * It displays some text and provides a way to get back to the home activity.
 *
 */

public class SearchActivity extends DashboardActivity 
{
	String data;

	/**
	 * onCreate
	 *
	 * Called when the activity is first created. 
	 * This is where you should do all of your normal static set up: create views, bind data to lists, etc. 
	 * This method also provides you with a Bundle containing the activity's previously frozen state, if there was one.
	 * 
	 * Always followed by onStart().
	 *
	 * @param savedInstanceState Bundle
	 */
/*
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView (R.layout.activity_search);
		setTitleFromActivityLabel (R.id.title_text);

		//get data from settings activity 
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

		//in the method getString "list" represents the date from
		//Times new Roman string value is a produces type value.
		String s = settings.getString("list", "Times New Roman");

		//this is just for the test.
		android.util.Log.e("The item selected is: ", s);

		Toast.makeText(getApplicationContext(), 
				s, Toast.LENGTH_SHORT).show();
		FileInputStream fin = null;
		InputStreamReader isr = null;

		char[] inputBuffer = new char[255];

		String data = null;

		data = ReadSettings(getApplicationContext());

	}
	*/

} // end class
