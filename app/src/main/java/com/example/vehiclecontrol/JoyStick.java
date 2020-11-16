package com.example.vehiclecontrol;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

public class JoyStick extends View {
    //Fix the X, Y coordinates and radius of the rocker background circle
    private float mRockerBg_X;
    private float mRockerBg_Y;
    private float mRockerBg_R;
    //X, Y coordinates of the joystick and the radius of the joystick
    private float mRockerBtn_X;
    private float mRockerBtn_Y;
    private float mRockerBtn_R;
    private Bitmap mBmpRockerBg;
    private Bitmap mBmpRockerBtn;

    private PointF mCenterPoint;

    public JoyStick(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        // Get bitmap
        mBmpRockerBg = BitmapFactory.decodeResource(context.getResources(), R.drawable.joystick_background);
        mBmpRockerBtn = BitmapFactory.decodeResource(context.getResources(), R.drawable.joystick_knob);

        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            // When calling this method, you can get the actual width of the view getWidth() and getHeight()
            @Override
            public boolean onPreDraw() {
                // TODO Auto-generated method stub
                getViewTreeObserver().removeOnPreDrawListener(this);

                mCenterPoint = new PointF(getWidth() / 2, getHeight() / 2);
                mRockerBg_X = mCenterPoint.x;
                mRockerBg_Y = mCenterPoint.y;

                mRockerBtn_X = mCenterPoint.x;
                mRockerBtn_Y = mCenterPoint.y;

                float tmp_f = mBmpRockerBg.getWidth() / (float)(mBmpRockerBg.getWidth() + mBmpRockerBtn.getWidth());
                mRockerBg_R = tmp_f * getWidth() / 2;
                mRockerBtn_R = (1.0f - tmp_f)* getWidth() / 2;

                return true;
            }
        });


        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                while(true){

                    //The system calls the onDraw method to refresh the screen
                    JoyStick.this.postInvalidate();

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        canvas.drawBitmap(mBmpRockerBg, null,
                new Rect((int)(mRockerBg_X - mRockerBg_R),
                        (int)(mRockerBg_Y - mRockerBg_R),
                        (int)(mRockerBg_X + mRockerBg_R),
                        (int)(mRockerBg_Y + mRockerBg_R)),
                null);
        canvas.drawBitmap(mBmpRockerBtn, null,
                new Rect((int)(mRockerBtn_X - mRockerBtn_R),
                        (int)(mRockerBtn_Y - mRockerBtn_R),
                        (int)(mRockerBtn_X + mRockerBtn_R),
                        (int)(mRockerBtn_Y + mRockerBtn_R)),
                null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            // When the touch screen area is not within the active range
            if (Math.sqrt(Math.pow((mRockerBg_X - (int) event.getX()), 2) + Math.pow((mRockerBg_Y - (int) event.getY()), 2)) >= mRockerBg_R) {
                //Get the angle formed by the joystick and the touch screen point
                double tempRad = getRad(mRockerBg_X, mRockerBg_Y, event.getX(), event.getY());
                //Ensure the length limit of the inner small circle movement
                getXY(mRockerBg_X, mRockerBg_Y, mRockerBg_R, tempRad);
            } else {//If the center point of the ball is smaller than the active area, then move with the user's touch screen point
                mRockerBtn_X = (int) event.getX();
                mRockerBtn_Y = (int) event.getY();
            }
            if(mJoyStickChangeListener != null) {
                mJoyStickChangeListener.report(mRockerBtn_X - mCenterPoint.x, mRockerBtn_Y - mCenterPoint.y);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            //When the button is released, the joystick must be restored to its initial position
            mRockerBtn_X = mCenterPoint.x;
            mRockerBtn_Y = mCenterPoint.y;
            if(mJoyStickChangeListener != null) {
                mJoyStickChangeListener.report(0, 0);
            }
        }
        return true;
    }

    /***
     Get the arc between two points
     */
    public double getRad(float px1, float py1, float px2, float py2) {
        //Get the distance between two points X
        float x = px2 - px1;
        //Get the distance between two points Y
        float y = py1 - py2;
        //Calculate the length of the hypotenuse
        float xie = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        //Get the cosine value of this angle (by the theorem in trigonometric function: adjacent side/hypotenuse = angle cosine value)
        float cosAngle = x / xie;
        //Get the radian of its angle through the law of arc cosine
        float rad = (float) Math.acos(cosAngle);
        //Note: When the Y coordinate of the touch screen position <the Y coordinate of the joystick, we have to take the negative value -0~-180
        if (py2 < py1) {
            rad = -rad;
        }
        return rad;
    }

    /**
     *
     *@Param R Rotation point of circular motion
     @Param centerX Rotation point X
     @Param centerY Rotation point Y
     Radians of rotation* @param rad
     */
    public void getXY(float centerX, float centerY, float R, double rad) {
        //Get the X coordinate of the circular motion
        mRockerBtn_X = (float) (R * Math.cos(rad)) + centerX;
        //Get the Y coordinate of circular motion
        mRockerBtn_Y = (float) (R * Math.sin(rad)) + centerY;
    }

    JoyStickChangeListener mJoyStickChangeListener = null;
    public void setJoyStickChangeListener(JoyStickChangeListener rockerChangeListener) {
        mJoyStickChangeListener = rockerChangeListener;
    }
    public interface JoyStickChangeListener {
        void report(float x, float y);
    }
}