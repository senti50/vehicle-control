package com.example.vehiclecontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView xViewLeft;
    private TextView yViewLeft;
    private TextView xViewRight;
    private TextView yViewRight;
    private JoyStick joyStickViewLeft;
    private JoyStick joyStickViewRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        joyStickViewLeft = findViewById(R.id.rockerView1);
        joyStickViewRight = findViewById(R.id.rockerView2);

        xViewLeft = findViewById(R.id.textView1);
        yViewLeft = findViewById(R.id.textView2);

        xViewRight = findViewById(R.id.textView3);
        yViewRight = findViewById(R.id.textView4);

        joyStickViewLeft.setJoyStickChangeListener(new JoyStickChangeListener() {

            @Override
            public void changingPosition(float x, float y) {
                doLog(x + "/" + y + " LEFT");
                changeCoordinatesOnView(xViewLeft, yViewLeft, x, y);
            }
        });

        joyStickViewRight.setJoyStickChangeListener(new JoyStickChangeListener() {
            @Override
            public void changingPosition(float x, float y) {
                doLog(x + "/" + y + " RIGHT");
                changeCoordinatesOnView(xViewRight, yViewRight, x, y);
            }
        });

    }


    void doLog(String log) {
        Log.i(TAG, log);
    }

    void changeCoordinatesOnView(TextView xTextView, TextView yTextView, float x, float y) {
        xTextView.setText("X: " + x);
        yTextView.setText("Y: " + y);
    }


}