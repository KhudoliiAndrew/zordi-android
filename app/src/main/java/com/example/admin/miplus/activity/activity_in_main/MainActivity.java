package com.example.admin.miplus.activity.activity_in_main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Debug;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.example.admin.miplus.fragment.FirstFragment;
import com.facebook.FacebookAuthorizationException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.firebase_auth.zzcz;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.UserInfo;

import java.util.Arrays;
import java.util.List;

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
    }

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
                    logo = (ImageView) findViewById(R.id.user_logo_google);
                    name = (TextView) findViewById(R.id.user_name_google);
                    email = (TextView) findViewById(R.id.user_email_google);
                    name.setText(mAuth.getCurrentUser().getDisplayName());
                    email.setText(mAuth.getCurrentUser().getEmail());
                   // email.setText(LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email")));
                    RequestOptions cropOptions = new RequestOptions().centerCrop();
                    Glide.with(MainActivity.this).load(mAuth.getCurrentUser().getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(logo);
            }
        };
        Drawer_Layout.addDrawerListener(actionBarDrawerToggle);
    }

    public void signoutOnclick(View view) {
        GoogleSignInClient mGoogleSignInClient ;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(getBaseContext(), gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(MainActivity.this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseAuth.getInstance().signOut();
                        Intent userIntent = new Intent(MainActivity.this, SplashActivity.class);
                        MainActivity.this.startActivity(userIntent);
                        MainActivity.this.finish();
                    }
                });

    }

    public void toStepsSetActivity(View v) {
        Intent stepsIntent = new Intent(MainActivity.this, StepsTargetActivity.class );
        MainActivity.this.startActivity(stepsIntent);
    }

    public void toSleepSetActivity(View v){
        Intent wakeIntent = new Intent(MainActivity.this, WakeActivity.class );
        MainActivity.this.startActivity(wakeIntent);
    }

    public void toStepsInformation(View view){
        Intent infStepsIntent = new Intent(MainActivity.this, InformationStepsActivity.class );
        MainActivity.this.startActivity(infStepsIntent);
    }

    public void toSleepInformation(View view){
        Intent infSleepIntent = new Intent(MainActivity.this, InformationSleepActivity.class );
        MainActivity.this.startActivity(infSleepIntent);
    }

    public void toPulseInformation(View view){
        Intent infPulseIntent = new Intent(MainActivity.this, InformationPulseActivity.class );
        MainActivity.this.startActivity(infPulseIntent);
    }
}
