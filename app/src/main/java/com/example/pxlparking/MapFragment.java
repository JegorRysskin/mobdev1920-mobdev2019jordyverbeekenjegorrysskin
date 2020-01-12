package com.example.pxlparking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment {

    private String mParkingName = "Oude Brandweer";
    private GoogleMap googleMap;
    MapView mMapView;
    private double[] geoLatLong = { 50.933116, 5.351454};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_map_fragment, container, false);

        mMapView = view.findViewById(R.id.map_fragment);
        mMapView.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null){
            mParkingName = getArguments().getString("parkingName");
            geoLatLong = getArguments().getDoubleArray("geoLocation");
        }

        setGoogleMaps(mParkingName, geoLatLong);
        return view;
    }

    private void setGoogleMaps(final String parkingName, final double[] geoLocation) {
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                //googleMap.setMyLocationEnabled(true);

                LatLng PXLParking = new LatLng(geoLocation[0], geoLocation[1]);
                googleMap.addMarker(new MarkerOptions().position(PXLParking).title(parkingName));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PXLParking, 15));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
