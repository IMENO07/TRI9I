package com.example.tri9itest;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTrip extends AppCompatActivity {

    private EditText departureEditText,arrivalEditText,conditionEditText;
    private Button pushTripButton,timePicker;
    private String time;
    private FirebaseAuth authProfile;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_trip);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        getSupportActionBar().setTitle("Add Trip");


        //findingById

        departureEditText = findViewById(R.id.departureEditText);
        arrivalEditText = findViewById(R.id.arrivalEditText);
        timePicker = findViewById(R.id.timePicker);
        conditionEditText = findViewById(R.id.conditionEditText);
        pushTripButton = findViewById(R.id.pushTripButton);

        //buttonClickListener

        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                int mins = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateTrip.this, android.R.style.Theme_Material_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        c.set(Calendar.MINUTE,minute);
                        c.setTimeZone(TimeZone.getDefault());
                        SimpleDateFormat format = new SimpleDateFormat("k:mm");
                        time = format.format(c.getTime());
                    }
                }, hours, mins, false);
                timePickerDialog.show();
            }
        });

        pushTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String departure = String.valueOf(departureEditText.getText());
                String destination = String.valueOf(arrivalEditText.getText());
                String condition = String.valueOf(conditionEditText.getText());

                Intent intent = new Intent(CreateTrip.this,CarList.class);
                intent.putExtra("departure", departure);
                intent.putExtra("destination",destination);
                intent.putExtra("time", time);
                intent.putExtra("condition", condition);
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
            Intent intent = new Intent(CreateTrip.this, CreateTrip.class);
            startActivity(intent);
        }else if (id == R.id.menu_trip_list) {
            Intent intent = new Intent(CreateTrip.this, TripList.class);
            startActivity(intent);
        }else if (id == R.id.menu_user_profile) {
            Intent intent = new Intent(CreateTrip.this, UserProfileActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_settings) {
            Intent intent = new Intent(CreateTrip.this, SettingsActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CreateTrip.this , MainActivity.class);

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