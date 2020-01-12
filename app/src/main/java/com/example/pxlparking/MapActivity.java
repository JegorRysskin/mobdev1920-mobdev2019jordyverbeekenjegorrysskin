package com.example.pxlparking;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String mParkingName;
    private String mAddress;
    private String mGeoLocation;
    private double[] geoLatLong;
    private String mParkingSpots;

    private TextView mAddressTextView;
    private TextView mGeoLocationTextView;
    private TextView mParkingSpotsTextView;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

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
                mAddressTextView.setText(R.string.adres);
                mAddressTextView.append(mAddress);
            }
            if (intentThatStartedThisActivity.hasExtra("geoLocation")) {
                geoLatLong = intentThatStartedThisActivity.getDoubleArrayExtra("geoLocation");
                mGeoLocation = geoLatLong[0] + ", " + geoLatLong[1];

                mGeoLocationTextView.setText(R.string.coordinates);
                mGeoLocationTextView.append(mGeoLocation);
            }
            if (intentThatStartedThisActivity.hasExtra("parkingSpots")) {
                mParkingSpots = intentThatStartedThisActivity.getStringExtra("parkingSpots");

                mParkingSpotsTextView.setText(R.string.freespots);
                mParkingSpotsTextView.append(mParkingSpots);
            }
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_activity);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng PXLParking = new LatLng(geoLatLong[0], geoLatLong[1]);
        mMap.addMarker(new MarkerOptions().position(PXLParking).title(mParkingName));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PXLParking, 15));

    }

        @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return true;
    }
}
