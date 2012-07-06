package sate.pocketvdc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditGridActivity extends DashboardActivity 
{
	@Override
	/**
	 * The onCreate method sets up various items including creating
	 * variables such as, gridNickName, gridName, etc. It also gets the 
	 * selected object's values to display in the textboxes.
	 */
	public void onCreate(Bundle savedInstanceState) 
	{
		// getting object's properties from other activity, F2Activity...
		final Grid selectedGrid = (Grid) getIntent().getParcelableExtra("grid");
		setResult(Activity.RESULT_CANCELED);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_grid);
		Button saveButton = (Button) findViewById(R.id.saveButton);
		Button cancelButton = (Button) findViewById(R.id.cancelButton);
		final EditText gridNickName = (EditText) findViewById(R.id.gridNickNameTextBox);
		final EditText gridName = (EditText) findViewById(R.id.gridNameTextBox);
		final EditText firstName = (EditText) findViewById(R.id.firstNameTextBox);
		final EditText lastName = (EditText) findViewById(R.id.lastNameTextBox);
		final EditText password = (EditText) findViewById(R.id.passwordTextBox);
		final EditText loginURI = (EditText) findViewById(R.id.loginURITextBox);

		gridNickName.setText(selectedGrid.getGridNickName());
		gridName.setText(selectedGrid.getGridName());
		firstName.setText(selectedGrid.getFirstName());
		lastName.setText(selectedGrid.getLastName());
		password.setText(selectedGrid.getPassword());
		loginURI.setText(selectedGrid.getLoginURI());

		/**
		 * The cancelButton setOnClickListener handles the cancelButton click.
		 * The user is sent to the previous screen.
		 */
		cancelButton.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View view) 
			{
				Intent changeAdd = new Intent();
				setResult(RESULT_CANCELED, changeAdd);
				// cancelled and went back to home screen

				finish();
			}
		});

		/**
		 * After making sure there is actually text in the texboxes, the new
		 * entered information is switched out with the old.
		 */
		saveButton.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View view)
			{

				if (gridNickName.getText().length() > 0
						&& gridName.getText().length() > 0
						&& firstName.getText().length() > 0
						&& lastName.getText().length() > 0
						&& password.getText().length() > 0
						&& loginURI.getText().length() > 0) {
					
					selectedGrid.setGridNickName(gridNickName.getText()
							.toString());
					selectedGrid.setGridName(gridName.getText().toString());
					selectedGrid.setFirstName(firstName.getText().toString());
					selectedGrid.setLastName(lastName.getText().toString());
					selectedGrid.setPassword(password.getText().toString());
					selectedGrid.setLoginURI(loginURI.getText().toString());

					Toast.makeText(getApplicationContext(),
							"Your Grid Has Been Successfully Updated.",
							Toast.LENGTH_SHORT);

					Intent goBack = new Intent(getApplicationContext(),
							ManageGridActivity.class);
					goBack.putExtra("back", selectedGrid);
					setResult(Activity.RESULT_OK, goBack);

					finish();
					// goes back to previous screen.
				} 
				else
				{
					Toast.makeText(getApplicationContext(),
							"Sorry, all fields must be filled information.",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}
