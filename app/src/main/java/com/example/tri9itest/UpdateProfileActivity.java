package com.example.tri9itest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfileActivity extends AppCompatActivity {
    private EditText edittxtUpdateUsername, edittxtUpdateFullname, edittxtUpdatePhone;
    private RadioGroup radioGrpUpdateGender;
    private RadioButton radioBtnUpdateGenderSelected;
    private String txtFullname , txtUsername, txtPhone, txtGender;
    private FirebaseAuth authprofile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        getSupportActionBar().setTitle("Update User profile");

        edittxtUpdateUsername = findViewById(R.id.username);
        edittxtUpdatePhone = findViewById(R.id.phonenmbr);
        edittxtUpdateFullname = findViewById(R.id.name);
        radioGrpUpdateGender = findViewById(R.id.gender);
        authprofile =FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = authprofile.getCurrentUser();

        //show profil data
        showProfil(firebaseUser);

        ImageView profil_pic = findViewById(R.id.profilPic);
        profil_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfileActivity.this, UploadProfilPicActivity.class);
                startActivity(intent);
                finish();
            }
        });
        TextView butnUpdateEmail = findViewById(R.id.btn_profile_update_email);
        butnUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfileActivity.this, UpdateEmailActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button butnUpdateProfile = findViewById(R.id.btn_update_profile);
        butnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(firebaseUser);
            }
        });
    }

    private void updateProfile(FirebaseUser firebaseUser) {
        int selectedGenderId = radioGrpUpdateGender.getCheckedRadioButtonId();
        radioBtnUpdateGenderSelected = findViewById(selectedGenderId);

        //validate phone number
        String mobilRegex = "[0][5-7][0-9]{8}";
        Matcher mobileMatcher;
        Pattern mobilePattern = Pattern.compile(mobilRegex);
        mobileMatcher = mobilePattern.matcher(txtPhone);

        if (TextUtils.isEmpty(txtFullname)){
            Toast.makeText(UpdateProfileActivity.this , "Please enter your Full Name", Toast.LENGTH_LONG).show();
            edittxtUpdateFullname.setError("Full Name required");
            edittxtUpdateFullname.requestFocus();

        } else if (TextUtils.isEmpty(txtUsername)) {
            Toast.makeText(UpdateProfileActivity.this , "Please enter your User Name", Toast.LENGTH_LONG).show();
            edittxtUpdateUsername.setError("User Name required");
            edittxtUpdateUsername.requestFocus();

        } else if (TextUtils.isEmpty(radioBtnUpdateGenderSelected.getText())) {
            Toast.makeText(UpdateProfileActivity.this,"please select your gender",Toast.LENGTH_LONG).show();
            radioBtnUpdateGenderSelected.setError("Gender is required");
            radioBtnUpdateGenderSelected.requestFocus();

        } else if (TextUtils.isEmpty(txtPhone)) {
            Toast.makeText(UpdateProfileActivity.this , "Please enter your phone number", Toast.LENGTH_LONG).show();
            edittxtUpdatePhone.setError("Phone number is required");
            edittxtUpdatePhone.requestFocus();

        } else if (txtPhone.length() !=10) {
            Toast.makeText(UpdateProfileActivity.this , "Please re-enter your phone number", Toast.LENGTH_LONG).show();
            edittxtUpdatePhone.setError("Phone number should be 10 digits");
            edittxtUpdatePhone.requestFocus();

        } else if (!mobileMatcher.find()) {
            Toast.makeText(UpdateProfileActivity.this , "Please re-enter your phone number", Toast.LENGTH_LONG).show();
            edittxtUpdatePhone.setError("Phone number is not valide");
            edittxtUpdatePhone.requestFocus();

        }else {
            txtGender = radioBtnUpdateGenderSelected.getText().toString();
            txtFullname = edittxtUpdateFullname.getText().toString();
            txtUsername = edittxtUpdateUsername.getText().toString();
            txtPhone = edittxtUpdatePhone.getText().toString();

            //ndkhlohom l bd
            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(txtFullname , txtUsername , txtGender , txtPhone);

            //Extract user ref from db
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");
            String userId = firebaseUser.getUid();
            reference.child(userId).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        UserProfileChangeRequest profilUpdate = new UserProfileChangeRequest.Builder().setDisplayName(txtFullname).build();
                        firebaseUser.updateProfile(profilUpdate);

                        Toast.makeText(UpdateProfileActivity.this, "Update Successful", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(UpdateProfileActivity.this, UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }else {
                        try {
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });

        }
    }

    private void showProfil(FirebaseUser firebaseUser) {
        String userIdofRegistered= firebaseUser.getUid();

        //Exracting user ref from database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");
        reference.child(userIdofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails =snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null){
                    txtFullname = readUserDetails.fullName;
                    txtUsername = readUserDetails.userName;
                    txtGender = readUserDetails.gender;
                    txtPhone = readUserDetails.phone;

                    edittxtUpdateFullname.setText(txtFullname);
                    edittxtUpdateUsername.setText(txtUsername);
                    edittxtUpdatePhone.setText(txtPhone);

                    if(txtGender.equals("Male")){
                        radioBtnUpdateGenderSelected = findViewById(R.id.radio_male);
                    }else {
                        radioBtnUpdateGenderSelected = findViewById(R.id.radio_female);
                    }
                    radioBtnUpdateGenderSelected.setChecked(true);
                }else {
                    Toast.makeText(UpdateProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

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
            Intent intent = new Intent(UpdateProfileActivity.this, CreateTrip.class);
            startActivity(intent);
        }else if (id == R.id.menu_trip_list) {
            Intent intent = new Intent(UpdateProfileActivity.this, TripList.class);
            startActivity(intent);
        }else if (id == R.id.menu_user_profile) {
            Intent intent = new Intent(UpdateProfileActivity.this, UserProfileActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_settings) {
            Intent intent = new Intent(UpdateProfileActivity.this, SettingsActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_settings) {
            Toast.makeText(this, "menu_settings", Toast.LENGTH_SHORT).show();
        }else if (id == R.id.menu_logout) {
            authprofile.signOut();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UpdateProfileActivity.this , MainActivity.class);

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