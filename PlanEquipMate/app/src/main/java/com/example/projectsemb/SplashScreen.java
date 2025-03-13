package com.example.projectsemb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {

    public int DELAY_TIME = 2000;
    TextView planEquipMate;
    ImageView iconImage;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        planEquipMate = findViewById(R.id.header);
        iconImage = findViewById(R.id.iconImage);

        //SP to get saved login data from last user
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String savedEmail = sharedPreferences.getString("email", "");
        String savedPassword = sharedPreferences.getString("password", "");

        new Handler().postDelayed(new Runnable() {
            public void run() {
                autoLogin(savedEmail, savedPassword);
            }
        }, DELAY_TIME);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void autoLogin(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Auto-login successful, take the user to the main part of the app
                        Intent intent = new Intent(SplashScreen.this, HomeActivity.class);
                        startActivity(intent);
                        //finish(); // Optional: Close the login activity to prevent coming back to it using the back button
                    } else {
                        Intent i = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(i);
                    }
                });
    }
}