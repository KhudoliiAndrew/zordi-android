package com.example.admin.miplus.activity.activity_in_main;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.admin.miplus.R;
import com.example.admin.miplus.activity.SplashActivity;
import com.example.admin.miplus.adapter.TabsPagerFragmentAdapter;
import com.example.admin.miplus.fragment.Dialogs.FeedbackDialogFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.AppTheme);
        setContentView(R.layout.main_activity);
        initBottomNavigationView();
        initToolbar();
        setContentNavigationView();
    }

    private void initBottomNavigationView() {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabsPagerFragmentAdapter adapter = new TabsPagerFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_main_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
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
                return true;
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.inflateMenu(R.menu.menu);

        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
    }

    private void setContentNavigationView() {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, GravityCompat.START, GravityCompat.END) {

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                final TextView name = (TextView) findViewById(R.id.user_name_google);
                final TextView email = (TextView) findViewById(R.id.user_email_google);
                final ImageView logo = (ImageView) findViewById(R.id.user_logo_google);
                name.setText(mAuth.getCurrentUser().getDisplayName());
                email.setText(mAuth.getCurrentUser().getEmail());
                Glide.with(MainActivity.this).load(mAuth.getCurrentUser().getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(logo);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
    }

    public void signoutOnclick(View view) {
        GoogleSignInClient mGoogleSignInClient;
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

    public void developerHelpOnClick(MenuItem item) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.donate_dialog);
        Button coppyButton = (Button) dialog.findViewById(R.id.coppy_button_donate_dialog);
        dialog.show();
        coppyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", "5168 7559 0373 9171");
                clipboard.setPrimaryClip(clip);
                Toast toast = Toast.makeText(getApplicationContext(), "Copy", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public void feedbackOnClick(MenuItem item) {
        DialogFragment dlgf1;
        dlgf1 = new FeedbackDialogFragment();
        dlgf1.show(getSupportFragmentManager(), "dlgf1");
    }


}
