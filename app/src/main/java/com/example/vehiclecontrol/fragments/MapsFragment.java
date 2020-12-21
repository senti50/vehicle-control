package com.example.vehiclecontrol.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.vehiclecontrol.R;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;

public class MapsFragment extends Fragment {
    private GoogleMap mMap;
    private LinkedHashMap<String, Marker> allPoints = new LinkedHashMap<>();
    private Polyline polyline = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);

        final ImageButton popupButton = rootView.findViewById(R.id.popupButton);


        popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getActivity().getApplicationContext(), v);
                popup.getMenuInflater().inflate(R.menu.options, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.importPoints:
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("text/plain");
                                startActivityForResult(intent, 77);
                                return true;
                            case R.id.exportPoints:
                                if (allPoints.isEmpty()) {
                                    Toast.makeText(getContext(), "No points on the map for export", Toast.LENGTH_LONG).show();
                                } else {
                                    exportPoints(getActivity().getApplicationContext());
                                }
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });


        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
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
    };

    public void showMenu(View v) {

        PopupMenu popup = new PopupMenu(getContext(), v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) getContext());
        popup.show();
    }


    private void exportPoints(Context context) {
        String filename = "Points";
        String filepath = "ExportedPoints";

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyyHH:mm:ss", Locale.getDefault());
        String currentDate = formatter.format(date);
        try {
            File myExternalFile = new File(getActivity().getExternalFilesDir(filepath), filename + currentDate + ".txt");
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
        Toast.makeText(getContext(), "Points have been exported to the  \"ExportedPoints\" folder ", Toast.LENGTH_LONG).show();

    }

    private boolean readTextFile(Uri uri) {
        BufferedReader reader;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(getActivity().getApplicationContext().getContentResolver().openInputStream(uri)));

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
                } catch (Exception e) {
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 77) {
            if (resultCode == RESULT_OK) {
                if (data == null) return;

                Uri uri = data.getData();
                boolean isCorrect = readTextFile(uri);
                if (isCorrect) {
                    Toast.makeText(getContext(), "Points have been imported", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Unable to import points, wrong file format", Toast.LENGTH_LONG).show();
                }
            }
        }

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


}