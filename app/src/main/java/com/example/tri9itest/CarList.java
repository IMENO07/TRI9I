package com.example.tri9itest;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public  class CarList extends AppCompatActivity implements RecyclerListner, recyclerListnerLong {
    private RecyclerView recyclerView;
    private List<CarResult> carsFiltred;
    private Button addCar;
    private int position;
    private FirebaseAuth authProfile;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_car_list);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        getSupportActionBar().setTitle("Car List");

        recyclerView = findViewById(R.id.backToMenu);
        addCar= findViewById(R.id.carList);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String  UserId = sharedPreferences.getString("UserId", "0") ;
        getCars(UserId);

        addCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarList.this,AddCar.class);
                startActivity(intent);
            }
        });


        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(position);

            }
        });



        recyclerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemLongClick(position);
                getCars(UserId);
                return false;
            }
        });

    }



    private void getCars(String owner) {
        ApiInterface apiInterface = CreateRequest.getRetrofitInstance().create(ApiInterface.class);
        Call<List<CarResult>> apiCall = apiInterface.getCars();
        apiCall.enqueue(new Callback<List<CarResult>>() {

            @Override
            public void onResponse(Call<List<CarResult>> call, Response<List<CarResult>> response) {
                List<CarResult> carResults = response.body();
                Toast.makeText(CarList.this, "got products", Toast.LENGTH_SHORT).show();
                setAdapter(Filter(carResults,owner));
            }

            @Override
            public void onFailure(Call<List<CarResult>> call, Throwable t) {
                Toast.makeText(CarList.this, "error", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void setAdapter(List<CarResult> carResult) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewCars adapterCar = new RecyclerViewCars(this,carResult, this,this);
        recyclerView.setAdapter(adapterCar);

    }

    private List<CarResult> Filter(List<CarResult> carResults, String owner) {
        int cpt = 0;
        carsFiltred = new ArrayList<>();

        for (CarResult post : carResults) {
            if (post.getOwner().equals(owner)) {
                cpt++;
                carsFiltred.add(post);
            }
        }
        if (cpt == 0) {
            Toast.makeText(this, "no cars", Toast.LENGTH_SHORT).show();
            return carsFiltred;
        }else {
            return carsFiltred;
        }


    }


    @Override
    public void onItemLongClick(int position) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_dialogbox_delete, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setView(dialogView)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String idCar = carsFiltred.get(position).getId();
                        ApiInterface apiInterface = CreateRequest.getRetrofitInstance().create(ApiInterface.class);
                        CarIdRequest request = new CarIdRequest(idCar);

                        Call<CarResult> call = apiInterface.DropCar(request);

                        call.enqueue(new Callback<CarResult>() {
                            @Override
                            public void onResponse(Call<CarResult> call, Response<CarResult> response) {
                                CarResult result = response.body();
                                Log.d(TAG, "Response body: " + new Gson().toJson(result));
                                Toast.makeText(CarList.this, "Car deleted", Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onFailure(Call<CarResult> call, Throwable t) {
                                Toast.makeText(CarList.this, "error", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }



    private void  pushTrip(String departure,String destination, String time , String condition , String idCar ,int places ){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String  owner = sharedPreferences.getString("UserId", "0") ;
        ApiInterface apiInterface = CreateRequest.getRetrofitInstance().create(ApiInterface.class);
        TripsResult tripRequest = new TripsResult(departure, destination, time, condition,idCar,places,owner);
        Call<TripsResult> call = apiInterface.getTripInformation(tripRequest);
        call.enqueue(new Callback<TripsResult>() {
            @Override
            public void onResponse(Call<TripsResult> call, Response<TripsResult> response) {
                getTrip(owner);
                Toast.makeText(CarList.this, "suceed", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CarList.this,ShowTime.class);
                startActivity(intent);
                finish();

            }

            @Override
            public void onFailure(Call<TripsResult> call, Throwable t) {
                Toast.makeText(CarList.this, "echec"+ t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Bundle extras = getIntent().getExtras();
        String departure = extras.getString("departure");
        String destination = extras.getString("destination");
        String time = extras.getString("time");
        String condition = extras.getString("condition");
        String idCar = carsFiltred.get(position).getId();
        int places = carsFiltred.get(position).getPlaces();
        Toast.makeText(CarList.this, "idCar", Toast.LENGTH_SHORT).show();
        pushTrip(departure , destination,  time , condition ,  idCar , places);

    }
    private void getTrip(String idUser){
        ApiInterface apiInterface = CreateRequest.getRetrofitInstance().create(ApiInterface.class);
        TripCreator request = new TripCreator(idUser);
        Call<List<TripsResult>> apiCall = apiInterface.getTripByUser(request);
        apiCall.enqueue(new Callback<List<TripsResult>>() {
            @Override
            public void onResponse(Call<List<TripsResult>> call, Response<List<TripsResult>> response) {
                List<TripsResult> trip = response.body();
                String id= String.valueOf(trip.get(0).getId());
                CreateReservationOwner(idUser,id);
                Toast.makeText(CarList.this, "got products", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<List<TripsResult>> call, Throwable t) {
                Log.d(TAG, String.valueOf(t));
                Toast.makeText(CarList.this, "error trip", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void CreateReservationOwner(String owner,String idTrip){
        ApiInterface apiInterface = CreateRequest.getRetrofitInstance().create(ApiInterface.class);

        ReservationRequest request = new ReservationRequest(owner,idTrip,0);

        Call<ReservationRequest> call = apiInterface.createReservation(request);

        call.enqueue(new Callback<ReservationRequest>() {
            @Override
            public void onResponse(Call<ReservationRequest> call, Response<ReservationRequest> response) {
                Toast.makeText(CarList.this, "reservation suceed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ReservationRequest> call, Throwable t) {

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
            Intent intent = new Intent(CarList.this, CreateTrip.class);
            startActivity(intent);
        }else if (id == R.id.menu_trip_list) {
            Intent intent = new Intent(CarList.this, TripList.class);
            startActivity(intent);
        }else if (id == R.id.menu_user_profile) {
            Intent intent = new Intent(CarList.this, UserProfileActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_settings) {
            Intent intent = new Intent(CarList.this, SettingsActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CarList.this , MainActivity.class);

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