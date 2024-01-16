package com.example.tri9itest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private Button login ;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();


        ImageView image = findViewById(R.id.imageMainNouveauprojet);
        image.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nouveauprojet));

        mAuth = FirebaseAuth.getInstance();
        Button login = findViewById(R.id.Mainlogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        TextView signup = findViewById(R.id.Mainsignup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null){
            Toast.makeText(MainActivity.this,"You are Already Logged In",Toast.LENGTH_SHORT).show();

            //start user profile activity
            startActivity(new Intent(MainActivity.this, ShowTime.class));
            finish();
        }
        else {
            Toast.makeText(this, "Login or Signup", Toast.LENGTH_SHORT).show();
        }
    }
}
