package com.example.admin.miplus.activity.activity_in_main;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.admin.miplus.R;
import com.example.admin.miplus.activity.SplashActivity;
import com.example.admin.miplus.activity.activivity_from_main.InformationPulseActivity;
import com.example.admin.miplus.activity.activivity_from_main.InformationSleepActivity;
import com.example.admin.miplus.activity.activivity_from_main.InformationStepsActivity;
import com.example.admin.miplus.activity.activivity_from_main.StepsTargetActivity;
import com.example.admin.miplus.activity.activivity_from_main.WakeActivity;
import com.example.admin.miplus.adapter.TabsPagerFragmentAdapter;
import com.google.firebase.auth.FirebaseAuth;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class MainActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.main_activity;
    private static final String CHANNEL_ID = "exampleServiceChannel";
    private DrawerLayout Drawer_Layout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private Button logout;
    private FirebaseAuth mAuth;
    private TextView name;
    private TextView email;
    private ImageView logo;
    private Button secondActivity_steps_button;
    private TextView quantitySteps;
    private Switch lightSwitch;
    private Switch darkSwitch;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private int steps = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setTheme(R.style.AppTheme);
        setContentView(LAYOUT);
        initBottomNavigationView();
        initToolbar();
        setView();
        setContentNavigationView();
        pedometr();
        //createNotificationChannel();
    }

    /*ToDo private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }*/

    private void  setView(){
        Drawer_Layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        logout = (Button) findViewById(R.id.log_out);
        secondActivity_steps_button = (Button) findViewById(R.id.steps_watch_button);

    }

    private void initBottomNavigationView(){
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabsPagerFragmentAdapter adapter = new TabsPagerFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_main_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item_main:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.item_map:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.item_settings:
                        viewPager.setCurrentItem(2);
                        break;
                }
                return false;
            }
        });
    }

    private void initToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(Color.WHITE);
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
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
    }

    private void setContentNavigationView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, Drawer_Layout, toolbar, GravityCompat.START, GravityCompat.END) {

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                if(newState == DrawerLayout.STATE_DRAGGING) {
                    logo = (ImageView) findViewById(R.id.user_logo_google);
                    name = (TextView) findViewById(R.id.user_name_google);
                    email = (TextView) findViewById(R.id.user_email_google);
                    name.setText(mAuth.getCurrentUser().getDisplayName());
                    email.setText(mAuth.getCurrentUser().getEmail());
                    RequestOptions cropOptions = new RequestOptions().centerCrop();
                    Glide.with(MainActivity.this).load(mAuth.getCurrentUser().getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(logo);
                }
            }
        };
        Drawer_Layout.addDrawerListener(actionBarDrawerToggle);
    }

    public void signoutOnclick(View view) {
        mAuth.signOut();
        Intent userIntent = new Intent(MainActivity.this, SplashActivity.class);
        MainActivity.this.startActivity(userIntent);
        MainActivity.this.finish();
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

    public void onClickSecondActivityStepsButton(View v) {
        Intent stepsIntent = new Intent(MainActivity.this, StepsTargetActivity.class );
        MainActivity.this.startActivity(stepsIntent);
        MainActivity.this.finish();
    }

    public void onClickToWakeActivity(View v){
        Intent wakeIntent = new Intent(MainActivity.this, WakeActivity.class );
        MainActivity.this.startActivity(wakeIntent);
        MainActivity.this.finish();
    }

    public void textInstaller(){
        viewPager.setCurrentItem(2);
        quantitySteps = (TextView) findViewById(R.id.quantity_of_steps_text);
        Intent CheckFromTargetActivity = getIntent();
        String StepsQuantity = String.valueOf(CheckFromTargetActivity.getIntExtra("StepsQuantity", 1000));
    }

    public void toStepsInformation(View view){
        Intent infStepsIntent = new Intent(MainActivity.this, InformationStepsActivity.class );
        MainActivity.this.startActivity(infStepsIntent);
        MainActivity.this.finish();
    }

    public void toSleepInformation(View view){
        Intent infSleepIntent = new Intent(MainActivity.this, InformationSleepActivity.class );
        MainActivity.this.startActivity(infSleepIntent);
        MainActivity.this.finish();
    }

    public void toPulseInformation(View view){
        Intent infPulseIntent = new Intent(MainActivity.this, InformationPulseActivity.class );
        MainActivity.this.startActivity(infPulseIntent);
        MainActivity.this.finish();
    }

    private void pedometr(){
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sSensor= sensorManager .getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
       // TextView how_many_steps = (TextView) findViewById(R.id.how_many_steps_text);
        //how_many_steps.setText(steps);
    }

    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        float[] values = event.values;
        int value = -1;

        if (values.length > 0) {
            value = (int) values[0];
        }
        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            steps++;
            pedometr();
        }
    }

    public void startService(View view){
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
    }

    public void stopService (View view){
        Intent intent = new Intent(this, MyService.class);
        stopService(intent);
    }
}
