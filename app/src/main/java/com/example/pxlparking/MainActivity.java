package com.example.pxlparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;

import static com.example.pxlparking.App.CHANNEL_1_ID;


public class MainActivity extends AppCompatActivity implements ParkingAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private ParkingAdapter mAdapter;
    private Cursor mCursor;
    private Context mContext;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView nv;
    private ParkingAdapterOnClickHandler mClickhandler;
    private NotificationManagerCompat notificationManager;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference firebaseRootRef = database.getReference();
    DatabaseReference parkingReference = firebaseRootRef.child("parkings");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        notificationManager = NotificationManagerCompat.from(this);


        mRecyclerView = findViewById(R.id.reclyclerview_parking);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new ParkingAdapter(this, mCursor, this));

        mContext = this;
        mClickhandler = this;

        mDrawerLayout = findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadParkingData(new MyCallback() {
            @Override
            public void onCallback(String jsonString) {
                mCursor = getJSONCursor(jsonString);
                mAdapter = new ParkingAdapter(mContext, mCursor, mClickhandler);
                mRecyclerView.setAdapter(mAdapter);

                Log.d(MainActivity.class.getSimpleName(), jsonString);
            }
        });


        nv = findViewById(R.id.nav_view);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.nav_home:
                        Intent intentMainActivity = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intentMainActivity);
                        break;
                    case R.id.nav_empty:
                        Intent intentEmptySpotsActivity = new Intent(MainActivity.this, EmptySpotsActivity.class);
                        startActivity(intentEmptySpotsActivity);
                        break;
                    default:
                        return true;
                }
                return true;

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadParkingData(final MyCallback myCallback) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                   for (int i = 0; i < 5; i++) {
                    if ((Long) dataSnapshot.child(i + "").child("parkingSpots").getValue() < 10) {
                        Notification notification = new NotificationCompat.Builder(MainActivity.this, CHANNEL_1_ID)
                                .setSmallIcon(R.drawable.ic_one)
                                .setContentTitle("Volzet")
                                .setContentText(dataSnapshot.child(i + "").child("name").getValue().toString())
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

    private interface MyCallback {
        void onCallback(String jsonString);
    }
}
