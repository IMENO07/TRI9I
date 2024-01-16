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
import android.view.Window;
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

public class ChangePasswordActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private EditText editxtPwdCur, editxtPwdNew, editxtPwdConfirm;
    private TextView textViewAthenticated;
    private Button btnChangePwd, btnReautheticate;
    private String userPwdCurr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().hide();

        getSupportActionBar().setTitle("Change Password");

        editxtPwdCur= findViewById(R.id.edittxt_change_pwd_current);
        editxtPwdNew= findViewById(R.id.editxt_change_pwd_new);
        editxtPwdConfirm = findViewById(R.id.editxt_change_pwd_new_confirm);
        textViewAthenticated= findViewById(R.id.txtv_change_pwd_authentificated);
        btnChangePwd= findViewById(R.id.btn_change_pwd_new_confirm) ;
        btnReautheticate= findViewById(R.id.btn_change_pwd_authenticate);

        editxtPwdNew.setEnabled(false);
        editxtPwdConfirm.setEnabled(false);
        btnChangePwd.setEnabled(false);

        authProfile =FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser.equals(null)){
            Toast.makeText(this, "Something went wrong! User's informations are not available", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePasswordActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish();
        }else {
            reAuthenticate(firebaseUser);
        }
    }

    private void reAuthenticate(FirebaseUser firebaseUser) {
        btnReautheticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPwdCurr = editxtPwdCur.getText().toString();
                if (TextUtils.isEmpty(userPwdCurr)){
                    Toast.makeText(ChangePasswordActivity.this, "Password is needed", Toast.LENGTH_SHORT).show();
                    editxtPwdCur.setError("Please enter your current passewor");
                    editxtPwdCur.requestFocus();
                }else {
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPwdCurr);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                textViewAthenticated.setText("Your Password has been verifiedY"+"ou can update your password now");
                                Toast.makeText(ChangePasswordActivity.this, "Password has been verified", Toast.LENGTH_SHORT).show();
                                //disable edit text
                                editxtPwdCur.setEnabled(false);
                                editxtPwdNew.setEnabled(true);
                                editxtPwdConfirm.setEnabled(true);

                                btnChangePwd.setEnabled(true);
                                btnReautheticate.setEnabled(false);

                                //changer a couleur du boutton
                                btnChangePwd.setBackgroundTintList(ContextCompat.getColorStateList(ChangePasswordActivity.this,R.color.teal_200));
                                btnChangePwd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        changePwd(firebaseUser);

                                    }
                                });
                            }else {
                                try {
                                    throw task.getException();
                                }catch (Exception e){
                                    Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    });
                }
            }
        });
    }

    private void changePwd(FirebaseUser firebaseUser) {
        String userPwdNew = editxtPwdNew.getText().toString();
        String userPwdConfirmNew = editxtPwdConfirm.getText().toString();
        if(TextUtils.isEmpty(userPwdNew)){
            Toast.makeText(this, "New password is needed", Toast.LENGTH_SHORT).show();
            editxtPwdNew.setError("Please enter your new password");
            editxtPwdNew.requestFocus();

        } else if (TextUtils.isEmpty(userPwdConfirmNew)) {
            Toast.makeText(this, "Please confirm your new password", Toast.LENGTH_SHORT).show();
            editxtPwdConfirm.setError("Please re-enter your new password");
            editxtPwdConfirm.requestFocus();
        } else if (!userPwdNew.matches(userPwdConfirmNew)) {
            Toast.makeText(this, "Password did not match", Toast.LENGTH_SHORT).show();
            editxtPwdConfirm.setError("Please re-enter same password");
            editxtPwdConfirm.requestFocus();
        } else if (userPwdCurr.matches(userPwdNew)) {
            Toast.makeText(this, "New password can not be the same as the old one", Toast.LENGTH_SHORT).show();
            editxtPwdConfirm.setError("Please enter your new password");
            editxtPwdConfirm.requestFocus();
        }else {firebaseUser.updatePassword(userPwdNew).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ChangePasswordActivity.this, "Password has been changed", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ChangePasswordActivity.this, UserProfileActivity.class);
                    startActivity(intent);
                    finish();

                }else {
                    try {
                        throw task.getException();
                    }catch (Exception e){
                        Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        }
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
            Intent intent = new Intent(ChangePasswordActivity.this, CreateTrip.class);
            startActivity(intent);
        }else if (id == R.id.menu_trip_list) {
            Intent intent = new Intent(ChangePasswordActivity.this, TripList.class);
            startActivity(intent);
        }else if (id == R.id.menu_user_profile) {
            Intent intent = new Intent(ChangePasswordActivity.this, UserProfileActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_settings) {
            Intent intent = new Intent(ChangePasswordActivity.this, SettingsActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePasswordActivity.this , MainActivity.class);

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