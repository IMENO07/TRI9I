package com.example.tri9itest;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public  class CarListEdit extends AppCompatActivity implements recyclerListnerLong , RecyclerListner {
    private RecyclerView recyclerView;
    private List<CarResult> carsFiltred;
    private Button addCar, backToMenu;
    private int position;
    private FirebaseAuth authProfile;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_car_list_edit);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        getSupportActionBar().setTitle("Edit car List");

        recyclerView = findViewById(R.id.backToMenu);
        backToMenu= findViewById(R.id.button);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String  owner = sharedPreferences.getString("UserId", "0") ;
        getCars(owner);



        backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });


        recyclerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemLongClick(position);
                getCars(owner);
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
                Toast.makeText(CarListEdit.this, "got products", Toast.LENGTH_SHORT).show();
                setAdapter(Filter(carResults,owner));
            }

            @Override
            public void onFailure(Call<List<CarResult>> call, Throwable t) {
                Toast.makeText(CarListEdit.this, "error", Toast.LENGTH_SHORT).show();

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
            carsFiltred = carResults;
        }

        return carsFiltred;
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
                                Toast.makeText(CarListEdit.this, "Car deleted", Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onFailure(Call<CarResult> call, Throwable t) {
                                Toast.makeText(CarListEdit.this, "error", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }

    @Override
    public void onItemClick(int position) {

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
            Intent intent = new Intent(CarListEdit.this, CreateTrip.class);
            startActivity(intent);
        }else if (id == R.id.menu_trip_list) {
            Intent intent = new Intent(CarListEdit.this, TripList.class);
            startActivity(intent);
        }else if (id == R.id.menu_user_profile) {
            Intent intent = new Intent(CarListEdit.this, UserProfileActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_settings) {
            Intent intent = new Intent(CarListEdit.this, SettingsActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CarListEdit.this , MainActivity.class);

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
