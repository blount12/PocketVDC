package sate.pocketvdc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class Joystick extends View 
{
	// -1 when screen is not being touched, changed in below methods
    float touchX = -1, touchY = -1;
    // get width and height of the joystick view
    float viewWidth, viewHeight;
    // buffer zone is how sensitive the joystick is to small motions
    int bigCircleRadius, smallCircleRadius, bufferZone;
    // variables for detecting joystick position
    boolean forward = false, back = false, left = false, right = false;

    // constructors
    public Joystick(Context context) 
    {
         super(context);
    }
    
    public Joystick(Context context, AttributeSet attrs) 
    { 
    	super( context, attrs );
    }
    	 
    public Joystick(Context context, AttributeSet attrs, int defStyle) 
    {
    	super( context, attrs, defStyle );
    }
    
    // getters for the joystick position
    public boolean goingForward()
    {
    	return forward;
    }
    
    public boolean goingBack()
    {
    	return back;
    }
    
    public boolean goingLeft()
    {
    	return left;
    }
    
    public boolean goingRight()
    {
    	return right;
    }

    @Override
    public void onDraw(Canvas canvas) 
    {
        Paint mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
        viewWidth = getWidth();
        viewHeight = getHeight();
        // outer circle will 1/6th less the size of the view it's in (for visibility and control)
        bigCircleRadius = (int) ((viewWidth/2)-viewWidth/6);
        // inner circle will be 1/3 the size of the outer circle
        smallCircleRadius = bigCircleRadius/3;
        // buffer zone will be 1/6 the width of the outer circle
        bufferZone = bigCircleRadius/6;
        // draw outer circle at the center of the view
        canvas.drawCircle(viewWidth/2, viewHeight/2, bigCircleRadius, mPaint);
        // draw inner circle
        if (touchX > -1 && touchY > -1) // if there is touch information
        {
            canvas.drawCircle(touchX, touchY, smallCircleRadius, mPaint);
        } 
        else // if there is no touch information
        {
            canvas.drawCircle(viewWidth/2, viewHeight/2, smallCircleRadius, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) 
    {
    	viewWidth = getWidth();
    	viewHeight = getHeight();
    	// if the touch is inside the large circle
    	if(Math.sqrt(Math.pow(Math.abs(me.getX() - viewWidth/2), 2) + Math.pow(Math.abs(me.getY() - viewHeight/2), 2)) <= bigCircleRadius)
    	{
    		switch (me.getAction()) 
    		{
    		case MotionEvent.ACTION_DOWN: // if there is a touch on the screen
    			touchX = me.getX();
    			touchY = me.getY();
    			// test for left/right/forward/back motion, with a buffer
    			if(touchX < viewWidth/2 - bufferZone)
    				left = true;
    			else if(touchX > viewWidth/2 + bufferZone)
    				right = true;
    			if(touchY < viewHeight/2 - bufferZone)
    				forward = true;
    			else if(touchY > viewHeight/2 + bufferZone)
    				back = true;
	        	invalidate();
	        	return true;
	        case MotionEvent.ACTION_MOVE: // if the user is dragging
	            touchX = me.getX();
	            touchY = me.getY();
	            // test for left/right/forward/back motion, with a buffer
    			if(touchX < viewWidth/2 - bufferZone)
    			{
    				left = true;
    				right = false;
    			}
    			else if(touchX > viewWidth/2 + bufferZone)
    			{
    				right = true;
    				left = false;
    			}
    			else
    			{
    				right = false;
    				left = false;
    			}
    			if(touchY < viewHeight/2 - bufferZone)
    			{
    				forward = true;
    				back = false;
    			}
    			else if(touchY > viewHeight/2 + bufferZone)
    			{
    				back = true;
    				forward = false;
    			}
    			else
    			{
    				back = false;
    				forward = false;
    			}
	            invalidate();
	            return true;
	        case MotionEvent.ACTION_UP: // the touch stops
	            touchX = -1;
	            touchY = -1;
	            left = false;
	            right = false;
	            forward = false;
	            back = false;
	            invalidate();
	            return true;
	        }
    	}
    	else if(me.getAction() == MotionEvent.ACTION_MOVE) // if user starts dragging inside the large circle and drags outside it
    	{
    		touchX = (float) (bigCircleRadius * (Math.abs(me.getX() - viewWidth/2)) / Math.sqrt(Math.pow(Math.abs(me.getX() - viewWidth/2), 2) + Math.pow(Math.abs(me.getY() - viewHeight/2), 2)));
    		touchY = (float) Math.sqrt(Math.pow(bigCircleRadius, 2) - Math.pow(touchX, 2));
    		if(me.getX() < viewWidth/2)
    			touchX = viewWidth/2 - touchX;
    		else
    			touchX += viewWidth/2;
    		if(me.getY() < viewHeight/2)
    			touchY = viewHeight/2 - touchY;
    		else
    			touchY += viewHeight/2;
    		// test for left/right/forward/back motion, with a buffer
    		if(touchX < viewWidth/2 - bufferZone)
			{
				left = true;
				right = false;
			}
			else if(touchX > viewWidth/2 + bufferZone)
			{
				right = true;
				left = false;
			}
			else
			{
				right = false;
				left = false;
			}
			if(touchY < viewHeight/2 - bufferZone)
			{
				forward = true;
				back = false;
			}
			else if(touchY > viewHeight/2 + bufferZone)
			{
				back = true;
				forward = false;
			}
			else
			{
				back = false;
				forward = false;
			}
    		invalidate();
    	}
    	else if(me.getAction() != MotionEvent.ACTION_MOVE) // if the touch event is outside of the circle and is not moving
    	{
    		touchX = -1;
    		touchY = -1;
    		left = false;
            right = false;
            forward = false;
            back = false;
    		invalidate();
    		return true;
    	}
		return false;
    }
}
