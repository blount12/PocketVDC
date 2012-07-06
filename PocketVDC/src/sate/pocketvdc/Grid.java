package sate.pocketvdc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

import test.edge.opengles.R;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Grid implements Parcelable {

	private String gridNickName;
	private String gridName;
	private String firstName;
	private String lastName;
	private String password;
	private String loginURI;

	// Directory where data file is stored in application directory
	private static final String DATA_FILE_DIR = "grids";
	// Name of grid data file
	private static final String DATA_FILE_NAME = "griddata.txt";
	// Number of items in each grid line
	private static final int GRID_ITEMS_PER_LINE = 6;
	// delimiter of items in lines of grid data file
	private static final String GRID_ITEM_DELIMITER = ";";
	private static final String GRID_LOG_TAG = Grid.class.getPackage()
			.toString() + ".Grid";

	// constructor
	public Grid(String gridNickName, String gridName, String firstName,
			String lastName, String password, String loginURI) {
		// usual required parameters
		this.gridNickName = gridNickName;
		this.gridName = gridName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.loginURI = loginURI;

	}

	public void setGridNickName(String val) {
		gridNickName = val;
	}

	public String getGridNickName() {
		return gridNickName;
	}

	public void setGridName(String val) {
		gridName = val;
	}

	public String getGridName() {
		return gridName;
	}

	public void setPassword(String val) {
		password = val;
	}

	public String getPassword() {
		return password;
	}

	public void setFirstName(String val) {
		firstName = val;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setLastName(String val) {
		lastName = val;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLoginURI(String val) {
		loginURI = val;
	}

	public String getLoginURI() {
		return loginURI;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	/**
	 * This method writes to a destination by taking
	 * advantage of the Parceable class.
	 * @param dest: This is the destination.
	 * @param flag: 
	 */
	public void writeToParcel(Parcel dest, int flag) {
		// TODO Auto-generated method stub
		dest.writeString(gridNickName);
		dest.writeString(gridName);
		dest.writeString(firstName);
		dest.writeString(lastName);
		dest.writeString(password);
		dest.writeString(loginURI);
	}

	/**
	 * This method writes to a destination by taking advantage of the Parceable
	 * class.
	 * 
	 * @param in
	 *            : This is what is read and stores in the variable names.
	 */
	public Grid(Parcel in) {
		this.gridNickName = in.readString();
		this.gridName = in.readString();
		this.firstName = in.readString();
		this.lastName = in.readString();
		this.password = in.readString();
		this.loginURI = in.readString();
	}

	@SuppressWarnings({ "rawtypes" })
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Grid createFromParcel(Parcel in) {
			return new Grid(in);
		}

		public Grid[] newArray(int size) {
			return new Grid[size];
		}
	};

	@Override
	/**
	 * This method overrides toString to produce a better display
	 * output for the list view in the ManageGridActivity.
	 */
	public String toString() {
		return gridName;
	}

	/**
	 * 
	 * @return String containing grid information delimited by
	 *         GRID_ITEM_DELIMITER
	 */
	public String toDataString() {
		return this.getGridNickName() + GRID_ITEM_DELIMITER
				+ this.getGridName() + GRID_ITEM_DELIMITER
				+ this.getFirstName() + GRID_ITEM_DELIMITER
				+ this.getLastName() + GRID_ITEM_DELIMITER + this.getPassword()
				+ GRID_ITEM_DELIMITER + this.getLoginURI()
				+ GRID_ITEM_DELIMITER;
	}

	/**
	 * Loads grid information from file in application directory
	 * 
	 * @param cxt
	 * @return List of Grid objects loaded from file
	 * @throws Exception
	 */
	public static List<Grid> readGrids(Context cxt) throws Exception {

		// Throw exception if context is null
		if (cxt == null) {
			// Change to illegal argument exception?
			throw new Exception(
					"Error in Grid.readGrids()> Passed context is null.");
		}

		List<Grid> result = new ArrayList<Grid>();

		File dir = cxt.getDir(DATA_FILE_DIR, Context.MODE_PRIVATE);

		File dataFile = new File(dir, DATA_FILE_NAME);

		// Check if file exists
		if (!dataFile.exists()) {
			// If it does not exist, then load from default
			Grid.loadDefaultGrids(cxt, dataFile);
		}

		if (!dataFile.canRead()) {
			// Change to IOException?
			throw new Exception(
					"Error in Grid.readGrids()> Unable to read data file.");
		}

		// Create reader
		BufferedReader reader = new BufferedReader(new FileReader(dataFile));

		String line = "";

		while ((line = reader.readLine()) != null) {
			// split line by delimiter
			String[] splitLine = line.split(GRID_ITEM_DELIMITER);

			if (splitLine.length >= GRID_ITEMS_PER_LINE) {
				// enough values parsed to be valid
				result.add(new Grid(splitLine[0], splitLine[1], splitLine[2],
						splitLine[3], splitLine[4], splitLine[5]));
			}
		}

		reader.close();

		return result;
	}

	/**
	 * Writes default grid data from resources to grid data file in application
	 * directory
	 * 
	 * @throws IOException
	 */
	private static void loadDefaultGrids(Context cxt, File dataFile)
			throws IOException {
		// Grab input stream for default grid data file.
		InputStream input = cxt.getResources().openRawResource(R.raw.griddata);

		// Grab channels for transfer
		ReadableByteChannel in = Channels.newChannel(input);
		FileChannel out = new FileOutputStream(dataFile).getChannel();

		// The count is the max number of bytes to transfer, so just gave a
		// large number
		out.transferFrom(in, 0, 40000);

		// Close channels
		in.close();
		out.close();
	}

	public static void writeGrids(List<Grid> grids, Context context) {
		PrintWriter writer = null;

		try {
			File dir = context.getDir(DATA_FILE_DIR, Context.MODE_PRIVATE);
			Log.d("SEE", dir.getAbsolutePath());
			File dataFile = new File(dir, DATA_FILE_NAME);

			writer = new PrintWriter(dataFile);

			for (Grid grid : grids) {
				writer.println(grid.toDataString());
			}
			writer.flush();
			writer.close();

		} catch (Exception e) {
			Log.e(GRID_LOG_TAG, "Error: " + e.getMessage());
		}

		writer.close();
	}
}
