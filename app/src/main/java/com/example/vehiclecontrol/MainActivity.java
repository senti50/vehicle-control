package com.example.vehiclecontrol;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, PopupMenu.OnMenuItemClickListener {
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

        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        joyStickViewLeft = findViewById(R.id.joyStickLeftView);
        joyStickViewRight = findViewById(R.id.joyStickRightView);

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

    public void showMenu(View v) {

        PopupMenu popup = new PopupMenu(this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) this);
        popup.inflate(R.menu.options);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.importPoints:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("text/plain");
                startActivityForResult(intent, 77);
                return true;
            case R.id.exportPoints:
                exportPoints(getApplicationContext());
                return true;
            default:
                return false;
        }
    }

    private void exportPoints(Context context) {
        String filename = "Points";
        String filepath = "ExportedPoints";

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyyHH:mm:ss", Locale.getDefault());
        String currentDate = formatter.format(date);
        try {
            File myExternalFile = new File(getExternalFilesDir(filepath), filename + currentDate + ".txt");
            FileOutputStream fos = new FileOutputStream(myExternalFile);

            for (Marker mark : allPoints.values()) {
                LatLng position = mark.getPosition();
                fos.write(position.toString().getBytes());
                fos.write(String.format("%n").getBytes());
            }

            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Points have been exported to the  \"ExportedPoints\" folder ", Toast.LENGTH_LONG).show();

    }

    private boolean readTextFile(Uri uri) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));

            String line = "";
            while ((line = reader.readLine()) != null) {

                try {
                    String replace = line.replace("lat/lng: (", "");
                    String replace1 = replace.replace(")", "");
                    String[] strings = replace1.split(",");
                    double lat = Double.parseDouble(strings[0]);
                    double lng = Double.parseDouble(strings[1]);
                    LatLng latLng = new LatLng(lat, lng);
                    MarkerOptions markerOptions = new MarkerOptions().position(latLng).draggable(true);
                    Marker marker = mMap.addMarker(markerOptions);
                    allPoints.put(marker.getId(), marker);
                    drawLineBetweenMarkers();
                    builder.append(line);
                }catch (Exception e){
                    return false;
                }

            }
            reader.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 77) {
            if (resultCode == RESULT_OK) {
                if (data == null) return;

                Uri uri = data.getData();
                boolean isCorrect = readTextFile(uri);
                if (isCorrect) {
                    Toast.makeText(this, "Points have been imported", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Unable to import points, wrong file format", Toast.LENGTH_LONG).show();
                }
            }
        }

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