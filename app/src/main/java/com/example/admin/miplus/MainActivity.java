package com.example.admin.miplus;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.miplus.adapter.TabsPagerFragmentAdapter;
import com.facebook.CallbackManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    private static final int LAYOUT = R.layout.activity_main;
    private DrawerLayout Drawer_Layout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private Button logout;
    private FirebaseAuth mAuth;
    private TextView name;
    private TextView email;
    private ImageView logo;
    private ConstraintLayout header;

    private Button Steps_btn;
    private Button Sleeping_monitor_btn;
    private Button Heart_rate_btn;
    private Button Family_access_btn;

    private SensorManager sensorManager;
    private TextView steps_info;
    boolean activityRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setTheme(R.style.AppTheme);
        setContentView(LAYOUT);
        initView();
        initTabs();
        initToolbar();
        initNavigationView();
        initHeader();

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Steps_btn = (Button) findViewById(R.id.steps_btn);
        Steps_btn.setOnClickListener(this);

        Sleeping_monitor_btn = (Button) findViewById(R.id.sleeping_monitor_btn);
        Sleeping_monitor_btn.setOnClickListener(this);

        Heart_rate_btn = (Button) findViewById(R.id.heart_rate_btn);
        Heart_rate_btn.setOnClickListener(this);

        Family_access_btn = (Button) findViewById(R.id.family_access_btn);
        Family_access_btn.setOnClickListener(this);

        steps_info = (TextView) findViewById(R.id.steps_info);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.steps_btn:
                Intent intent = new Intent(this, StepsActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        switch (view.getId()) {
            case R.id.sleeping_monitor_btn:
                Intent intent = new Intent(this, SleepingMonitorActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        switch (view.getId()) {
            case R.id.heart_rate_btn:
                Intent intent = new Intent(this, HeartRateActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        switch (view.getId()) {
            case R.id.family_access_btn:
                Intent intent = new Intent(this, FamilyAccessActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void initTabs() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabsPagerFragmentAdapter adapter = new TabsPagerFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
        toolbar.inflateMenu(R.menu.menu);

        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
    }

    private void initNavigationView() {
        Drawer_Layout = (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    private void initHeader() {
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        //String nametext = currentUser.getDisplayName();
        //String emailtext = currentUser.getEmail();
        // name.setText(currentUser.getDisplayName());
        //email.setText(emailtext, TextView.BufferType.EDITABLE);
        //logo.setImageMatrix(currentUser.getPhotoUrl());
    }

    private void initView() {
        logout = (Button) findViewById(R.id.log_out);
        name = (TextView) findViewById(R.id.user_name_google);
        email = (TextView) findViewById(R.id.user_email_google);
        logo = (ImageView) findViewById(R.id.user_logo_google);
        header = (ConstraintLayout) findViewById(R.id.header);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Drawer_Layout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CallbackManager callbackManager = CallbackManager.Factory.create();
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityRunning = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {Toast.makeText(this, "Count sensor is not available!", Toast.LENGTH_LONG).show();
            }
        }

    @Override
    protected void onPause() {
        super.onPause();
        activityRunning = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(activityRunning){
            steps_info.setText(String.valueOf(event.values[0]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}