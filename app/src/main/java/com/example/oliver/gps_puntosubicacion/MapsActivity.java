package com.example.oliver.gps_puntosubicacion;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener, View.OnClickListener {

    private GoogleMap mMap;
    EditText addressEditText;
    String title;
    LatLng position1;
    SharedPreferences sharedPreferences;
    int locationCount;
    //Button delete;
    FloatingActionButton delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        delete = (FloatingActionButton) findViewById(R.id.btn1);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearMarker(v);
                mMap.clear();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Opening the sharedPreferences object
        sharedPreferences = getSharedPreferences("location", 0);

        // Getting number of locations already stored
        locationCount = sharedPreferences.getInt("locationCount", 0);


        // If locations are already saved
        if (locationCount != 0) {
            String lat = "";
            String lng = "";
            // Iterating through all the locations stored
            for (int i = 0; i < locationCount; i++) {
                // Getting the latitude of the i-th location
                lat = sharedPreferences.getString("lat" + i, "0");
                // Getting the longitude of the i-th location
                lng = sharedPreferences.getString("lng" + i, "0");
                // Toast.makeText(this, lat + "," + lng, Toast.LENGTH_LONG).show();
                double lat3 = Double.valueOf(lat).doubleValue();
                double lng3 = Double.valueOf(lng).doubleValue();
                position1 = new LatLng(lat3, lng3);
                mMap.addMarker(new MarkerOptions().position(position1));
            }
        }

        mMap.setOnMapClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        try {
        addressEditText = (EditText) findViewById(R.id.editTextAddMarker);
        title = addressEditText.getText().toString();

        if (title.length() > 2) {
            Geocoder coder = new Geocoder(this);
            List<Address> address;
            LatLng p1 = null;
            Address location = null;
                // May throw an IOException
                address = coder.getFromLocationName(title, 5);
                if (address == null) {
                    return ;
                }
                location = address.get(0);
                p1 = new LatLng(location.getLatitude(), location.getLongitude() );




            mMap.addMarker(new MarkerOptions().position(p1));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(p1));

            Toast.makeText(this, "Referencia Agregada", Toast.LENGTH_LONG).show();

            addressEditText.setText("");

            locationCount++;

            /** Opening the editor object to write data to sharedPreferences */
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Storing the latitude for the i-th location
            editor.putString("lat" + Integer.toString((locationCount - 1)), Double.toString(latLng.latitude));

            // Storing the longitude for the i-th location
            editor.putString("lng" + Integer.toString((locationCount - 1)), Double.toString(latLng.longitude));

            // Storing the count of locations or marker count
            editor.putInt("locationCount", locationCount);


            /** Saving the values stored in the shared preferences */
            editor.commit();


        } else if (title.length() < 1) {
            Toast.makeText(this, "Debe introducir una referencia", Toast.LENGTH_LONG).show();
        }
        } catch (IOException ex) {

            ex.printStackTrace();
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }
    public void clearMarker(View view) {
        // Opening the editor object to delete data from sharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Clearing the editor
        editor.clear();
        // Committing the changes
        editor.commit();
    }
}
