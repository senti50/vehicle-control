package com.example.vehiclecontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private JoyStick joyStickViewRight;
    private JoyStick joyStickViewLeft;


    private TextView xView;
    private TextView yView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
        // The screen keeps on
        setContentView(R.layout.activity_main);


        joyStickViewLeft = findViewById(R.id.rockerView1);
        joyStickViewRight = findViewById(R.id.rockerView2);

        xView=(TextView)findViewById(R.id.textView1);
        yView=(TextView)findViewById(R.id.textView2);

        joyStickViewLeft.setJoyStickChangeListener(new JoyStick.JoyStickChangeListener() {

            @Override
            public void report(float x, float y) {
                // TODO Auto-generated method stub

                doLog(x + "/" + y + " LEFT");
                xView.setText("X: "+x);
                yView.setText("Y: "+y);
            }
        });

        joyStickViewRight.setJoyStickChangeListener(new JoyStick.JoyStickChangeListener() {

            @Override
            public void report(float x, float y) {
                // TODO Auto-generated method stub
                doLog(x + "/" + y + " RIGHT");

            }
        });
    }

    private static final String TAG = "MainActivity";

    void doLog(String log) {
        Log.i(TAG, log);
    }


}