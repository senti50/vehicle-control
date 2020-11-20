package com.example.vehiclecontrol;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import java.util.LinkedHashMap;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MainActivity";
    private TextView xViewLeft;
    private TextView yViewLeft;
    private TextView xViewRight;
    private TextView yViewRight;
    private JoyStick joyStickViewLeft;
    private JoyStick joyStickViewRight;


    private GoogleMap mMap;

    private LinkedHashMap<String, Marker> allPoints = new LinkedHashMap<>();
    private Polyline polyline = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).draggable(true);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                Marker marker = mMap.addMarker(markerOptions);
                allPoints.put(marker.getId(), marker);
                drawLineBetweenMarkers();

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                allPoints.remove(marker.getId());
                marker.remove();
                drawLineBetweenMarkers();
                return false;

            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Marker marker1 = allPoints.get(marker.getId());
                if (marker1 != null) {
                    marker1.setPosition(marker.getPosition());
                }
                drawLineBetweenMarkers();
            }
        });
    }

    private void drawLineBetweenMarkers() {
        if (polyline != null) polyline.remove();
        if (allPoints.size() > 1) {
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color(Color.BLACK);
            for (Marker marker : allPoints.values()) {
                polylineOptions.add(marker.getPosition());
            }
            polyline = mMap.addPolyline(polylineOptions);

        }
    }

    private void doLog(String log) {
        Log.i(TAG, log);
    }

    private void changeCoordinatesOnView(TextView xTextView, TextView yTextView, float x, float y) {
        xTextView.setText("X: " + x);
        yTextView.setText("Y: " + y);
    }


}