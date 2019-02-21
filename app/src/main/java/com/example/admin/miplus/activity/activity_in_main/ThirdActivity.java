package com.example.admin.miplus.activity.activity_in_main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.admin.miplus.R;

public class ThirdActivity extends AppCompatActivity {
    private static final int LAYOUT = R.layout.third_activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
    }

}
