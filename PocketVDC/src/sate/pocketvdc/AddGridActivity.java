package sate.pocketvdc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddGridActivity extends DashboardActivity 
{

	/**
	 * onCreate method to create certain items when the activity starts up.
	 */
	private static final String INSTANTIATION_VALUE = "dummyVal";
	private static final String BACK_TO_MNGGRID = "Added a Grid";
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_grid);

		// adding buttons and text fields to get R.id values
		// I don't know if we need variables for the autocomplete stuff but I
		// went ahead and assigned them in case we do
		Button getInfo = (Button) findViewById(R.id.getGridInfoButton);
		Button clearInfo = (Button) findViewById(R.id.clearInfoButton);
		Button myAdd = (Button) findViewById(R.id.addButton);
		Button cancelButton = (Button) findViewById(R.id.cancelButton);
		final EditText gridNickName = (EditText) findViewById(R.id.gridNickNameTextBox);
		final EditText gridName = (EditText) findViewById(R.id.gridNameTextBox);
		final EditText firstName = (EditText) findViewById(R.id.firstNameTextBox);
		final EditText lastName = (EditText) findViewById(R.id.lastNameTextBox);
		final EditText password = (EditText) findViewById(R.id.passwordTextBox);
		final EditText loginURI = (EditText) findViewById(R.id.loginURITextBox);
		@SuppressWarnings("unused")
		final EditText loginPage = (EditText) findViewById(R.id.loginPageTextBox);
		@SuppressWarnings("unused")
		final EditText helperURL = (EditText) findViewById(R.id.helperURLTextBox);
		@SuppressWarnings("unused")
		final EditText website = (EditText) findViewById(R.id.websiteTextBox);
		@SuppressWarnings("unused")
		final EditText support = (EditText) findViewById(R.id.supportTextBox);
		@SuppressWarnings("unused")
		final EditText account = (EditText) findViewById(R.id.accountTextBox);
		@SuppressWarnings("unused")
		final EditText password2 = (EditText) findViewById(R.id.passwordTextBox2);
		@SuppressWarnings("unused")
		final EditText webSearch = (EditText) findViewById(R.id.webSearchTextBox);

		/**
		 * The getInfo senOnClickListener method handles the user clicks and if
		 * all the correct information is filled in a grid is added to the
		 * internal storage and the list on the previous page is updated.
		 */
		getInfo.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View view)
			{
				if (gridNickName.getText().toString().length() > 0
						&& gridName.getText().toString().length() > 0
						&& firstName.getText().toString().length() > 0
						&& lastName.getText().toString().length() > 0
						&& password.getText().toString().length() > 0
						&& loginURI.getText().toString().length() > 0) 
				{
					Toast.makeText(
							getApplicationContext(),
							"The fields below will populate with information from the\nserver for a given region",
							Toast.LENGTH_SHORT).show();

					Intent cancelBackToManageGrid = new Intent();
					setResult(RESULT_OK, cancelBackToManageGrid);
					// Intent to the virtual world activity?
					// TRY/CATCH FOR CONNECT TO SERVER?
					// DO I HAVE TO CHECK CONNECTION TO SERVER JUST IN
					// CASE????????
				} 
				else
				{
					Toast.makeText(getApplicationContext(),
							"Sorry, you have to complete all the fields",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		/**
		 * The clearInfo setOnClickListener method handles the clearing of text
		 * boxes upon user request.
		 */
		clearInfo.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View view) 
			{
				Intent cancelBackToManageGrid = new Intent();
				setResult(RESULT_OK, cancelBackToManageGrid);

				// clearing texts
				gridNickName.getText().clear();
				gridName.getText().clear();
				firstName.getText().clear();
				lastName.getText().clear();
				password.getText().clear();
				loginURI.getText().clear();
				gridNickName.requestFocus();
			}
		});

		/**
		 * The myAdd setOnClickListener method handles the user Add button. It
		 * adds a grid to the internal storage and updates the listview on the
		 * previous page.
		 */
		myAdd.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view) 
			{

				/*
				 * 
				 * putting extras in intent and then object is sent back to the
				 * F2Acitivity class to be written to output.txt and updated in
				 * the listView dynamically.
				 */
				// using dummy val for instantiation.....
				Grid myNewGrid = new Grid(INSTANTIATION_VALUE, INSTANTIATION_VALUE, INSTANTIATION_VALUE,
						INSTANTIATION_VALUE,INSTANTIATION_VALUE, INSTANTIATION_VALUE);

				myNewGrid.setGridNickName(gridNickName.getText().toString());
				myNewGrid.setGridName(gridName.getText().toString());
				myNewGrid.setFirstName(firstName.getText().toString());
				myNewGrid.setLastName(lastName.getText().toString());
				myNewGrid.setPassword(password.getText().toString());
				myNewGrid.setLoginURI(loginURI.getText().toString());

				Intent addGrid = new Intent();
				addGrid.putExtra(BACK_TO_MNGGRID, myNewGrid);
				setResult(Activity.RESULT_OK, addGrid);

				Toast.makeText(
						getApplicationContext(),
						"Grid successfully added!\nThis will be added to the existing list of grids",
						Toast.LENGTH_SHORT).show();

				finish();
			}
		});

		/**
		 * The cancelButton setOnClickListener handles the user click event for
		 * the cancel button. This exits the current screen and goes back to the
		 * previous one.
		 */
		cancelButton.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View view)
			{
				Intent cancelBackToManageGrid = new Intent();
				setResult(RESULT_OK, cancelBackToManageGrid);

				finish();
			}
		});
	}

	/**
	 * This method returns a new Grid when called. It is populated with the
	 * newly entered information.
	 * 
	 * @param myGrid
	 *            : myGrid is the object that houses values the user just
	 *            entered.
	 * @return This returns a new grid complete with the newly entered
	 *         information.
	 */
	static Grid NewGrid(Grid myGrid) 
	{
		return myGrid;
	}
}
