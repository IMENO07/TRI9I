package com.example.tri9itest;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity {
    private TextView usernametxtv, fullnametxtv, emailtxtv, phonetxtv,gendertxtv;
    private String username, fullname, email, phone,gender;
    private ImageView imageView;

    private Button btnAddCar , btnCarList ;
    private FirebaseAuth authProfile;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Objects.requireNonNull(getSupportActionBar()).setTitle("profil");

        usernametxtv= findViewById(R.id.username);
        fullnametxtv= findViewById(R.id.fullName);
        btnCarList= findViewById(R.id.btn_car_list);
        emailtxtv= findViewById(R.id.email);
        phonetxtv= findViewById(R.id.phonenum);
        gendertxtv= findViewById(R.id.gender);
        btnCarList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(UserProfileActivity.this,CarListEdit.class);
                startActivity(intent);
            }
        });
        btnAddCar = findViewById(R.id.btn_add_car); btnAddCar.setOnClickListener(new  View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(UserProfileActivity.this,AddCar.class);
                startActivity(intent);
            }
        });
        imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this , UploadProfilPicActivity.class);
                startActivity(intent);
            }
        });

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null){
            Toast.makeText(this, "Something went wrong , User's details are not available at the moment", Toast.LENGTH_SHORT).show();
        }
        else {
            checkifEmailIsVerified(firebaseUser);
            showUserProfil(firebaseUser);
        }
    }

    private void checkifEmailIsVerified(FirebaseUser firebaseUser) {
        if(firebaseUser.isEmailVerified()){
            showAlertDialog();

        }
    }

    private void showAlertDialog() {
        //setup the Alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now . You can not login without email verification");

        //Open email app
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent =new Intent(Intent.ACTION_MAIN);
                // intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        //craet the alert box
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showUserProfil(FirebaseUser firebaseUser) {
        String userId = firebaseUser.getUid();

        //Extracting user reference from db for "registered users
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null){
                    fullname = readUserDetails.fullName;
                    email = firebaseUser.getEmail();
                    username = readUserDetails.userName;
                    gender = readUserDetails.gender;
                    phone = readUserDetails.phone;

                    fullnametxtv.setText(fullname);
                    emailtxtv.setText(email);
                    usernametxtv.setText(username);
                    gendertxtv.setText(gender);
                    phonetxtv.setText(phone);

                    //set image
                    Uri uri = firebaseUser.getPhotoUrl();
                    Picasso.get().load(uri).into(imageView);
                }else{
                    Toast.makeText(UserProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(UserProfileActivity.this, CreateTrip.class);
            startActivity(intent);
        }else if (id == R.id.menu_trip_list) {
            Intent intent = new Intent(UserProfileActivity.this, TripList.class);
            startActivity(intent);
        }else if (id == R.id.menu_user_profile) {
            Intent intent = new Intent(UserProfileActivity.this, UserProfileActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_settings) {
            Intent intent = new Intent(UserProfileActivity.this, SettingsActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserProfileActivity.this , MainActivity.class);

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