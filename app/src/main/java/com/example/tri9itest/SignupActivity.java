package com.example.tri9itest;

import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private EditText fullNameEditText;
    private EditText userNameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private RadioGroup genderradiogrp;
    private RadioButton genderselected;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private static final String TAG ="SignupActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().setTitle("Inscription");

        fullNameEditText = findViewById(R.id.signupname);
        userNameEditText = findViewById(R.id.signupusername);
        emailEditText = findViewById(R.id.signupmail);
        phoneEditText = findViewById(R.id.signupphonenmbr);
        passwordEditText = findViewById(R.id.signuppassword);
        confirmPasswordEditText = findViewById(R.id.signupconfpassword);
        genderradiogrp = findViewById(R.id.signupgender);
        genderradiogrp.clearCheck();

        Button signupButton = findViewById(R.id.btnSignUp);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //obtenir les info

                int selectedGenderid = genderradiogrp.getCheckedRadioButtonId();
                genderselected = findViewById(selectedGenderid);


                String fullName = fullNameEditText.getText().toString();
                String userName = userNameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                String textGender ;

                //validate phone number
                String mobilRegex = "[0][5-7][0-9]{8}";
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobilRegex);
                mobileMatcher = mobilePattern.matcher(phone);

                if (TextUtils.isEmpty(fullName)){
                    Toast.makeText(SignupActivity.this , "Please enter your Full name", Toast.LENGTH_LONG).show();
                    fullNameEditText.setError("Full name is required");
                    fullNameEditText.requestFocus();

                } else if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(SignupActivity.this , "Please enter your User name", Toast.LENGTH_LONG).show();
                    userNameEditText.setError("User name is required");
                    userNameEditText.requestFocus();

                } else if (TextUtils.isEmpty(email)){
                    Toast.makeText(SignupActivity.this , "Please enter your Email adresse", Toast.LENGTH_LONG).show();
                    emailEditText.setError("Email is required");
                    emailEditText.requestFocus();

                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(SignupActivity.this , "Please re-enter a valid email", Toast.LENGTH_LONG).show();
                    emailEditText.setError("A valid Email is required");
                    emailEditText.requestFocus();

                } else if (genderradiogrp.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(SignupActivity.this,"please select your gender",Toast.LENGTH_LONG).show();
                    genderselected.setError("Gender is required");
                    genderselected.requestFocus();

                } else if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(SignupActivity.this , "Please enter your phone number", Toast.LENGTH_LONG).show();
                    phoneEditText.setError("Phone number is required");
                    phoneEditText.requestFocus();

                } else if (phone.length() !=10) {
                    Toast.makeText(SignupActivity.this , "Please re-enter your phone number", Toast.LENGTH_LONG).show();
                    phoneEditText.setError("Phone number should be 10 digits");
                    phoneEditText.requestFocus();

                } else if (!mobileMatcher.find()) {
                    Toast.makeText(SignupActivity.this , "Please re-enter your phone number", Toast.LENGTH_LONG).show();
                    phoneEditText.setError("Phone number is not valide");
                    phoneEditText.requestFocus();

                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignupActivity.this , "Please enter your passeword", Toast.LENGTH_LONG).show();
                    passwordEditText.setError("Passeword required");
                    passwordEditText.requestFocus();

                } else if (password.length() < 8) {
                    Toast.makeText(SignupActivity.this , "Passeword should at least have 8 digits", Toast.LENGTH_LONG).show();
                    passwordEditText.setError("Weak passeword");
                    passwordEditText.requestFocus();

                } else if (TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(SignupActivity.this , "Veillez confirmer votre mot de passe s'il vous plait", Toast.LENGTH_LONG).show();
                    confirmPasswordEditText.setError("Confirmation du mot de passe Demandé");
                    confirmPasswordEditText.requestFocus();

                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignupActivity.this , "Le mot de passe n'est pas compatible", Toast.LENGTH_LONG).show();
                    confirmPasswordEditText.setError("Confirmation du mot de passe Demandé");
                    confirmPasswordEditText.requestFocus();
                    //effacer les chapmps du mot de passe
                    passwordEditText.clearComposingText();
                    confirmPasswordEditText.clearComposingText();
                }else {
                    textGender = genderselected.getText().toString();
                    signUp(fullName, userName, email, textGender ,phone, password);
                }
            }
        });
    }

    private void signUp(String fullName, String userName, String email, String gender ,String phone, String password) {
        FirebaseAuth auth= FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    //Enter user data to firebase
                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(fullName , userName , gender, phone );

                    //Extracting user reference from database
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                //mail de verification
                                firebaseUser.sendEmailVerification();

                                Toast.makeText(SignupActivity.this, "You are Logged in now", Toast.LENGTH_SHORT).show();
                                //ouvrire page profil
                                Intent intent = new Intent(SignupActivity.this , UserProfileActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);//pour ne pas revenir en arriere
                                startActivity(intent);
                                finish();

                            }else {
                                Toast.makeText(SignupActivity.this,"User registererd failled. Please try again ",Toast.LENGTH_LONG).show();

                            }

                        }
                    });


                }else {
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        passwordEditText.setError("Your passeword is too weak. It should at least have 8 caratcers");
                        passwordEditText.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        emailEditText.setError("Your email is invalid or already in use. Kindly Re-enter");
                        emailEditText.requestFocus();
                    }catch (FirebaseAuthUserCollisionException e) {
                        emailEditText.setError("User is already registered with this email. Use another email");
                        emailEditText.requestFocus();
                    }catch (Exception e){
                        Log.e(TAG , e.getMessage());
                        Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

}
