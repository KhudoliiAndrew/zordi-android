package com.example.admin.miplus;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.constraint.ConstraintLayout;
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

import com.example.admin.miplus.adapter.TabsPagerFragmentAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirstActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.first_activity;
    private DrawerLayout Drawer_Layout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private Button logout;
    private FirebaseAuth mAuth;
    private TextView name;
    private TextView email;
    private ImageView logo;
    private ConstraintLayout header;

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
    private void initNavigationView(){
        Drawer_Layout = (DrawerLayout) findViewById(R.id.drawer_layout);
    }
    private void initHeader(){
        final FirebaseUser currentUser = mAuth.getCurrentUser();
      // String nametext = currentUser.getDisplayName();
      //  String emailtext = currentUser.getEmail();
       // name.setText(currentUser.getDisplayName());
        //email.setText(emailtext, TextView.BufferType.EDITABLE);
        //logo.setImageMatrix(currentUser.getPhotoUrl());
    }
    private void  initView(){
        logout = (Button) findViewById(R.id.log_out);
        name = (TextView) findViewById(R.id.user_name_google);
        email = (TextView) findViewById(R.id.user_email_google);
        logo = (ImageView) findViewById(R.id.user_logo_google);
        header = (ConstraintLayout) findViewById(R.id.header);
    }


    public void onClick(View view) {
            final FirebaseUser currentUser = mAuth.getCurrentUser();
            currentUser.delete();
                Intent userIntent = new Intent(FirstActivity.this, SplashActivity.class);
                FirstActivity.this.startActivity(userIntent);
                FirebaseAuth.getInstance().signOut();
                finish();
                FirstActivity.this.finish();
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
}
