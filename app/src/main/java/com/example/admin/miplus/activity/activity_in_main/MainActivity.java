package com.example.admin.miplus.activity.activity_in_main;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
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

import com.example.admin.miplus.R;
import com.example.admin.miplus.activity.SplashActivity;
import com.example.admin.miplus.activity.TargetActivity;
import com.example.admin.miplus.adapter.TabsPagerFragmentAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
        logo = (ImageView) findViewById(R.id.user_logo_google);
        name = (TextView) findViewById(R.id.user_name_google);
        email = (TextView) findViewById(R.id.user_email_google);
        name.setText(mAuth.getCurrentUser().getDisplayName());
        email.setText(mAuth.getCurrentUser().getEmail());
        logo.setImageURI(mAuth.getCurrentUser().getPhotoUrl());
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
        Intent stepsIntent = new Intent(MainActivity.this, TargetActivity.class );
        MainActivity.this.startActivity(stepsIntent);
        MainActivity.this.finish();
    }

    public void textInstaller(){
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        TabLayout.Tab tab = tabLayout.getTabAt(3);
        tab.select();
        quantitySteps = (TextView) findViewById(R.id.quantity_of_steps_text);
        Intent CheckFromTargetActivity = getIntent();
        String StepsQuantity = String.valueOf(CheckFromTargetActivity.getIntExtra("StepsQuantity", 1000));
    }
}
