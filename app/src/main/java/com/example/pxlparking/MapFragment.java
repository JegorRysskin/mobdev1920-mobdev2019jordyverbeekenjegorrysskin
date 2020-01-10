package com.example.pxlparking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private String mParkingName;
    private GoogleMap mMap;
    private double[] geoLatLong;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_map_fragment, container, false);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng PXLParking = new LatLng(geoLatLong[0], geoLatLong[1]);
        mMap.addMarker(new MarkerOptions().position(PXLParking).title(mParkingName));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PXLParking, 15));
    }


}
