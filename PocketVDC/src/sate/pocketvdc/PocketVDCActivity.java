package sate.pocketvdc;

import sate.pocketvdc.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ConfigurationInfo;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class PocketVDCActivity extends Activity
{
	// these variables are for showing/hiding the menu
	final int MENU_INVISIBLE = 0;
	final int MENU_INVENTORY = 1;
	final int MENU_MAP = 2;
	final int MENU_PLACES = 3;
	final int MENU_PEOPLE = 4;
	final int MENU_BUILD = 5;
	final int MENU_SETTINGS = 6;
	int menuStatus = MENU_INVISIBLE;
	Dialog dialog, keepTrackOfSettingDialog;
	AlertDialog.Builder alertDialog;
	private static final int GET_IMAGE = 0;
	private static final int GET_DIALOG = 1;
	private static final String OBJECT_FROM_SDCARD = "back";
	private static final String HINTS_PREF_VAL = "hintsPref";
	private static final String VOICE_PREF_VAL = "allowVoice";
	
	
	private SharedPreferences settings;
	private boolean getHintsVal, getAllowVoiceVal; 
	private Editor myEditor;

	String selected;
	ListView contextMenu;
	ImageView myImage;
	TextView heightText, widthText, densityText;
	EditText chatBox;
	Button enterText;
	ToggleButton chatButton;

	final int TELEPORT_DIALOG = 0;
	final int BACK_DIALOG = 1;

	/**
	 * This method creates the dialog to prompt the user whether or not they
	 * want to teleport to a certain location.
	 * 
	 * @param id
	 *            : id is the integer parameter passed into the onCreateDialog
	 *            method.
	 */
	protected Dialog onCreateDialog(int id)
	{
		Dialog dialog;
		switch (id)
		{
		case TELEPORT_DIALOG:
			final AlertDialog.Builder teleportBuilder = new AlertDialog.Builder(this);
			teleportBuilder.setMessage("Teleport to this location?");
			teleportBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					// teleport code
					dialog.dismiss();
				}
			});
			teleportBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					dialog.cancel();
				}
			});
			dialog = teleportBuilder.create();
			break;
		case BACK_DIALOG:
			final AlertDialog.Builder backBuilder = new AlertDialog.Builder(this);
			backBuilder.setMessage("This action will log you out of the virtual world.  Are you sure you want to continue?");
			backBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					dialog.dismiss();
					// Save objects, edited prims, etc??????

					finish();
				}
			});
			backBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					dialog.cancel();
				}
			});
			dialog = backBuilder.create();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

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
		inflater.inflate(R.menu.main_world_menu, menu);
		return true;
	}

	/**
	 * This method handles the user's touches when certain items are selected
	 * from the menu inworld. It displays the proper choices as well as using
	 * fragments.
	 * 
	 * @param item
	 *            : The parameter of type MenuItem is passed in to make the
	 *            submenu items functional.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		final ListView contextMenu = (ListView) findViewById(R.id.menu);
		
		
		// Handle item selection
		switch (item.getItemId())
		{
		case R.id.inventory:
			// open inventory
			if (menuStatus == MENU_INVENTORY)
			{
				menuStatus = MENU_INVISIBLE;
				contextMenu.setVisibility(View.INVISIBLE);
			} else
			{
				//handle deeper nested values
				final OnItemClickListener backAction = new OnItemClickListener()
				{

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
					{
						if(position == 0)
						{
							Toast.makeText(getApplicationContext(), "hey back", Toast.LENGTH_SHORT).show();
						
							menuStatus = MENU_INVENTORY;
							String[] shiftBack = getResources().getStringArray(R.array.inventory);
							contextMenu.setAdapter(new ArrayAdapter<String>(PocketVDCActivity.this, android.R.layout.simple_list_item_1, shiftBack));
							contextMenu.setVisibility(View.VISIBLE);
							
						   //set listenerback
							//contextMenu.setOnItemClickListener(backAction);
							//SEPARATE LISTENER TO HANDLE REGULAR INVENTORY ACTION!
							
						   
						}
						
					}

				};
				
				menuStatus = MENU_INVENTORY;
				final String[] inventory = getResources().getStringArray(
						R.array.inventory);
				contextMenu.setAdapter(new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_1, inventory));
				contextMenu.setVisibility(View.VISIBLE);
				
				contextMenu.setOnItemClickListener(new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id)
					{
						selected = (String) contextMenu.getItemAtPosition(position);
						if (selected.equals(inventory[0]))
						{
						
							
						
							
							String[] my_textures = getResources().getStringArray(
									R.array.my_textures);
						
							
							contextMenu.setAdapter(new ArrayAdapter<String>(
									getApplicationContext(), android.R.layout.simple_list_item_1,
									my_textures));
							
						//	contextMenu.setOnItemClickListener(backAction);
							
							  

						} else if (selected.equals(inventory[1]))
						{
							
							//ArrayList<String> opensim_textures = new ArrayList<String>(com.wglxy.example.dash1.R.array.opensim_textures);
							
							String[] opensim_textures = getResources().getStringArray(
									R.array.opensim_textures);
							contextMenu.setAdapter(new ArrayAdapter<String>(
									getApplicationContext(), android.R.layout.simple_list_item_1,
									opensim_textures));
						} else if (selected.equals(inventory[2]))
						{
							
							//ArrayList<String> primitives = new ArrayList<String>(com.wglxy.example.dash1.R.array.primitives);
							String[] primitives = getResources().getStringArray(
									R.array.primitives);
							contextMenu.setAdapter(new ArrayAdapter<String>(
									getApplicationContext(), android.R.layout.simple_list_item_1,
									primitives));
						}

					}
				});
			}
			return true;
		case R.id.map:
			// open map
			return true;
		case R.id.places:
			// open places
			if (menuStatus == MENU_PLACES)
			{
				menuStatus = MENU_INVISIBLE;
				contextMenu.setVisibility(View.INVISIBLE);
			} else
			{

				menuStatus = MENU_PLACES;
				final String[] places = getResources().getStringArray(
						R.array.places);
				contextMenu.setAdapter(new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_1, places));
				contextMenu.setVisibility(View.VISIBLE);
///////////USE SAME METHOD FOR INVENTORY!!!!!!!!!!!!!
				final OnItemClickListener teleport = new OnItemClickListener()
				{

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
					{
						showDialog(TELEPORT_DIALOG);
					}

				};

				contextMenu.setOnItemClickListener(new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id)
					{
						String selected = (String) contextMenu.getItemAtPosition(position);
						
						if (selected.equals(places[0]))
						{
							//ArrayList<String> places_favorites = new ArrayList<String>(com.wglxy.example.dash1.R.array.places_favorites);
						
							String[] places_favorites = getResources().getStringArray(
									R.array.places_favorites);
							contextMenu.setAdapter(new ArrayAdapter<String>(
									getApplicationContext(), android.R.layout.simple_list_item_1,
									places_favorites));
							contextMenu.setOnItemClickListener(teleport);
						}

						else if (selected.equals(places[1]))
						{
							//ArrayList<String> places_landmarks = new ArrayList<String>(com.wglxy.example.dash1.R.array.places_landmarks);
							String[] places_landmarks = getResources().getStringArray(
								R.array.places_landmarks);
							contextMenu.setAdapter(new ArrayAdapter<String>(
									getApplicationContext(), android.R.layout.simple_list_item_1,
									places_landmarks));
							contextMenu.setOnItemClickListener(teleport);
						} else if (selected.equals(places[2]))
						{
							//ArrayList<String> places_teleport_history = new ArrayList<String>(com.wglxy.example.dash1.R.array.places_teleport_history);
							String[] places_teleport_history = getResources().getStringArray(
									R.array.places_teleport_history);
							contextMenu.setAdapter(new ArrayAdapter<String>(
									getApplicationContext(), android.R.layout.simple_list_item_1,
									places_teleport_history));
							contextMenu.setOnItemClickListener(teleport);
						} else if (selected.equals(places[3]))
						{
							
							//ArrayList<String> places_inventory = new ArrayList<String>(com.wglxy.example.dash1.R.array.places_inventory);
							String[] places_inventory = getResources().getStringArray(
									R.array.places_inventory);
							contextMenu.setAdapter(new ArrayAdapter<String>(
									getApplicationContext(), android.R.layout.simple_list_item_1,
									places_inventory));
							contextMenu.setOnItemClickListener(teleport);
						} else if (selected.equals(places[4]))
						{
							//ArrayList<String> places_library = new ArrayList<String>(com.wglxy.example.dash1.R.array.places_library);
							String[] places_library = getResources().getStringArray(
									R.array.places_library);
							contextMenu.setAdapter(new ArrayAdapter<String>(
									getApplicationContext(), android.R.layout.simple_list_item_1,
									places_library));
							contextMenu.setOnItemClickListener(teleport);
						}
					}
				});
			}
			return true;
		case R.id.people:
			// open people
			return true;
		case R.id.build:
			// open build
			return true;
		case R.id.upload:
			// calling create dialog method.
			dialog = onCreateDialogImg(GET_DIALOG);
			dialog.show();
			
			
			return true;
			
			

		case R.id.settings:
			// calling create dialog method for settings dialog
			dialog = onCreateDialogOptionsWindow(GET_DIALOG);
			dialog.show();
			
			keepTrackOfSettingDialog = dialog;
			return true;
			
			
		default:
			return super.onOptionsItemSelected(item);
		}
	
		
	}

	// Hold reference to our GLSurfaceView
	private GLSurfaceView mGLSurfaceView;

	private Renderer mGLRenderer;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            : This Bundle type parameter is used to ensure the onCreate is
	 *            performed correctly.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(android.view.Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.main);

		// image view! changed on Tuesday July 3rd.
		myImage = (ImageView) findViewById(R.id.select);
		contextMenu = (ListView) findViewById(R.id.menu);
		chatBox = (EditText) findViewById(R.id.chatBox);
		enterText = (Button) findViewById(R.id.enterButton);
		chatButton = (ToggleButton) findViewById(R.id.chatButton);
		mGLSurfaceView = (GLSurfaceView) findViewById(R.id.gl_surface_view);
		
		chatButton.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				if(chatBox.getVisibility() == View.INVISIBLE)
				{
					chatBox.setVisibility(View.VISIBLE);
					enterText.setVisibility(View.VISIBLE);
				}
				else
				{
					chatBox.setVisibility(View.INVISIBLE);
					enterText.setVisibility(View.INVISIBLE);
				}
				
			}
		});
		
		enterText.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				if(chatBox.getText() != null)
				{
					//eventually this will be the code to actually send the message
					Toast.makeText(getApplicationContext(), chatBox.getText(), Toast.LENGTH_SHORT).show();
					chatBox.setText(null);
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Please enter some text.", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		//getting settings from Shared Preferences

		settings = PreferenceManager.getDefaultSharedPreferences(this);
		// Check if system supports OpenGL ES 2.0
		final ActivityManager activityManager = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
		final boolean supportsEs2 = true; //configurationInfo.reqGlEsVersion >= 0x20000;
		if (supportsEs2)
		{
			// Request an OpenGL ES 2.0 compatible context
			mGLSurfaceView.setEGLContextClientVersion(2);

			// Set the renderer to our demo renderer.
			mGLRenderer = new PocketVDCRenderer(this);
			mGLSurfaceView.setRenderer(this.mGLRenderer);

			mGLSurfaceView.setOnTouchListener(new OnTouchListener()
			{

				@Override
				public boolean onTouch(View v, MotionEvent event)
				{
					if (menuStatus != MENU_INVISIBLE)
					{
						
						contextMenu.setVisibility(View.INVISIBLE);
						menuStatus = MENU_INVISIBLE;
						
					}
						
						
						
					 else
					{
						((OnTouchListener) mGLRenderer).onTouch(v, event);
						
					}
					
					return true;
				}

			});
		} else
		{
			// This is where you could create an OpenGL ES 1.x compatible
			// renderer if you wanted to support both ES 1 and ES 2.
			return;
		}

	}

	protected Dialog onCreateDialogOptionsWindow(int id)
	{
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.checksettings_inworld);
		dialog.setTitle(R.string.options_title);

		// When in dialog, remember dialog.findViewById.
		final CheckBox hintsCheckBox = (CheckBox) dialog.findViewById(R.id.hintsCheckBox);
		final CheckBox allowVoiceCheckBox = (CheckBox) dialog.findViewById(R.id.allowVoiceCheckBox);
		
		getHintsVal = settings.getBoolean(HINTS_PREF_VAL, false);
		getAllowVoiceVal = settings.getBoolean(VOICE_PREF_VAL, false);
		
		// setting myEditor to settings.edit(). This is to edit shared preferences.
		myEditor = settings.edit();
		
		if(getHintsVal == true)
		{
			hintsCheckBox.setChecked(true);
			
		}
		if(getAllowVoiceVal == true)
		{
			allowVoiceCheckBox.setChecked(true);
		}
		
		//handle changes user makes in world to hints.
		hintsCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) 
			{
				
				if(hintsCheckBox.isChecked() == true)
				{
					
					myEditor.putBoolean(HINTS_PREF_VAL, true);
					myEditor.commit();
				
				}
				else
				{
					myEditor.putBoolean(VOICE_PREF_VAL, false);
					myEditor.commit();
					
				}
				Toast.makeText(getApplicationContext(), "Change Updated...", Toast.LENGTH_SHORT).show();
				
			}
			
		});
		
		//handle changes user makes in world to allow voice.
		allowVoiceCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) 
			{
				
				if(allowVoiceCheckBox.isChecked() == true)
				{
					myEditor.putBoolean(VOICE_PREF_VAL, true);
					myEditor.commit();
				}
				else
				{
					myEditor.putBoolean(VOICE_PREF_VAL, false);
					myEditor.commit();
				}
				Toast.makeText(getApplicationContext(), "Change Updated...", Toast.LENGTH_SHORT).show();
				
			}
		});
		
		return dialog;

	}
	/**
	 * This method sets up the custom dialog through XML, and then handles the
	 * onClick events to make sure the image was selected.
	 * 
	 * @param: The id parameter is passed in from the calling action.
	 */
	protected Dialog onCreateDialogImg(int id)
	{
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.custlayout);
		dialog.setTitle(R.string.upload_image);

		ImageButton sdImage = (ImageButton) dialog.findViewById(R.id.sdImageButton);
		sdImage.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent goToSD = new Intent(v.getContext(), Sdcard.class);
				startActivityForResult(goToSD, GET_IMAGE);

			}
		});
		return dialog;

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
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		// getting data back from EditGridActivity
		if ((resultCode == Activity.RESULT_OK))
		{
			// returned grid obj should be grid user selected and edited.
			Bundle getIt = intent.getExtras();
			final Bitmap returnedImageObject = getIt.getParcelable(OBJECT_FROM_SDCARD);
			Toast.makeText(getApplicationContext(),
					"here is the image we selected" + returnedImageObject, Toast.LENGTH_LONG)
					.show();
			// myImage.setImageBitmap(returnedImageObject);

			dialog.dismiss();

			// have the application run a 3 second pause for the user to see
			// their
			// selected image.

			myImage.setImageBitmap(returnedImageObject);
			// PREVIEW 3 SECONDS HERE ...
			Handler handler = new Handler();
			handler.postDelayed(new Runnable()
			{
				public void run()
				{
					myImage.setVisibility(View.GONE);

				}
			}, 3000);
			myImage.setVisibility(View.VISIBLE);

		}

		else
		{
			Toast.makeText(getApplicationContext(), "Images not loaded properly.",
					Toast.LENGTH_SHORT).show();
			dialog.dismiss();
		}

	}

	/**
	 * This method handles the user's click for the onButton.
	 * 
	 * @param v
	 *            : This parameter deals with the creation of rendering the
	 *            correct view/layout objects.
	 */
	public void onButtonClicked(View v)
	{
		// Make a toast when button is clicked
		// Toast.makeText(PocketVDCActivity.this, "Button clicked",
		// Toast.LENGTH_SHORT).show();
		((PocketVDCRenderer) mGLRenderer).clearObjects();
	}

	@Override
	protected void onResume()
	{
		// The activity must call the GL surface view's onResume() on activity
		// onResume().
		super.onResume();
		this.mGLSurfaceView.onResume();
		contextMenu = (ListView) findViewById(R.id.menu);

	}

	/**
	 * The activity's onPause method.
	 */
	@Override
	protected void onPause()
	{
		// The activity must call the GL surface view's onPause() on activity
		// onPause().
		super.onPause();
		this.mGLSurfaceView.onPause();

	}

	/**
	 * Handles the functionality of the back button, if a menu is visible the
	 * back button should close it, otherwise if it is sending the user out of
	 * the main virtual world screen it should make a popup asking if they want
	 * to leave menu status == MENU_INVENTORY
	 */
	public void onBackPressed()
	{
		if(contextMenu.getVisibility() == View.VISIBLE)
		{
			contextMenu.setVisibility(View.INVISIBLE);
			menuStatus = MENU_INVISIBLE;
		}
		
		else
		{
			dialog = onCreateDialog(BACK_DIALOG);
			dialog.show();
		}
	}
	
}

/**
 * This class handles and creates the surface view of the inworld. It also
 * ensures that the context of the application is correct.
 * 
 */
class LessonOneSurfaceView extends GLSurfaceView // implements
// View.OnTouchListener
{

	public LessonOneSurfaceView(Context context)
	{
		super(context);
	}

	public LessonOneSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
}
