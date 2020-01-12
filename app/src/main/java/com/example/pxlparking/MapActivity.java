package com.example.pxlparking;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private String mParkingName;
    private String mAddress;
    private String mGeoLocation;
    private double[] geoLatLong;
    private String mParkingSpots;

    private TextView mAddressTextView;
    private TextView mGeoLocationTextView;
    private TextView mParkingSpotsTextView;

    private GoogleMap mMap;

    private LatLng currentPosition;
    private Marker currentLocationMarker;
    private Double lat;
    private Double lng;

    private Location locationParking;
    private Location locationCurrent;

    private LocationManager locationManager;
    private String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        provider = locationManager.getBestProvider(new Criteria(), false);

        locationParking = new Location(provider);
        locationCurrent = new Location(provider);

        checkLocationPermission();

        mAddressTextView = findViewById(R.id.tv_address);
        mGeoLocationTextView = findViewById(R.id.tv_geoLocation);
        mParkingSpotsTextView = findViewById(R.id.tv_parkingSpots);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {

            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TITLE)) {
                mParkingName = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TITLE);
                setTitle(mParkingName);
            }
            if (intentThatStartedThisActivity.hasExtra("address")) {
                mAddress = intentThatStartedThisActivity.getStringExtra("address");
                mAddressTextView.setText(mAddress);
            }
            if (intentThatStartedThisActivity.hasExtra("geoLocation")) {
                geoLatLong = intentThatStartedThisActivity.getDoubleArrayExtra("geoLocation");
                mGeoLocation = geoLatLong[0] + ", " + geoLatLong[1];

                mGeoLocationTextView.setText(mGeoLocation);
            }
            if (intentThatStartedThisActivity.hasExtra("parkingSpots")) {
                mParkingSpots = intentThatStartedThisActivity.getStringExtra("parkingSpots");
                mParkingSpotsTextView.setText(mParkingSpots);
            }
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng PXLParking = new LatLng(geoLatLong[0], geoLatLong[1]);
        locationParking.setLongitude(geoLatLong[1]);
        locationParking.setLatitude(geoLatLong[0]);
        mMap.addMarker(new MarkerOptions().position(PXLParking).title(mParkingName));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PXLParking, 15));

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Permission")
                        .setMessage("PxlApp needs permission to use your location.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        locationManager.requestLocationUpdates(provider, 400, 1, this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }

        lat = location.getLatitude();
        lng = location.getLongitude();

        currentPosition = new LatLng(lat, lng);
        currentLocationMarker = mMap.addMarker(new MarkerOptions().position(currentPosition).title("Current location"));

        locationCurrent.setLongitude(lng);
        locationCurrent.setLatitude(lat);
        TextView text = findViewById(R.id.tv_parkingSpots);
        String test = locationCurrent.distanceTo(locationParking) + "";
        text.setText(test);

        Log.i("Location info: Lat", lat.toString());
        Log.i("Location info: Lng", lng.toString());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(provider, 400, 1, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager.removeUpdates(this);
        }
    }
}
