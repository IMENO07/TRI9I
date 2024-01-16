package com.example.tri9itest;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListPassengers extends AppCompatActivity  {
    private List<Reservation> reservationsFiltered;
    private List<Passenger> passengers;
    private RecyclerView recyclerView;
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_passengers);
        recyclerView = findViewById(R.id.ViewPassengers);
        passengers = new ArrayList<>();
        getReservations();

        getSupportActionBar().setTitle("Passengers's List");

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
    }

    private List<Reservation> filter(List<Reservation> reservations) {
        List<Reservation> filteredReservations = new ArrayList<>();
        for (Reservation post : reservations) {
            if (post.getPlacesReservation() != 0) {
                filteredReservations.add(post);
            }
        }
        return filteredReservations;
    }

    private void createList(List<Reservation> reservations) {
        List<Passenger> tempPassengers = new ArrayList<>();
        for (Reservation post : reservations) {
            setDriver(post.getIdUser(), tempPassengers);
        }
        passengers.addAll(tempPassengers);
        setAdapter(passengers);
    }

    private void setDriver(String idUser, List<Passenger> passengers) {
        FirebaseAuth authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    Toast.makeText(ListPassengers.this, "done2", Toast.LENGTH_SHORT).show();
                    String username = readUserDetails.userName;
                    String phone = readUserDetails.phone;
                    Passenger passenger = new Passenger(username, phone);
                    passengers.add(passenger);
                } else {
                    Toast.makeText(ListPassengers.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListPassengers.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getReservations() {
        ApiInterface apiInterface = CreateRequest.getRetrofitInstance().create(ApiInterface.class);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String owner = sharedPreferences.getString("idTrip", "0");
        Log.d("UserIdResult", "UserIdResult in : " + owner);

        ReservationIdTrip request = new ReservationIdTrip(owner);

        Call<List<Reservation>> call = apiInterface.getReservationByTrip(request);

        call.enqueue(new Callback<List<Reservation>>() {
            @Override
            public void onResponse(@NonNull Call<List<Reservation>> call, @NonNull Response<List<Reservation>> response) {
                List<Reservation> result = response.body();
                if (result != null) {
                    List<Reservation> reservations = filter(result);
                    Toast.makeText(ListPassengers.this, "done1", Toast.LENGTH_SHORT).show();
                    createList(filter(result));
                    Log.d(TAG, "updated successfully");
                } else {
                    Log.e(TAG, "Empty response");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Reservation>> call, @NonNull Throwable t) {
                Log.e(TAG, "Failed to update trip places", t);
            }
        });
    }

    private void setAdapter(List<Passenger> passengers) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewPassenger adapter = new RecyclerViewPassenger(this, passengers);
        recyclerView.setAdapter(adapter);

        Toast.makeText(ListPassengers.this, "done3", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(ListPassengers.this, CreateTrip.class);
            startActivity(intent);
        }else if (id == R.id.menu_trip_list) {
            Intent intent = new Intent(ListPassengers.this, TripList.class);
            startActivity(intent);
        }else if (id == R.id.menu_user_profile) {
            Intent intent = new Intent(ListPassengers.this, UserProfileActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_settings) {
            Intent intent = new Intent(ListPassengers.this, SettingsActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ListPassengers.this , MainActivity.class);

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