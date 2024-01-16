package com.example.tri9itest;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TripDetails extends AppCompatActivity {

    private TextView departure,destination,carbrand,time,places,condition,plaque,placestxt,driverName,NumberDriver;
    private Button push;
    private EditText placesnbr;
    private FirebaseAuth authProfile;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_trip_details);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        getSupportActionBar().setTitle("Trip Details");

        departure= findViewById(R.id.departure);
        destination= findViewById(R.id.destination);
        carbrand= findViewById(R.id.carbrand);
        time= findViewById(R.id.time);
        places= findViewById(R.id.placesnbr);
        condition= findViewById(R.id.condition);
        push = findViewById(R.id.deleteReservation);
        plaque = findViewById(R.id.plaque);
        driverName = findViewById(R.id.driverName);
        placestxt = findViewById(R.id.placestxt);
        NumberDriver = findViewById(R.id.NumberDriver);
        placesnbr = findViewById(R.id.placesnbr);

        Intent intent = getIntent();
        String idTrip = intent.getStringExtra("idTrip");


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String  owner = sharedPreferences.getString("UserId", "0") ;

        Log.d("UserIdResult", "UserIdResult in : " + owner);

        getTrip(idTrip);

        push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int placesReserved = Integer.valueOf(String.valueOf(placesnbr.getText()));
                int places = Integer.valueOf(String.valueOf(placestxt.getText()));

                ApiInterface apiInterface = CreateRequest.getRetrofitInstance().create(ApiInterface.class);

                ReservationRequest request = new ReservationRequest(owner,idTrip,placesReserved);

                Call<ReservationRequest> call = apiInterface.createReservation(request);

                call.enqueue(new Callback<ReservationRequest>() {
                    @Override
                    public void onResponse(Call<ReservationRequest> call, Response<ReservationRequest> response) {
                        Toast.makeText(TripDetails.this, "reservation suceed", Toast.LENGTH_SHORT).show();
                        updatePlaces(idTrip,placesReserved,places);

                    }

                    @Override
                    public void onFailure(Call<ReservationRequest> call, Throwable t) {

                    }
                });

            }
        });
    }

    private void getCar(String idCar){

        ApiInterface apiInterface = CreateRequest.getRetrofitInstance().create(ApiInterface.class);
        CarIdRequest request = new CarIdRequest(idCar);
        Call<CarResult> call = apiInterface.getCarInformation(request);
        call.enqueue(new Callback<CarResult>() {
            @Override
            public void onResponse(Call<CarResult> call, Response<CarResult> response) {
                CarResult result = response.body();
                Log.d(TAG, "Response body: " + new Gson().toJson(result));
                carbrand.setText(String.valueOf(result.getBrand()));
                plaque.setText(String.valueOf(result.getPlaque()));
            }

            @Override
            public void onFailure(Call<CarResult> call, Throwable t) {
                Toast.makeText(TripDetails.this, "error car", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updatePlaces(String idTrip,int placesReserved,int places){
        Log.d(TAG, String.valueOf(placesReserved));
        Log.d(TAG, String.valueOf(places));


        int FinalPlaces = places - placesReserved ;

        ApiInterface apiInterface = CreateRequest.getRetrofitInstance().create(ApiInterface.class);
        TripPlaces request = new TripPlaces(idTrip,FinalPlaces);

        Call<TripPlaces> call = apiInterface.updatesTripPlaces(request);

        call.enqueue(new Callback<TripPlaces>() {
            @Override
            public void onResponse(Call<TripPlaces> call, Response<TripPlaces> response) {
                TripPlaces result = response.body();
                Log.d(TAG, "updated successfuly ");
                Intent intent = new Intent(TripDetails.this , ShowTime.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);//pour ne pas revenir en arriere
                startActivity(intent);
                finish();

            }

            @Override
            public void onFailure(Call<TripPlaces> call, Throwable t) {
                Log.e(TAG, "Failed to update trip places", t);
            }
        });
    }

    private void getTrip(String idTrip){
        ApiInterface apiInterface = CreateRequest.getRetrofitInstance().create(ApiInterface.class);
        TripIdRequest request = new TripIdRequest(idTrip);
        Call<TripsResult> apiCall = apiInterface.getTrip(request);
        apiCall.enqueue(new Callback<TripsResult>() {
            @Override
            public void onResponse(Call<TripsResult> call, Response<TripsResult> response) {
                TripsResult trip = response.body();
                String departureDetail = String.valueOf(trip.getDeparture());
                String destinationDetail = String.valueOf(trip.getDestination());
                String timeTrip = String.valueOf(trip.getTime());
                String idCar = String.valueOf(trip.getIdCar());
                String conditionDetail = String.valueOf(trip.getCondition());
                String idDriver = String.valueOf(trip.getIdCreator());
                int places = Integer.valueOf(trip.getPlaces());
                setDriver(idDriver);
                departure.setText(departureDetail);
                destination.setText(destinationDetail);
                time.setText(timeTrip);
                condition.setText(conditionDetail);
                placestxt.setText(String.valueOf(places));
                getCar(idCar);
                Toast.makeText(TripDetails.this, "got products", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<TripsResult> call, Throwable t) {
                Toast.makeText(TripDetails.this, "error trip", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setDriver(String idUser){
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        //Extracting user reference from db for "registered users
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null){
                    String username = readUserDetails.userName;
                    String phone = readUserDetails.phone;
                    driverName.setText(username);
                    NumberDriver.setText(phone);

                }else{
                    Toast.makeText(TripDetails.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TripDetails.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(TripDetails.this, CreateTrip.class);
            startActivity(intent);
        }else if (id == R.id.menu_trip_list) {
            Intent intent = new Intent(TripDetails.this, TripList.class);
            startActivity(intent);
        }else if (id == R.id.menu_user_profile) {
            Intent intent = new Intent(TripDetails.this, UserProfileActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_settings) {
            Intent intent = new Intent(TripDetails.this, SettingsActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TripDetails.this , MainActivity.class);

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