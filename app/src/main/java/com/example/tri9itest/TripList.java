package com.example.tri9itest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripList extends AppCompatActivity implements RecyclerListner {
    private RecyclerView recyclerView;
    private List<TripsResult> tripsFiltred;
    private Button filter;
    private int position;
    private EditText destinationFilter,departureFilter;
    private FirebaseAuth authProfile;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_trip_list);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        getSupportActionBar().setTitle("Find a Trip");

        recyclerView = findViewById(R.id.ViewPassengers);
        destinationFilter = findViewById(R.id.destinationFilter);
        departureFilter = findViewById(R.id.departureFilter);
        filter = findViewById(R.id.filter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String departure = String.valueOf(departureFilter.getText());
                String destination = String.valueOf(destinationFilter.getText());
                getTrips(departure,destination);
            }
        });

        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick(position);

            }
        });
    }
    private void getTrips(String departure,String destination) {
        ApiInterface apiInterface = CreateRequest.getRetrofitInstance().create(ApiInterface.class);
        Call<List<TripsResult>> apiCall = apiInterface.getTrips();
        apiCall.enqueue(new Callback<List<TripsResult>>() {
            @Override
            public void onResponse(Call<List<TripsResult>> call, Response<List<TripsResult>> response) {
                List<TripsResult> tripsResults = response.body();
                Toast.makeText(TripList.this, "got products", Toast.LENGTH_SHORT).show();
                setAdapter(Filter(tripsResults,departure,destination));
            }

            @Override
            public void onFailure(Call<List<TripsResult>> call, Throwable t) {
                Toast.makeText(TripList.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAdapter(List<TripsResult> tripsResults) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this,tripsResults, this);
        recyclerView.setAdapter(adapter);

    }

    private List<TripsResult> Filter (List<TripsResult> tripsResults , String departure, String destination){
        int cpt=0;
        for(TripsResult  post:tripsResults ){
            if(post.getDeparture().equals(departure) || post.getDestination().equals(destination)){
                cpt++;
                tripsFiltred = new ArrayList<>();
                tripsFiltred.add(post);
            }
        }
        if (cpt==0){
            tripsFiltred=tripsResults;
        }
        return tripsFiltred;
    }

    @Override
    public void onItemClick(int position ) {
        Intent intent = new Intent(TripList.this,TripDetails.class);
        intent.putExtra("idTrip", tripsFiltred.get(position).getId().toString());
        startActivity(intent);
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
            Intent intent = new Intent(TripList.this, CreateTrip.class);
            startActivity(intent);
        }else if (id == R.id.menu_trip_list) {
            Intent intent = new Intent(TripList.this, TripList.class);
            startActivity(intent);
        }else if (id == R.id.menu_user_profile) {
            Intent intent = new Intent(TripList.this, UserProfileActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_settings) {
            Intent intent = new Intent(TripList.this, SettingsActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TripList.this , MainActivity.class);

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