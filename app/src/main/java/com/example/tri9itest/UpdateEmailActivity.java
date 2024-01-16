package com.example.tri9itest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateEmailActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private EditText editTextNewEmail , editTextPwd;
    private TextView textViewAthenticated;
    private String userOldEmail, usernewEmail;
    private Button btnUpdateEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);

        getSupportActionBar().setTitle("Update Email");
        editTextNewEmail = findViewById(R.id.editxt_update_email_new);
        editTextPwd =findViewById(R.id.edittxt_verify_password);
        textViewAthenticated = findViewById(R.id.txtv_update_email_authentificated);
        btnUpdateEmail = findViewById(R.id.btn_update_email);

        btnUpdateEmail.setEnabled(false);
        editTextNewEmail.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        //njibo email 9dim et on l'affiche
        userOldEmail = firebaseUser.getEmail();
        TextView textViewOldEmail = findViewById(R.id.txtv_update_email_old);
        textViewOldEmail.setText(userOldEmail);

        if (firebaseUser.equals("")){
            Toast.makeText(this, "Something went wrong ! user's details are not avilable", Toast.LENGTH_SHORT).show();
        }else{
            reAuthenticate(firebaseUser);
        }
    }

    private void reAuthenticate(FirebaseUser firebaseUser) {
        Button buttonVerifyUser = findViewById(R.id.btn_authenticate_user);
        buttonVerifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userpwd = editTextPwd.getText().toString();
                if (TextUtils.isEmpty(userpwd)){
                    Toast.makeText(UpdateEmailActivity.this, "Password is needed to continuie", Toast.LENGTH_SHORT).show();
                    editTextPwd.setError("Please enter your password fot authentification");
                    editTextPwd.requestFocus();
                }else {
                    AuthCredential credential = EmailAuthProvider.getCredential(userOldEmail, userpwd);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(UpdateEmailActivity.this, "Password has been verified"+ "You can update email now", Toast.LENGTH_SHORT).show();

                                textViewAthenticated.setText("You can update your email now");

                                //disable edit text
                                editTextNewEmail.setEnabled(true);
                                editTextPwd.setEnabled(false);
                                btnUpdateEmail.setEnabled(true);
                                buttonVerifyUser.setEnabled(false);

                                //changer a couleur du boutton
                                btnUpdateEmail.setBackgroundTintList(ContextCompat.getColorStateList(UpdateEmailActivity.this,R.color.teal_200));
                                btnUpdateEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        usernewEmail = editTextNewEmail.getText().toString();
                                        if (TextUtils.isEmpty(usernewEmail)){
                                            Toast.makeText(UpdateEmailActivity.this, "New email is required", Toast.LENGTH_SHORT).show();
                                            editTextNewEmail.setError("Please enter your new email");
                                            editTextNewEmail.requestFocus();
                                        } else if (!Patterns.EMAIL_ADDRESS.matcher(usernewEmail).matches()) {
                                            Toast.makeText(UpdateEmailActivity.this, "Valid email is required", Toast.LENGTH_SHORT).show();
                                            editTextNewEmail.setError("Please provide valid email");
                                            editTextNewEmail.requestFocus();
                                        } else if (userOldEmail.matches(usernewEmail)) {
                                            Toast.makeText(UpdateEmailActivity.this, "New email can not be the same as old one", Toast.LENGTH_SHORT).show();
                                            editTextNewEmail.setError("Please enter new email");
                                            editTextNewEmail.requestFocus();
                                        }else {
                                            updateEmail(firebaseUser);
                                        }
                                    }
                                });
                            }else {
                                try {
                                    throw task.getException();
                                }catch (Exception e){
                                    Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    });
                }
            }
        });
    }

    private void updateEmail(FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(usernewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()){
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(UpdateEmailActivity.this, "The email has been updated . Please verify your new email", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UpdateEmailActivity.this, UserProfileActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    try {
                        throw task.getException();
                    }catch (Exception e){
                        Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
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
            Intent intent = new Intent(UpdateEmailActivity.this, CreateTrip.class);
            startActivity(intent);
        }else if (id == R.id.menu_trip_list) {
            Intent intent = new Intent(UpdateEmailActivity.this, TripList.class);
            startActivity(intent);
        }else if (id == R.id.menu_user_profile) {
            Intent intent = new Intent(UpdateEmailActivity.this, UserProfileActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_settings) {
            Intent intent = new Intent(UpdateEmailActivity.this, SettingsActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_settings) {
            Toast.makeText(this, "menu_settings", Toast.LENGTH_SHORT).show();
        }else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UpdateEmailActivity.this , MainActivity.class);

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