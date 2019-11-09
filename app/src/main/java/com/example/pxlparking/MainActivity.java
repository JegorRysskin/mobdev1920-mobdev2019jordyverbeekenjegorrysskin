package com.example.pxlparking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ParkingAdapter mAdapter;
    private Cursor mCursor;

    private String sample_parkingdata = "[{\"id\":1, \"name\": \"Oude Brandweer\"}, {\"id\":2, \"name\": \"De Singel\"}, {\"id\":3, \"name\": \"Grenslandhallen\"}, {\"id\":4, \"name\": \"Hawaii\"}, {\"id\":5, \"name\": \"Ijshal De Schaverdijn\"}, {\"id\":6, \"name\": \"Japanse Tuin\"}]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.reclyclerview_parking);

        mCursor = getJSONCursor(sample_parkingdata);
        mAdapter = new ParkingAdapter(this, mCursor);

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
}
