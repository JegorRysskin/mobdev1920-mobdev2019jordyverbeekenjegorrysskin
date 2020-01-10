package com.example.pxlparking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment {

    private String mParkingName;
    private GoogleMap googleMap;
    MapView mMapView;
    private double[] geoLatLong;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_map_fragment, container, false);

        mMapView = view.findViewById(R.id.map_fragment);
        mMapView.onCreate(savedInstanceState);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bundle bundle = getArguments();

        if (bundle != null){
            //TODO: default location upon load will be the one saved in local storage

            mParkingName = getArguments().getString("parkingName");
            geoLatLong = getArguments().getDoubleArray("geoLocation");

            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap mMap) {
                    googleMap = mMap;

                    //googleMap.setMyLocationEnabled(true);

                    LatLng PXLParking = new LatLng(geoLatLong[0], geoLatLong[1]);
                    googleMap.addMarker(new MarkerOptions().position(PXLParking).title(mParkingName));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PXLParking, 15));
                }
            });
        }
        return view;
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
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
