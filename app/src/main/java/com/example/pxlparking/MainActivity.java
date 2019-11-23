package com.example.pxlparking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;


public class MainActivity extends AppCompatActivity implements ParkingAdapter.ParkingAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private ParkingAdapter mAdapter;
    private Cursor mCursor;
    private Context mContext;
    private ParkingAdapter.ParkingAdapterOnClickHandler mClickhandler;

    private String sample_parkingdata = "[{\"id\":0, \"name\": \"Oude Brandweer\", \"address\": \"Willekensmolenstraat 120, 3500 Hasselt\", \"parkingSpots\": 135, \"lat\":50.933116, \"long\":5.351454}, " +
            "{\"id\":1, \"name\": \"De Singel\", \"address\": \"Elfde-Liniestraat 32, 3500 Hasselt\", \"parkingSpots\": 199, \"lat\":50.935183, \"long\":5.346945}, " +
            "{\"id\":2, \"name\": \"Grenslandhallen\", \"address\": \"Grenslandhallen, 3500 Hasselt\", \"parkingSpots\": 250, \"lat\":50.933845, \"long\":5.362953}, " +
            "{\"id\":3, \"name\": \"Hawaii\", \"address\": \"Koning Boudewijnlaan Parking, 3500 Hasselt\", \"parkingSpots\": 175, \"lat\":50.932676, \"long\":5.344924}, " +
            "{\"id\":4, \"name\": \"Ijshal De Schaverdijn\", \"address\": \"Gouverneur Verwilghensingel 13, 3500 Hasselt\", \"parkingSpots\": 150, \"lat\":50.937358, \"long\":5.354852}, " +
            "{\"id\":5, \"name\": \"Japanse Tuin\", \"address\": \"Gouverneur Verwilghensingel 16, 3500 Hasselt\", \"parkingSpots\": 100, \"lat\":50.935296, \"long\":5.358092}]";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference firebaseRootRef = database.getReference();
    DatabaseReference parkingReference = firebaseRootRef.child("parkings");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.reclyclerview_parking);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new ParkingAdapter(this,mCursor,this));

        mContext = this;
        mClickhandler = this;

        loadParkingData(new MyCallback() {
            @Override
            public void onCallback(String jsonString) {
                mCursor = getJSONCursor(jsonString);
                mAdapter = new ParkingAdapter(mContext, mCursor, mClickhandler);
                mRecyclerView.setAdapter(mAdapter);

                Log.d(MainActivity.class.getSimpleName(), jsonString);
            }
        });

    }

    private void loadParkingData(final MyCallback myCallback) {

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String jsonString = new Gson().toJson(dataSnapshot.getValue());
                myCallback.onCallback(jsonString);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(MainActivity.class.getSimpleName(), "Failed to read value.", error.toException());
            }
        };
        parkingReference.addListenerForSingleValueEvent(valueEventListener);
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
        Intent intent = new Intent(this, MapActivity.class);

        if (!mCursor.moveToPosition(adapterPosition))
            return;

        String parkingName = mCursor.getString(mCursor.getColumnIndex("name"));
        String parkingSpots = mCursor.getString(mCursor.getColumnIndex("parkingSpots"));
        String address = mCursor.getString(mCursor.getColumnIndex("address"));

        double posLong = Double.parseDouble(mCursor.getString(mCursor.getColumnIndex("long")));
        double posLat = Double.parseDouble(mCursor.getString(mCursor.getColumnIndex("lat")));

        intent.putExtra(Intent.EXTRA_TITLE, parkingName);
        intent.putExtra("address", address);
        intent.putExtra("geoLocation", new double[]{posLat, posLong});
        intent.putExtra("parkingSpots", parkingSpots);
        startActivity(intent);
    }

    private interface MyCallback{
        void onCallback(String jsonString);
    }
}
