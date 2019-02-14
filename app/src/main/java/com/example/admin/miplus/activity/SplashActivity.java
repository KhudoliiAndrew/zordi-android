package com.example.admin.miplus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.admin.miplus.R;
import com.example.admin.miplus.activity.activity_in_main.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentUser == null) {
                    Intent userIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    SplashActivity.this.startActivity(userIntent);
                    SplashActivity.this.finish();
                } else {
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                }
            }
        }, 1000);
    }
}
