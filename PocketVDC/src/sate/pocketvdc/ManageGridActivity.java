package sate.pocketvdc;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

//save in application specific space....

public class ManageGridActivity extends DashboardActivity
{

	private List<Grid> grids = null;
	private ListView listView = null;
	private Grid selectedGrid = null;
	// declaring request information
	private static final int DATA_FROM_EDIT_ACTIVITY = 2;
	private static final int DATA_FROM_ADD_ACTIVITY = 1;
	private static final int RETURN_TO_WINDOW = 0;
	// log tag
	private static final String MANAGE_GRID_ACTIVITY_LOG_TAG = "Manage Grid Activity";
	private static final String GET_BACK_OBJECT = "back";
	private static final String ADDED_A_GRID = "Added a Grid";
	private static final String CASE_TO_EDIT = "grid";

	// creates the menu on the action bar
	/**
	 * This method creates the action bar at the top of the virtual world. It
	 * makes use of the XML layout in the Menu folder in the project directory.
	 * 
	 * @param menu
	 *            : The type of parameter the is passed in during the creation
	 *            of the menu itself.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home_screen_menu, menu);
		return true;
	}

	/**
	 * The onCreate method starts the activity and sets up various items such as
	 * the listview and other objects.
	 */
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_grid);
		setTitleFromActivityLabel(R.id.title_text);

		try
		{
			this.grids = Grid.readGrids(this);
		} catch (Exception e1)
		{
			Log.e(MANAGE_GRID_ACTIVITY_LOG_TAG, "Failed to load grids");
			e1.printStackTrace();
		}

		listView = (ListView) findViewById(R.id.listView2);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		final ArrayAdapter<Grid> myListAdapter = new ArrayAdapter<Grid>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, this.grids);

		listView.setAdapter(myListAdapter);
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, final int position,
					long id)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(ManageGridActivity.this);

				// Move this title to the string xml
				builder.setTitle(R.string.builder_Title);

				// using the variable runTimeName to keep track of user choice
				// to modify menu.
				final String runTimeName = listView.getItemAtPosition(position).toString();

				selectedGrid = (Grid) listView.getItemAtPosition(position);

				// Build option list, get runTimeName to display friendly output
				// of
				// Add Grid#, Edit Grid#.
				final ListView modeList = new ListView(ManageGridActivity.this);
				final String[] stringArray = new String[] { "Edit " + runTimeName,
						"Delete " + runTimeName };
				final ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(
						ManageGridActivity.this, android.R.layout.simple_list_item_1,
						android.R.id.text1, stringArray);
				modeList.setAdapter(modeAdapter);

				/**
				 * The builder setItems method sets Items and displays a the
				 * alert dialog for the user to make their choice.
				 */
				builder.setItems(stringArray, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						Toast.makeText(getApplicationContext(), stringArray[which] + which,
								Toast.LENGTH_SHORT).show();

						// Be careful with the hardcoding of cases
						switch (which)
						{
						case 0:
							Intent goToEdit = new Intent(view.getContext(), EditGridActivity.class);
							goToEdit.putExtra(CASE_TO_EDIT, selectedGrid);

							dialog.dismiss();

							startActivityForResult(goToEdit, DATA_FROM_EDIT_ACTIVITY);

							break;
						case 1:
							// update the list view and array list now
							// that the user had edited their data grid.
							// remove original, update with newest.
							grids.remove(selectedGrid);
							((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

							myListAdapter.notifyDataSetChanged();

							// call method write grids to write a text
							// file to internal!
							Grid.writeGrids(grids, ManageGridActivity.this);

							dialog.dismiss();

							break;
						default:
							break;
						}

					}
				});
				Dialog dialog = builder.create();

				dialog.show();
			}

		});

		Button addButton = (Button) findViewById(R.id.addGridButton);
		/**
		 * The addButton setOnClickListener handles the user click on the add
		 * button. From here a startActivityForResult is sent to the
		 * AddGridActivity.
		 */
		addButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{
				Intent addIntent = new Intent(ManageGridActivity.this, AddGridActivity.class);
				startActivityForResult(addIntent, DATA_FROM_ADD_ACTIVITY);
			}
		});
	}

	@Override
	/**
	 * Here the onActivityResult method handles the responses of the edit grid and
	 * add grid screens. This also does this by checking whether the result returned is
	 * good.
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		// getting data back from EditGridActivity
		if ((resultCode == Activity.RESULT_OK) && (requestCode == DATA_FROM_EDIT_ACTIVITY))
		{
			// returned grid obj should be grid user selected and edited.
			Bundle getIt = intent.getExtras();
			Grid returnedGridObject = getIt.getParcelable(GET_BACK_OBJECT);

			// update the list view and array list now that the user had edited
			// their data grid.
			// remove original, update with newest.
			if (this.selectedGrid != null)
			{
				this.grids.remove(this.selectedGrid);
			}

			this.grids.add(returnedGridObject);

			((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

			Grid.writeGrids(this.grids, this);
		} else if ((resultCode == Activity.RESULT_CANCELED) && (requestCode == RETURN_TO_WINDOW))
		{
			this.setVisible(true);
			return;
		}

		// Add a grid
		if (resultCode == Activity.RESULT_OK && requestCode == DATA_FROM_ADD_ACTIVITY)
		{
			Bundle getAddGrid = intent.getExtras();
			Grid newAddedGrid = getAddGrid.getParcelable(ADDED_A_GRID);
			this.grids.add(newAddedGrid);
			((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

			Grid.writeGrids(this.grids, this);
		}
	}
} // end class
