package com.example.pxlparking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity implements ParkingAdapter.ParkingAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private ParkingAdapter mAdapter;
    private Cursor mCursor;

    private String sample_parkingdata = "[{\"id\":1, \"name\": \"Oude Brandweer\", \"address\": \"Willekensmolenstraat 120, 3500 Hasselt\", \"parkingSpots\": 135, \"lat\":50.933116, \"long\":5.351454}, " +
            "{\"id\":2, \"name\": \"De Singel\", \"address\": \"Elfde-Liniestraat 32, 3500 Hasselt\", \"parkingSpots\": 199, \"lat\":50.935183, \"long\":5.346945}, " +
            "{\"id\":3, \"name\": \"Grenslandhallen\", \"address\": \"Grenslandhallen, 3500 Hasselt\", \"parkingSpots\": 250, \"lat\":50.933845, \"long\":5.362953}, " +
            "{\"id\":4, \"name\": \"Hawaii\", \"address\": \"Koning Boudewijnlaan Parking, 3500 Hasselt\", \"parkingSpots\": 175, \"lat\":50.932676, \"long\":5.344924}, " +
            "{\"id\":5, \"name\": \"Ijshal De Schaverdijn\", \"address\": \"Gouverneur Verwilghensingel 13, 3500 Hasselt\", \"parkingSpots\": 150, \"lat\":50.937358, \"long\":5.354852}, " +
            "{\"id\":6, \"name\": \"Japanse Tuin\", \"address\": \"Gouverneur Verwilghensingel 16, 3500 Hasselt\", \"parkingSpots\": 100, \"lat\":50.935296, \"long\":5.358092}]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.reclyclerview_parking);

        mCursor = getJSONCursor(sample_parkingdata);
        mAdapter = new ParkingAdapter(this, mCursor, this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mRecyclerView.setAdapter(mAdapter);

    }

    private Cursor getJSONCursor(String sample_parkingdata) {
        try {
            JSONArray jsonArray = new JSONArray(sample_parkingdata);
            return new JSONArrayCursor(jsonArray);
        } catch (JSONException exception){
            String ex = exception.getMessage();
        }
        return null;
    }

    @Override
    public void onClick(int adapterPosition) {
        Intent intent = new Intent(this,MapActivity.class);

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
}
