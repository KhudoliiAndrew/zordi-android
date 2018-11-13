package com.example.admin.miplus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DISPLEY_LENGHT = 5000;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        setContentView(R.layout.login_activity);

        mAuth = FirebaseAuth.getInstance();

        final FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentUser == null){
                    Intent userIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    SplashActivity.this.startActivity(userIntent);
                    SplashActivity.this.finish();
                } else {
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                }
            }
        }, SPLASH_DISPLEY_LENGHT);
    }

    private void updateUI(FirebaseUser currentUser) {
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
