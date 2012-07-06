package sate.pocketvdc;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MediaActivity extends Activity {
	// creating variables dialog, alertDialog, and constants instead of
	// literal strings.

	Dialog dialog;
	AlertDialog.Builder alertDialog;
	private static final int GET_IMAGE = 0;
	private static final int GET_DIALOG = 1;
	private static final String OBJECT_FROM_SDCARD = "back";
	private static final String IMAGE_WIDTH = "Image Width";
	private static final String IMAGE_HEIGHT = "Image Height";
	private static final String IMAGE_DENSITY = "Image Density";
	ImageView myImage;
	TextView heightText, widthText, densityText;

	/**
	 * This method handles the startup of the application, deals with setting
	 * image information, height width, and more. It also displays a dialog and
	 * asks the user to get images from the gallery.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.media);
	
		
		
		Button uploadImage = (Button) findViewById(R.id.uploadButton);
		Button startApp = (Button) findViewById(R.id.startButton);
		myImage = (ImageView) findViewById(R.id.select);
		heightText = (TextView) findViewById(R.id.imageHeight);
		widthText = (TextView) findViewById(R.id.imageWidth);
		densityText = (TextView) findViewById(R.id.imageDensity);

		// set textviews to transparent by using the integer color 0.
		heightText.setTextColor(0);
		widthText.setTextColor(0);
		densityText.setTextColor(0);

		/**
		 * A method from a button to simulate an upload into the virtual world.
		 */
		uploadImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Here it is ",
						Toast.LENGTH_SHORT).show();

			}
		});

		/**
		 * The startApp method handles the button action that pops up the
		 * dialog. It calls the onCreateDialog method to setup the custom xml
		 * layout.
		 */
		startApp.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View view) {

				dialog = onCreateDialog(GET_DIALOG);
				dialog.show();

			}
		});

	}

	/**
	 * This method handles the activity results from the Sdcard.java class. It
	 * handles the passed object from the SD card, sets the image
	 * specifications, and displays it in the center of the screen.
	 * 
	 * @param: The requestCode parameter is returned from the Sdcard.java.
	 * @param: The resultCode parameter is returned after the result of the
	 *         action.
	 * @param: The intent parameter is the specific intent being used.
	 */
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		// getting data back from EditGridActivity
		if ((resultCode == Activity.RESULT_OK)) {
			// returned grid obj should be grid user selected and edited.
			Bundle getIt = intent.getExtras();
			final Bitmap returnedImageObject = getIt
					.getParcelable(OBJECT_FROM_SDCARD);
			Toast.makeText(getApplicationContext(),
					"here is the image we selected" + returnedImageObject,
					Toast.LENGTH_LONG).show();
			myImage.setImageBitmap(returnedImageObject);

			// changing text from transparent to white.
			heightText.setTextColor(-1);
			widthText.setTextColor(-1);
			densityText.setTextColor(-1);
			heightText.setText(IMAGE_HEIGHT + "= "
					+ returnedImageObject.getHeight() + ".");
			widthText.setText(IMAGE_WIDTH + "= "
					+ returnedImageObject.getWidth() + ".");
			densityText.setText(IMAGE_DENSITY + "= "
					+ returnedImageObject.getDensity() + ".");
			
			dialog.dismiss();
			
			// have the application sleep to pause image view
			Handler handler  = new Handler();
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					myImage.setImageBitmap(returnedImageObject);
					
				}
				
			}, 2000);
			
		myImage.setVisibility(View.GONE);
			
		}
		else
		{
			Toast.makeText(getApplicationContext(), "Images not loaded properly.", Toast.LENGTH_SHORT).show();
			dialog.dismiss();
			
		}
	}

	/**
	 * This method sets up the custom dialog through XML, and then handles the
	 * onClick events to make sure the image was selected.
	 * 
	 * @param: The id parameter is passed in from the calling action.
	 */
	protected Dialog onCreateDialog(int id) {
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.custlayout);
		dialog.setTitle(R.string.upload_image);

		ImageButton sdImage = (ImageButton) dialog
				.findViewById(R.id.sdImageButton);
		sdImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent goToSD = new Intent(v.getContext(), Sdcard.class);
				startActivityForResult(goToSD, GET_IMAGE);

			}
		});
		return dialog;

	}

}