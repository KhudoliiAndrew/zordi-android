package com.example.admin.miplus.activity.activity_in_main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.admin.miplus.R;

public class ThirdActivity extends AppCompatActivity {
    private static final int LAYOUT = R.layout.third_activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(">>>>>>", "StepCreate");
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
    }

}
