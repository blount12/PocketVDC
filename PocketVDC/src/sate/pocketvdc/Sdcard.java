package sate.pocketvdc;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

public class Sdcard extends Activity

{
	ImageView imageOutput;
	private static final int SELECT_PHOTO = 100;
	private static final String BACK_TO_MEDIA_ACT = "back";
	private static final String IMAGE_TYPE = "image/*";
	final int REQUIRED_SIZE = 70;
	Bitmap yourSelectedImage, smallerImage, orientationImage;
	Uri selectedImage;
	static InputStream input;
	Bitmap resizedBitmap;

	/**
	 * This method is started when the application is created and sets up the
	 * method in which the user selects a picture from gallery.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sdcard);

		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType(IMAGE_TYPE);

		startActivityForResult(photoPickerIntent, SELECT_PHOTO);
	}

	/**
	 * This method handles the result of activities when making use of
	 * "gallery."
	 * 
	 * @param: The requestCode parameter is returned from the "gallery."
	 * @param: The resultCode parameter is returned after the result of the
	 *         action.
	 * @param: The intent parameter is the specific intent being used when
	 *         interacting with the "gallery."
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch (requestCode) {
		case SELECT_PHOTO:
			if (resultCode == RESULT_OK) {
				selectedImage = imageReturnedIntent.getData();
				InputStream imageStream = null;
				try {
					imageStream = getContentResolver().openInputStream(
							selectedImage);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// @SuppressWarnings("unused")
				// LinearLayout layout = (LinearLayout)
				// findViewById(R.id.layout_root);

				// using bitmap out of memory fix/////////////////////////

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 8;

				yourSelectedImage = BitmapFactory.decodeStream(imageStream,
						null, options);

				// yourSelectedImage = BitmapFactory.decodeStream(imageStream);
				/*
				 * imageOutput = new ImageView(this); LinearLayout.LayoutParams
				 * params = new LinearLayout.LayoutParams(
				 * LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
				 * imageOutput.setLayoutParams(params);
				 * imageOutput.setScaleType(ImageView.ScaleType.CENTER_CROP);
				 * imageOutput.setMaxHeight(50); imageOutput.setMaxWidth(50); /*
				 * try { yourSelectedImage = MediaStore.Images.Media.getBitmap(
				 * getContentResolver(), selectedImage); } catch
				 * (FileNotFoundException e) { // TODO Auto-generated catch
				 * block e.printStackTrace(); } catch (IOException e) { // TODO
				 * Auto-generated catch block e.printStackTrace(); }
				 */
				// after setting the images maxHeight and maxWidth, we set the
				// chosen bitmap
				// to the image view called imageOutput.
				// CHANGE 2:11
				// imageOutput.setImageBitmap(yourSelectedImage);

				if (yourSelectedImage != null) {
					smallerImage = scaleDownBitmap(yourSelectedImage, 200, this);
					Matrix matrix = new Matrix();
					float rotation = rotationForImage(this, selectedImage);
					if (rotation != 0f) {
						matrix.preRotate(rotation);

					}

					resizedBitmap = Bitmap.createBitmap(smallerImage, 0, 0,
							smallerImage.getWidth(), smallerImage.getHeight(),
							matrix, true);

					// CHANGED Tuesday afternoon 1:49PM: from mediaact to pocketinworld!
					Intent goBackToPocketVDCScreen = new Intent(
							getApplicationContext(), PocketVDCActivity.class);
					goBackToPocketVDCScreen.putExtra(BACK_TO_MEDIA_ACT,
							resizedBitmap);
					setResult(Activity.RESULT_OK, goBackToPocketVDCScreen);

				} //end of if statement
				else {
					Toast.makeText(getApplicationContext(),
							"Your images could not be loaded.",
							Toast.LENGTH_SHORT).show();
				}//end of else concept.
				uploadToServer(smallerImage);
				/*
				 * try { imageStream.close(); } catch (IOException e) { // TODO
				 * Auto-generated catch block e.printStackTrace(); }
				 */

				finish();

			}
		}

	}

	/**
	 * This method scales down bitmaps by giving a new height and width by
	 * dividing its image properties.
	 * 
	 * @param photo
	 *            : The actual bitmap that is passed in to scaleDownBitmap.
	 * @param newHeight
	 *            : The parameter getting passed in for the new height.
	 * @param context
	 *            : The current context in which this is happening.
	 * @return photo: The method returns a bitmap photo, now resized.
	 */
	public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight,
			Context context) {
		final float densityMultiplier = context.getResources()
				.getDisplayMetrics().density;

		int h = (int) (newHeight * densityMultiplier);
		int w = (int) (h * photo.getWidth() / ((double) photo.getHeight()));

		// change filter on bitmap? change to false...
		photo = Bitmap.createScaledBitmap(photo, w, h, true);

		return photo;
	}

	/**
	 * This method takes the context, image uri and recognizes its rotational
	 * layout. It also gets the image's exif information to figure out the fix.
	 * 
	 * @param context
	 *            : The activity context
	 * @param uri
	 *            : The uri of the image
	 * @return: returns the given float value.
	 */
	public static float rotationForImage(Context context, Uri uri) {
		if (uri.getScheme().equals("content")) {
			String[] projection = { Images.ImageColumns.ORIENTATION };
			Cursor c = context.getContentResolver().query(uri, projection,
					null, null, null);
			if (c.moveToFirst()) {
				return c.getInt(0);
			}
		} else if (uri.getScheme().equals("file")) {
			try {
				ExifInterface exif = new ExifInterface(uri.getPath());
				int rotation = (int) exifOrientationToDegrees(exif
						.getAttributeInt(ExifInterface.TAG_ORIENTATION,
								ExifInterface.ORIENTATION_NORMAL));
				return rotation;
			} catch (IOException e) {

			}
		}
		return 0f;
	}

	/**
	 * The exifOrientationToDegrees method gets the exif information from the
	 * int rotation in the "rotationForImage" method. Depending on its
	 * orientation the image is rotated to the correct position.
	 * 
	 * @param exifOrientation
	 * @return: The return value of roated degrees.
	 */
	private static float exifOrientationToDegrees(int exifOrientation) {
		if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
			return 90;
		} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
			return 180;
		} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
			return 270;
		}
		return 0;
	}

	/**
	 * The method uploadToServer uploads the selected bitmap image to the server
	 * using Base64 to encode to a string.
	 * 
	 * @param image
	 *            : The selected bitmap being passed in as a parameter.
	 */

	private static void uploadToServer(Bitmap image) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 90, out);

		// declaring a byte array
		byte[] bytesOfArrayStream = out.toByteArray();

		String encodedImage = Base64.encodeToString(bytesOfArrayStream, 0);

		ArrayList<NameValuePair> nameVal = new ArrayList<NameValuePair>();
		nameVal.add(new BasicNameValuePair("image", encodedImage));
		// try to do server upload.
		try {
			HttpClient httpVirtual = new DefaultHttpClient();

			HttpPost httpVirtualPost = new HttpPost(
					"http://10.0.0.0:88/android/base.asp");
			httpVirtualPost.setEntity(new UrlEncodedFormEntity(nameVal));

			HttpResponse serverResponse = httpVirtual.execute(httpVirtualPost);

			HttpEntity serverEntity = serverResponse.getEntity();

			input = serverEntity.getContent();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		//resizedBitmap.recycle();
		//resizedBitmap = null;
		smallerImage.recycle();
		smallerImage = null;
		yourSelectedImage.recycle();
		yourSelectedImage = null;

	}
}
