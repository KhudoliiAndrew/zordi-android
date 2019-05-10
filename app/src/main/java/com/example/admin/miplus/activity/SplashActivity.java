package com.example.admin.miplus.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.miplus.R;
import com.example.admin.miplus.activity.activity_in_main.MainActivity;
import com.example.admin.miplus.data_base.DataBaseRepository;
import com.example.admin.miplus.data_base.models.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.IOException;

public class SplashActivity extends AppCompatActivity {


    final DataBaseRepository dataBaseRepository = new DataBaseRepository();
    private Profile profile = new Profile();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        checkUser();

    }

    public static boolean hasConnection() {
        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void checkUser(){
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (hasConnection()) {
            if (currentUser != null) {
                dataBaseRepository.getProfileTask()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.getResult() != null && task.getResult().exists()) {
                                    profile = task.getResult().toObject(Profile.class);
                                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                                    SplashActivity.this.startActivity(mainIntent);
                                    SplashActivity.this.finish();
                                } else {
                                    profile.setDefaultInstance();
                                    dataBaseRepository.setProfile(profile);
                                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                                    SplashActivity.this.startActivity(mainIntent);
                                    SplashActivity.this.finish();
                                }
                            }
                        });


            } else {
                Intent userIntent = new Intent(SplashActivity.this, LoginActivity.class);
                SplashActivity.this.startActivity(userIntent);
                SplashActivity.this.finish();
            }
        } else {
            TextView textView = (TextView) findViewById(R.id.reload_text);
            textView.setText("No internet connection");
            ImageView imageView = (ImageView) findViewById(R.id.reload_splash);
            imageView.setImageResource(R.drawable.ic_replay_white_24dp);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkUser();
                    Toast.makeText(SplashActivity.this, "Checking...", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
