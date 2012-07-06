package sate.pocketvdc;



import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;


public class JoystickActivity extends Activity 
{
	private Handler mHandler = new Handler();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joystick);
    }

    private Runnable mUpdateTextView = new Runnable() {
      public void run() {
        TextView textView = (TextView)findViewById(R.id.textView1);
        Joystick js = (Joystick)findViewById(R.id.joystick1);
        textView.setText("F: " + js.goingForward() + " B: " + js.goingBack() + " L: " + js.goingLeft() + " R: " + js.goingRight());
        mHandler.postDelayed(mUpdateTextView, 100);
      }
    };

    protected void onStart() 
    {
    	super.onStart();
      mHandler.postDelayed(mUpdateTextView, 100);
    }

}