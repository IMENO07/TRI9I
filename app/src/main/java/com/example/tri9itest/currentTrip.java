package com.example.tri9itest;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class currentTrip extends AppCompatActivity {

    TextView departure, destination, carbrand, time, places, condition,textView2, plaque, placestxt, driverName, NumberDriver;
    Button deleteReservation,passengerList;
    EditText placesnbr;
    int placeshello;
    String idTrip,idReservationOut;
    LinearLayout DriverInfo;
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_current_trip);
        getSupportActionBar().hide();

        departure = findViewById(R.id.departure);
        destination = findViewById(R.id.destination);
        carbrand = findViewById(R.id.carbrand);
        time = findViewById(R.id.time);
        places = findViewById(R.id.placesnbr);
        condition = findViewById(R.id.condition);
        deleteReservation = findViewById(R.id.deleteReservation);
        plaque = findViewById(R.id.plaque);
        driverName = findViewById(R.id.driverName);
        placestxt = findViewById(R.id.placestxt);
        NumberDriver = findViewById(R.id.NumberDriver);
        placesnbr = findViewById(R.id.placesnbr);
        DriverInfo = findViewById(R.id.DriverInfo);
        textView2 = findViewById(R.id.textView2);
        passengerList = findViewById(R.id.displayPassenger);
        passengerList.setVisibility(View.GONE);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String owner = sharedPreferences.getString("UserId", "0");
        Log.d("UserIdResult", "UserIdResult in : " + owner);

        getReservation(owner);

        passengerList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(currentTrip.this,ListPassengers.class);
                startActivity(intent);
            }
        });


        deleteReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int placesReserved = Integer.valueOf(String.valueOf(placestxt.getText()));

                ApiInterface apiInterface = CreateRequest.getRetrofitInstance().create(ApiInterface.class);
                ReservationId request = new ReservationId(idReservationOut);
                Call<ReservationId> call = apiInterface.dropReservation(request);
                call.enqueue(new Callback<ReservationId>() {
                    @Override
                    public void onResponse(Call<ReservationId> call, Response<ReservationId> response) {
                        if (response.isSuccessful()) {
                            updatePlaces(idTrip, placesReserved, placeshello);
                            Toast.makeText(currentTrip.this, "Reservation deleted", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(currentTrip.this, ShowTime.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(currentTrip.this, "Failed to delete reservation", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ReservationId> call, Throwable t) {
                        Toast.makeText(currentTrip.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    private void getCar(String idCar) {
        ApiInterface apiInterface = CreateRequest.getRetrofitInstance().create(ApiInterface.class);
        CarIdRequest request = new CarIdRequest(idCar);
        Call<CarResult> call = apiInterface.getCarInformation(request);
        call.enqueue(new Callback<CarResult>() {
            @Override
            public void onResponse(Call<CarResult> call, Response<CarResult> response) {
                if (response.isSuccessful()) {
                    CarResult result = response.body();
                    if (result != null) {
                        carbrand.setText(String.valueOf(result.getBrand()));
                        plaque.setText(String.valueOf(result.getPlaque()));
                    } else {
                        Toast.makeText(currentTrip.this, "Failed to get car information", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(currentTrip.this, "Failed to get car information", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CarResult> call, Throwable t) {
                Toast.makeText(currentTrip.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePlaces(String idTrip, int placesReserved, int places) {
        Log.d(TAG, String.valueOf(placesReserved));
        Log.d(TAG, String.valueOf(places));
        int finalPlaces = places + placesReserved;
        Log.d(TAG, String.valueOf(finalPlaces));

        ApiInterface apiInterface = CreateRequest.getRetrofitInstance().create(ApiInterface.class);
        TripPlaces request = new TripPlaces(idTrip, finalPlaces);

        Call<TripPlaces> call = apiInterface.updatesTripPlaces(request);

        call.enqueue(new Callback<TripPlaces>() {
            @Override
            public void onResponse(Call<TripPlaces> call, Response<TripPlaces> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(currentTrip.this, "Trip places updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(currentTrip.this, "Failed to update trip places", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TripPlaces> call, Throwable t) {
                Toast.makeText(currentTrip.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTrip(String idTrip,String idUser) {
        ApiInterface apiInterface = CreateRequest.getRetrofitInstance().create(ApiInterface.class);
        TripIdRequest request = new TripIdRequest(idTrip);
        Call<TripsResult> apiCall = apiInterface.getTrip(request);
        apiCall.enqueue(new Callback<TripsResult>() {
            @Override
            public void onResponse(Call<TripsResult> call, Response<TripsResult> response) {
                if (response.isSuccessful()) {
                    TripsResult trip = response.body();
                    if (trip != null) {
                        String departureDetail = String.valueOf(trip.getDeparture());
                        String destinationDetail = String.valueOf(trip.getDestination());
                        String timeTrip = String.valueOf(trip.getTime());
                        String idCar = String.valueOf(trip.getIdCar());
                        String conditionDetail = String.valueOf(trip.getCondition());
                        String idDriver = String.valueOf(trip.getIdCreator());
                        if(idUser.equals(idDriver)){
                            passengerList.setVisibility(View.VISIBLE);
                            deleteReservation.setVisibility(View.GONE);
                            DriverInfo.setVisibility(View.GONE);
                            placestxt.setVisibility(View.GONE);
                            textView2.setVisibility(View.GONE);
                        }
                        Integer placesTrip = Integer.valueOf(trip.getPlaces());
                        setDriver(idDriver);
                        departure.setText(departureDetail);
                        destination.setText(destinationDetail);
                        time.setText(timeTrip);
                        condition.setText(conditionDetail);
                        placeshello=placesTrip;
                        getCar(idCar);
                        Toast.makeText(currentTrip.this, "Got trip details", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(currentTrip.this, "Failed to get trip details", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(currentTrip.this, "Failed to get trip details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TripsResult> call, Throwable t) {
                Toast.makeText(currentTrip.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getReservation(String idUser) {
        idTrip="";idReservationOut="";

        ApiInterface apiInterface = CreateRequest.getRetrofitInstance().create(ApiInterface.class);
        ReservationIdUser request = new ReservationIdUser(idUser);
        Call<List<Reservation>> apiCall = apiInterface.getReservationByUser(request);
        apiCall.enqueue(new Callback<List<Reservation>>() {
            @Override
            public void onResponse(Call<List<Reservation>> call, Response<List<Reservation>> response) {
                if (response.isSuccessful()) {
                    List<Reservation> reservations = response.body();
                    if (reservations != null && !reservations.isEmpty()) {
                        Reservation reservation = reservations.get(0);
                        idTrip = String.valueOf(reservation.getIdTrip());
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("idTrip",reservation.getIdTrip());
                        editor.commit();
                        idReservationOut = String.valueOf(reservation.getIdReservation());
                        int places = reservation.getPlacesReservation();
                        placestxt.setText(String.valueOf(places));

                        getTrip(idTrip,idUser);
                        Toast.makeText(currentTrip.this, "Got reservation: " + idTrip, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(currentTrip.this, "No reservations found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(currentTrip.this, "Failed to get reservation", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Reservation>> call, Throwable t) {
                Toast.makeText(currentTrip.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDriver(String idUser) {
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    String username = readUserDetails.userName;
                    String phone = readUserDetails.phone;
                    driverName.setText(username);
                    NumberDriver.setText(phone);
                } else {
                    Toast.makeText(currentTrip.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(currentTrip.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(currentTrip.this, CreateTrip.class);
            startActivity(intent);
        }else if (id == R.id.menu_trip_list) {
            Intent intent = new Intent(currentTrip.this, TripList.class);
            startActivity(intent);
        }else if (id == R.id.menu_user_profile) {
            Intent intent = new Intent(currentTrip.this, UserProfileActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_settings) {
            Intent intent = new Intent(currentTrip.this, SettingsActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(currentTrip.this , MainActivity.class);

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
