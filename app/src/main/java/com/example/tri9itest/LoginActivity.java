package com.example.tri9itest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private static final String TAG ="LoginActivity";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        getSupportActionBar().setTitle("Login");

        ImageView image = findViewById(R.id.imageNouveauprojet);
        image.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nouveauprojet));

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();

        TextView ForgotPassword = findViewById(R.id.txtForgetpassword);
        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "You can reset your password", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this,ForgotPasswordactivity.class));
            }
        });


        loginButton = findViewById(R.id.btnLogin);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    emailEditText.setError("Email is required");
                    emailEditText.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(LoginActivity.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                    emailEditText.setError("valid Email is required");
                    emailEditText.requestFocus();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Please enter your passeword", Toast.LENGTH_SHORT).show();
                    passwordEditText.setError("passeword is required");
                    passwordEditText.requestFocus();
                } else {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        //get current user
                                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                        // Login success, start userprofil activity
                                            String userId = firebaseUser.getUid();
                                            //Extracting user reference from db for "registered users
                                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
                                            referenceProfile.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);

                                                        String fullname = readUserDetails.fullName;
                                                        String userName = readUserDetails.userName;
                                                        String phone = readUserDetails.phone;

                                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putString("UserId",firebaseUser.getUid());
                                                    editor.commit();

                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                        Log.d("user info", task.getResult().getUser().getIdToken(true).toString());
                                        task.getResult().getUser();
                                        startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
                                        finish();

                                    }
                                    else {
                                        try {
                                            throw task.getException();
                                        }catch (FirebaseAuthInvalidUserException e){
                                            emailEditText.setError("User does not exists or is no longer valid . Please register again");
                                            emailEditText.requestFocus();
                                        }catch (FirebaseAuthInvalidCredentialsException e){
                                            emailEditText.setError("Invalid credentials .Kindly check and re-enter");
                                            emailEditText.requestFocus();
                                        }catch (Exception e){
                                            Log.e(TAG, e.getMessage());
                                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                }
            }
        });
    }
}