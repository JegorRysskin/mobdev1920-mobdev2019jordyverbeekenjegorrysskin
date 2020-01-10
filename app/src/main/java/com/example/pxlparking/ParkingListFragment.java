package com.example.pxlparking;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Objects;

import static com.example.pxlparking.App.CHANNEL_1_ID;

public class ParkingListFragment extends Fragment implements ParkingAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private ParkingAdapter mAdapter;
    private Cursor mCursor;
    private Context mContext;
    private ParkingAdapterOnClickHandler mClickhandler;
    private NotificationManagerCompat notificationManager;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference firebaseRootRef = database.getReference();
    DatabaseReference parkingReference = firebaseRootRef.child("parkings");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_list_fragment, container, false);

        notificationManager = NotificationManagerCompat.from(getContext());

        mRecyclerView = view.findViewById(R.id.reclyclerview_parking);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new ParkingAdapter(getContext(), mCursor, this));

        mContext = getContext();
        mClickhandler = this;

        loadParkingData(new ParkingListFragment.MyCallback() {
            @Override
            public void onCallback(String jsonString) {
                mCursor = getJSONCursor(jsonString);
                mAdapter = new ParkingAdapter(mContext, mCursor, mClickhandler);
                mRecyclerView.setAdapter(mAdapter);

                Log.d(ParkingListFragment.class.getSimpleName(), jsonString);
            }
        });

        return view;
    }

    private void loadParkingData(final ParkingListFragment.MyCallback myCallback) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (int i = 0; i <= 5; i++) {
                    Long freeParkingSpots = (Long) dataSnapshot.child(i + "").child("parkingSpots").getValue();
                    String parkingName = dataSnapshot.child(i + "").child("name").getValue().toString();

                    if (freeParkingSpots == 20 || freeParkingSpots == 10 || freeParkingSpots == 5 && getFavorite(i)) {
                        Notification notification = new NotificationCompat.Builder(getContext(), CHANNEL_1_ID)
                                .setSmallIcon(R.drawable.ic_one)
                                .setContentTitle("Favoriet bijna volzet")
                                .setContentText(parkingName + " heeft nog maar " + freeParkingSpots + " plaatsen vrij!")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .build();

                        notificationManager.notify(i, notification);
                    }
                }

                String jsonString = new Gson().toJson(dataSnapshot.getValue());
                myCallback.onCallback(jsonString);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(ParkingListFragment.class.getSimpleName(), "Failed to read value.", error.toException());
            }
        };
        parkingReference.addListenerForSingleValueEvent(valueEventListener);
    }

    private boolean getFavorite(int index) {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("favorite" ,Context.MODE_PRIVATE);
        boolean defaultValue = getResources().getBoolean(R.bool.saved_isChecked_default_key);
        return sharedPref.getBoolean("button_favorite" + index, defaultValue);
    }

    private Cursor getJSONCursor(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            return new JSONArrayCursor(jsonArray);
        } catch (JSONException exception) {
            String ex = exception.getMessage();
        }
        return null;
    }

    @Override
    public void onClick(int adapterPosition) {

        if (!mCursor.moveToPosition(adapterPosition)){
            return;
        } else {
            mCursor.moveToPosition(adapterPosition);
        }

        String parkingName = mCursor.getString(mCursor.getColumnIndex("name"));
        String parkingSpots = mCursor.getString(mCursor.getColumnIndex("parkingSpots"));
        String address = mCursor.getString(mCursor.getColumnIndex("address"));

        double posLong = Double.parseDouble(mCursor.getString(mCursor.getColumnIndex("long")));
        double posLat = Double.parseDouble(mCursor.getString(mCursor.getColumnIndex("lat")));


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment_landscape);

        if (mapFragment != null && mapFragment.isVisible()){
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            MapFragment newMapFragment = new MapFragment();
            Bundle bundle = new Bundle();
            bundle.putString("parkingName", parkingName);
            bundle.putDoubleArray("geoLocation", new double[]{posLat, posLong});
            newMapFragment.setArguments(bundle);

            transaction.replace(mapFragment.getId(), newMapFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        } else {
            Intent intent = new Intent(getActivity().getBaseContext(), MapActivity.class);
            intent.putExtra(Intent.EXTRA_TITLE, parkingName);
            intent.putExtra("address", address);
            intent.putExtra("geoLocation", new double[]{posLat, posLong});
            intent.putExtra("parkingSpots", parkingSpots);
            startActivity(intent);
        }
    }

    private interface MyCallback {
        void onCallback(String jsonString);
    }
}
