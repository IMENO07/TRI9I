package com.example.tri9itest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPasswordactivity extends AppCompatActivity {
    private EditText editTexteditxtPwdresetEmail;
    private FirebaseAuth authProfile;
    private Button buttonPwdreset;
    private final static String TAG = "ForgotPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_forgot_passwordactivity);
        getSupportActionBar().hide();

        getSupportActionBar().setTitle("Forgot Password");

        editTexteditxtPwdresetEmail = findViewById(R.id.edittxt_pwd_reset_email);
        buttonPwdreset = findViewById(R.id.btn_pwd_reset);
        buttonPwdreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTexteditxtPwdresetEmail.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(ForgotPasswordactivity.this, "Please enter your registered email", Toast.LENGTH_SHORT).show();
                    editTexteditxtPwdresetEmail.setError("EMAIL IS REQUIRED");
                    editTexteditxtPwdresetEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(ForgotPasswordactivity.this, "Please enter valid email", Toast.LENGTH_SHORT).show();
                    editTexteditxtPwdresetEmail.setError("VALID EMAIL IS REQUIRED");
                    editTexteditxtPwdresetEmail.requestFocus();
                } else{
                    resetPassword(email);
                }
            }
        });
    }

    private void resetPassword(String email) {
        authProfile = FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgotPasswordactivity.this, "Please check your inbox for passeword reset link", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ForgotPasswordactivity.this , MainActivity.class);

                    //clear stack to prevent user coming back to forgot pwd page
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }else {
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        editTexteditxtPwdresetEmail.setError("User does not exists or is no longer valid. Please register again");
                    }catch (Exception e){
                        Log.e(TAG , e.getMessage());
                        Toast.makeText(ForgotPasswordactivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    Toast.makeText(ForgotPasswordactivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}