package com.example.pxlparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.pxlparking.App.CHANNEL_1_ID;

public class EmptySpotsActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView nv;
    private NotificationManagerCompat notificationManager;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference firebaseRootRef = database.getReference();
    DatabaseReference parkingReference = firebaseRootRef.child("parkings");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_spots);

        notificationManager = NotificationManagerCompat.from(this);

        mDrawerLayout = findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = findViewById(R.id.nav_view);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.nav_home:
                        Intent intentMainActivity = new Intent(EmptySpotsActivity.this, MainActivity.class);
                        startActivity(intentMainActivity);
                        break;
                    case R.id.nav_empty:
                        Intent intentEmptySpotsActivity = new Intent(EmptySpotsActivity.this, EmptySpotsActivity.class);
                        startActivity(intentEmptySpotsActivity);
                        break;
                    default:
                        return true;
                }
                return true;

            }
        });

        parkingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (int i = 0; i < 5; i++) {
                    if ((Long) dataSnapshot.child(i + "").child("parkingSpots").getValue() < 10) {
                        Notification notification = new NotificationCompat.Builder(EmptySpotsActivity.this, CHANNEL_1_ID)
                                .setSmallIcon(R.drawable.ic_one)
                                .setContentTitle("Volzet")
                                .setContentText(dataSnapshot.child(i + "").child("name").getValue().toString())
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .build();

                        notificationManager.notify(i, notification);
                    }
                }

                TextView name0 = findViewById(R.id.name0);
                name0.setText(dataSnapshot.child("0").child("name").getValue().toString() + ":  ");
                TextView parkingSpots0 = findViewById(R.id.parkingSpots0);
                parkingSpots0.setText(dataSnapshot.child("0").child("parkingSpots").getValue().toString());

                TextView name1 = findViewById(R.id.name1);
                name1.setText(dataSnapshot.child("1").child("name").getValue().toString() + ":  ");
                TextView parkingSpots1 = findViewById(R.id.parkingSpots1);
                parkingSpots1.setText(dataSnapshot.child("1").child("parkingSpots").getValue().toString());

                TextView name2 = findViewById(R.id.name2);
                name2.setText(dataSnapshot.child("2").child("name").getValue().toString() + ":  ");
                TextView parkingSpots2 = findViewById(R.id.parkingSpots2);
                parkingSpots2.setText(dataSnapshot.child("2").child("parkingSpots").getValue().toString());

                TextView name3 = findViewById(R.id.name3);
                name3.setText(dataSnapshot.child("3").child("name").getValue().toString() + ":  ");
                TextView parkingSpots3 = findViewById(R.id.parkingSpots3);
                parkingSpots3.setText(dataSnapshot.child("3").child("parkingSpots").getValue().toString());

                TextView name4 = findViewById(R.id.name4);
                name4.setText(dataSnapshot.child("4").child("name").getValue().toString() + ":  ");
                TextView parkingSpots4 = findViewById(R.id.parkingSpots4);
                parkingSpots4.setText(dataSnapshot.child("4").child("parkingSpots").getValue().toString());

                TextView name5 = findViewById(R.id.name5);
                name5.setText(dataSnapshot.child("5").child("name").getValue().toString() + ":  ");
                TextView parkingSpots5 = findViewById(R.id.parkingSpots5);
                parkingSpots5.setText(dataSnapshot.child("5").child("parkingSpots").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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


}
