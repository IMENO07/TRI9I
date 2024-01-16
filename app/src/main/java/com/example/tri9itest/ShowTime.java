package com.example.tri9itest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ShowTime extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    Button addTrip,tripList,CurrentTrip;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_time);
        addTrip = findViewById(R.id.addTrip);
        tripList = findViewById(R.id.tripList2);
        CurrentTrip = findViewById(R.id.currentTrip);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        addTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowTime.this,CreateTrip.class);
                startActivity(intent);
            }
        });

        tripList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowTime.this,TripList.class);
                startActivity(intent);
            }
        });
        CurrentTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowTime.this,currentTrip.class);
                startActivity(intent);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();

        if (id == R.id.menu_refresh){
            startActivity(getIntent());
            finish();
        } else if (id == R.id.menu_add_trip) {
            Intent intent = new Intent(ShowTime.this, CreateTrip.class);
            startActivity(intent);
        }else if (id == R.id.menu_trip_list) {
            Intent intent = new Intent(ShowTime.this, TripList.class);
            startActivity(intent);
        }else if (id == R.id.menu_user_profile) {
            Intent intent = new Intent(ShowTime.this, UserProfileActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_settings) {
            Intent intent = new Intent(ShowTime.this, SettingsActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ShowTime.this , MainActivity.class);

            //clear stack to prevent user coming back to profil after loggin out
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }


        return  super.onOptionsItemSelected(item);
    }
}