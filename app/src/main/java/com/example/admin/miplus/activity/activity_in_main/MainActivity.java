package com.example.admin.miplus.activity.activity_in_main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.example.admin.miplus.R;
import com.example.admin.miplus.activity.SplashActivity;
import com.example.admin.miplus.activity.activivity_from_main.InformationPulseActivity;
import com.example.admin.miplus.activity.activivity_from_main.InformationSleepActivity;
import com.example.admin.miplus.activity.activivity_from_main.InformationStepsActivity;
import com.example.admin.miplus.activity.activivity_from_main.StepsTargetActivity;
import com.example.admin.miplus.activity.activivity_from_main.WakeActivity;
import com.example.admin.miplus.adapter.TabsPagerFragmentAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class MainActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.main_activity;
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
    private Switch light_switch;
    private Switch dark_switch;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private int steps = 0;
    private TextView how_many_steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setTheme(R.style.AppTheme);
        setContentView(LAYOUT);
        initTabs();
        initToolbar();
        setView();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        viewPager.setCurrentItem(2);

    }

    private void  setView(){
        Drawer_Layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        logout = (Button) findViewById(R.id.log_out);
        secondActivity_steps_button = (Button) findViewById(R.id.steps_watch_button);
    }
    private void initTabs(){
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabsPagerFragmentAdapter adapter = new TabsPagerFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
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

    private void setContentView(){
        /*toolbar = (Toolbar) findViewById(R.id.toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, Drawer_Layout, toolbar, GravityCompat.START, GravityCompat.END) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
                // getSupportActionBar().setTitle(mTitle);
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
                //getSupportActionBar().setTitle(mTitle);
                // invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()

            }
        };
        Drawer_Layout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();*/

            logo = (ImageView) findViewById(R.id.user_logo_google);
            name = (TextView) findViewById(R.id.user_name_google);
            email = (TextView) findViewById(R.id.user_email_google);
            name.setText(mAuth.getCurrentUser().getDisplayName());
            email.setText(mAuth.getCurrentUser().getEmail());
            RequestOptions cropOptions = new RequestOptions().centerCrop();
            Glide.with(this).load(mAuth.getCurrentUser().getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(logo);
    }
    public void signOut_onClick(View view) {
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
                setContentView();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick_secondActivity_steps_button(View v) {
        Intent stepsIntent = new Intent(MainActivity.this, StepsTargetActivity.class );
        MainActivity.this.startActivity(stepsIntent);
        MainActivity.this.finish();
     //   Drawer_Layout.addDrawerListener();   лушатель открыт дровбл или нет

    }
    public void onClick_to_Wake_activity(View v){
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
    public void light_or_dark_setter(View v){
         light_switch = (Switch) findViewById(R.id.light_theme_switch);
         dark_switch = (Switch) findViewById(R.id.dark_theme_switch);
         if(light_switch!= null){
            // dark_switch = null;
         }
         if(dark_switch!= null){
             //light_switch = null;
         }
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
        how_many_steps = (TextView) findViewById(R.id.how_many_steps_text);
        how_many_steps.setText(steps);
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
}
