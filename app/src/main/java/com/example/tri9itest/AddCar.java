package com.example.tri9itest;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCar extends AppCompatActivity {

    private EditText brandCar, modalCar, plaqueCar, placesCar;
    private Button pushCarButton;
    private FirebaseAuth authProfile;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_car);
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        getSupportActionBar().setTitle("Add Car");

        //findingById

        brandCar = findViewById(R.id.brandCar);
        modalCar = findViewById(R.id.modalCar);
        placesCar = findViewById(R.id.placesCar);
        plaqueCar = findViewById(R.id.conditionEditText);
        pushCarButton = findViewById(R.id.pushCarButton);

        //buttonClickListener

        pushCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String brand = String.valueOf(brandCar.getText());
                String modal = String.valueOf(modalCar.getText());
                int plaque = Integer.valueOf(plaqueCar.getText().toString());
                int places = Integer.valueOf(placesCar.getText().toString());

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String  owner = sharedPreferences.getString("UserId", "0") ;
                Log.d("UserIdResult", "UserIdResult in : AddCar" + owner);
                ApiInterface apiInterface = CreateRequest.getRetrofitInstance().create(ApiInterface.class);
                CarResult carRequest = new CarResult(owner, brand, modal, places , plaque);

                Call<CarResult> call = apiInterface.getCarInformation(carRequest);
                call.enqueue(new Callback<CarResult>() {
                    @Override
                    public void onResponse(Call<CarResult> call, Response<CarResult> response) {
                        Toast.makeText(AddCar.this, "suceed", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<CarResult> call, Throwable t) {
                        Toast.makeText(AddCar.this, "echec! "+ t.getMessage()
                                , Toast.LENGTH_SHORT).show();
                    }
                });
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
            Intent intent = new Intent(AddCar.this, CreateTrip.class);
            startActivity(intent);
        }else if (id == R.id.menu_trip_list) {
            Intent intent = new Intent(AddCar.this, TripList.class);
            startActivity(intent);
        }else if (id == R.id.menu_user_profile) {
            Intent intent = new Intent(AddCar.this, UserProfileActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_settings) {
            Intent intent = new Intent(AddCar.this, SettingsActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddCar.this , MainActivity.class);

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