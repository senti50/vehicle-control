package com.example.vehiclecontrol;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.vehiclecontrol.fragments.MapsFragment;
import com.example.vehiclecontrol.fragments.PositionVisualizationFragment;
import com.example.vehiclecontrol.fragments.VideoFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class MainActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";
    private Button changeFragmentButton;
    private JoyStick joyStickViewLeft;
    private JoyStick joyStickViewRight;

    private boolean isMapFragment = true;
    private boolean isPositionVisualizationFragment = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);


        final PositionVisualizationFragment positionVisualizationFragment = new PositionVisualizationFragment();
        replaceCornerFragment(positionVisualizationFragment);

        final MapsFragment mapsFragment = new MapsFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_fragment_container, mapsFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        final VideoFragment videoFragment = new VideoFragment();


        joyStickViewLeft = findViewById(R.id.joyStickLeftView);
        joyStickViewRight = findViewById(R.id.joyStickRightView);

        changeFragmentButton = findViewById(R.id.change_fragmentButton);


        changeFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMapFragment) {
                    replaceFragment(mapsFragment);
                    isMapFragment = true;
                    changeFragmentButton.setText("Video");
                } else {
                    if (isPositionVisualizationFragment) {
                        replaceFragment(videoFragment);
                    } else {
                        FragmentManager manager = getSupportFragmentManager();
                        manager.beginTransaction().remove(videoFragment).commit();
                        manager.executePendingTransactions();
                        manager.beginTransaction()
                                .add(R.id.corner_fragment_container, positionVisualizationFragment)
                                .add(R.id.main_fragment_container, videoFragment)
                                .commit();
                        isPositionVisualizationFragment = true;
                    }
                    isMapFragment = false;
                    changeFragmentButton.setText("Map");
                }

            }
        });


        joyStickViewLeft.setJoyStickChangeListener(new JoyStickChangeListener() {

            @Override
            public void changingPosition(float x, float y) {
                doLog(x + "/" + y + " LEFT");

            }
        });

        joyStickViewRight.setJoyStickChangeListener(new JoyStickChangeListener() {
            @Override
            public void changingPosition(float x, float y) {
                doLog(x + "/" + y + " RIGHT");

            }
        });


        findViewById(R.id.corner_fragment_container).setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMapFragment) {
                    // MapsFragment is in main_fragment_container
                    if (!isPositionVisualizationFragment) {
                        FragmentManager manager = getSupportFragmentManager();
                        manager.beginTransaction().remove(videoFragment).commit();
                        manager.executePendingTransactions();
                        manager.beginTransaction()
                                .replace(R.id.corner_fragment_container, positionVisualizationFragment)
                                .commit();

                        isPositionVisualizationFragment = true;
                    } else {
                        FragmentManager manager = getSupportFragmentManager();
                        manager.beginTransaction().remove(videoFragment).commit();
                        manager.executePendingTransactions();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.corner_fragment_container, videoFragment)
                                .commit();
                        isPositionVisualizationFragment = false;
                    }


                }
            }
        }));

    }


    private void replaceCornerFragment(final Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.corner_fragment_container, fragment);
        transaction.commit();

    }

    private void replaceFragment(final Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.commit();

    }


    private void doLog(String log) {
        Log.i(TAG, log);
    }


}