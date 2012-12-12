package com.locator.wlan.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;


/**
 * Extended view to react on user gestures
 * 
 * 
 * @author Nico Bleh
 * 
 * 
 */
public class TouchView extends View {
	
	//The Bitmap to display
	private Bitmap picture;
	
	//x and y position
    private float mDX;
    private float mDY;
    
    //x und y position des Zoommittelpunktes
    private float zoomX;
    private float zoomY;
    
    //Toggles touchdetection
    private boolean touchenabled = true;
    
    //Detector for scale
    private ScaleGestureDetector mScaleDetector;
    
    //Scale factor
    private float mScaleFactor = 1.f;
    
    //Detector for Movement
    private GestureDetector mGestureDetector;
    
    private boolean drawPoint;

	private float cy;

	private float cx;
	
	Paint paint; //TODO Sollte hier nicht sein. Jedes Element sollte sich selbst auf die Karte zeichnen

    //Konstruktor
    public TouchView(Context context) {
    	this(context, null, 0);
    	//Init
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mGestureDetector = new GestureDetector(context, new GestureListener());
        paint = new Paint();
        
    }
    
    public TouchView(Context context, AttributeSet attrs) {
    	this(context, attrs, 0);
    	mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mGestureDetector = new GestureDetector(context, new GestureListener());
        paint = new Paint();
    }
	
    public TouchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mGestureDetector = new GestureDetector(context, new GestureListener());
        picture = null;
        paint = new Paint();
    }

    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
    	//Checks if touchevents should be handled
    	if(touchenabled) {
    		// Let the ScaleGestureDetector inspect all events.
    		mScaleDetector.onTouchEvent(ev);
    		mGestureDetector.onTouchEvent(ev);
    	}
        return true;
    }
    
    /**
     * Change the current bitmap
     * 
     * @param bmp Bitmap to change to
     */
    public void setPicture(Bitmap bmp) {
    	picture = bmp;
    	invalidate(); //calls onDraw()
    }
    
    /**
     * Enables touch detection and handling
     */
    public void enableTouch() {
    	touchenabled = true;
    }
    
    /**
     * Disable touch detection and handling
     */
    public void disableTouch() {
    	touchenabled = false;
    }
    
    /**
     * getter for picture x
     * 
     * @return int current x value
     */
    public int getPictureX() {
    	return  (int) (mDX);
    }
    
    /**
     * getter for picture y
     * 
     * @return int current y value
     */
    public int getPictureY() {
    	return  (int) (mDY);
    }
    
    /**
     * getter for picture width
     * 
     * @return int bitmap width
     */
    public int getPictureWidth() {
    	return (int) (picture.getWidth() * mScaleFactor);
    }
    
    /**
     * getter for picture height
     * 
     * @return int bitmap height
     */
    public int getPictureHeight() {
    	return (int) (picture.getHeight() * mScaleFactor);
    }
    
    /**
     * getter for picture scale factor
     * 
     * @return float current scale factor
     */
    public float getPictureScaleFactor() {
    	return mScaleFactor;
    }
    
    /**
     * resetting the canvas values
     */
    public void reset() {
    	mDX = 0;
    	mDY = 0;
    	mScaleFactor = 1.f;
    	invalidate();
    }
    
    /**
     * set the canvas to desired position
     * 
     * @param mDX float x value
     * @param mDY float y value
     */
    public void positionCanvas(float mDX, float mDY) {
    	this.mDX = mDX;
    	this.mDY = mDY;
    	invalidate();
    }
    
    @Override
    public void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
    	
    	if(picture != null) { //apply the scaleFactor and movement values to the canvas
    		canvas.save();
    		
			canvas.scale(mScaleFactor, mScaleFactor, zoomX, zoomY);
    		canvas.translate(mDX, mDY);
    		
    		canvas.drawBitmap(picture, 0, 0, null); //draw bitmap on canvas
    		
    		if(drawPoint) { //TODO 
    	        paint.setColor(Color.RED); 
				canvas.drawCircle(-cx, -cy, (float) 10, paint);
			}
			
    		canvas.restore();
    	}
    }
    
    public void drawPoint(float[] xy) {
    	this.drawPoint = true;
    	this.cx = xy[0];
    	this.cy = xy[1];
    }
    
    public void noDrawPoint() {
    	drawPoint = false;
    }
    
    /**
     * Handles Scaleevents
     * 
     * @author Nico Bleh
     *
     */
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    	
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor(); //Set new scale factor
            
            zoomX = detector.getFocusX();
            zoomY = detector.getFocusY();
            
            /*change x and y of the Bitmap to keep it in place while scaling
            final float x = detector.getCurrentSpan() - detector.getPreviousSpan();
            mDX -= x;
            mDY -= x;
            */
            
            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
            
         
            
            invalidate(); //calls onDraw()
            return true;
        }
    }
    
    /**
     * Handle move events
     * 
     * @author Nico Bleh
     *
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
    	
    	@Override
    	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
    		
    		//change x and y values depending on the current scale factor.
    		mDX -= distanceX / mScaleFactor;
        	mDY -= distanceY / mScaleFactor;
        	
        	
        	
        	invalidate(); //calls onDraw()
    		
			return true;
    	}
    	
    }

	public void setLocation(float[] xy) {
		mDX = xy[0];
    	mDY = xy[1];
    	mScaleFactor = 1.f;
    	invalidate();
	}
}
