package com.example.admin.miplus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.example.admin.miplus.R;
import com.example.admin.miplus.activity.activity_in_main.MainActivity;
import com.example.admin.miplus.data_base.DataBaseRepository;
import com.example.admin.miplus.data_base.models.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class SplashActivity extends AppCompatActivity {


    final DataBaseRepository dataBaseRepository = new DataBaseRepository();
    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentUser != null) {
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                    dataBaseRepository.getProfile()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.getResult() != null && task.getResult().exists()) {
                                        profile = task.getResult().toObject(Profile.class);
                                    } else {
                                        profile = new Profile();
                                        profile.setDefaultInstance();
                                        dataBaseRepository.setProfile(profile);
                                    }
                                }
                            });

                } else {
                    Intent userIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    SplashActivity.this.startActivity(userIntent);
                    SplashActivity.this.finish();
                }

            }
        }, 0);
    }
}
