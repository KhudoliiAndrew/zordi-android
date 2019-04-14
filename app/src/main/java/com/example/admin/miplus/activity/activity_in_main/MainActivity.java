package com.example.admin.miplus.activity.activity_in_main;

import android.Manifest;
import android.app.AlarmManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.PermissionChecker;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.admin.miplus.BuildConfig;
import com.example.admin.miplus.R;
import com.example.admin.miplus.Services.NotificationReceiver;
import com.example.admin.miplus.activity.SplashActivity;
import com.example.admin.miplus.adapter.TabsPagerFragmentAdapter;
import com.example.admin.miplus.data_base.DataBaseRepository;
import com.example.admin.miplus.data_base.models.Profile;
import com.example.admin.miplus.fragment.Dialogs.DonateDialogFragment;
import com.example.admin.miplus.fragment.Dialogs.FeedbackDialogFragment;
import com.example.admin.miplus.pedometr.StepCounterService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

public class MainActivity extends AppCompatActivity {

    public static final int NOTIFICATION_REMINDER_NIGHT = 2;
    private Profile profile = new Profile();
    final DataBaseRepository dataBaseRepository = new DataBaseRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (dataBaseRepository.getProfile() != null) {
            profile = dataBaseRepository.getProfile();
        } else {
            dataBaseRepository.getProfileTask()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            profile = task.getResult().toObject(Profile.class);
                        }
                    });

        }

        setNotification();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, StepCounterService.class));
        } else {
            startService(new Intent(this, StepCounterService.class));
            Log.d(">>>>>>", "sdadfasdsadsa");
        }

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.commit();
        }

        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= 23 && PermissionChecker
                .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PermissionChecker.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        setTheme(R.style.LightAppThemeWithTransparentStatusBar);
        setContentView(R.layout.main_activity);
        initBottomNavigationView();
        initToolbar();
        setContentNavigationView();

        Button signoutButton = (Button) findViewById(R.id.log_out);
        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signoutOnclick();
            }
        });
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

        newUserChecker();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(Color.WHITE);

        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (item.getItemId() == android.R.id.home && getSupportFragmentManager().getBackStackEntryCount() == 0) {
            drawerLayout.openDrawer(GravityCompat.START);
        } else {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setContentNavigationView() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, GravityCompat.START, GravityCompat.END) {

            @Override
            public void onDrawerOpened(View drawerView) {
                setHeaderContent();
                setSwitchPositions();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                setHeaderContent();
                setSwitchPositions();
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
    }

    private void setSwitchPositions(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        final SwitchCompat sleepSwitch = (SwitchCompat) findViewById(R.id.sleep_switch);
        final SwitchCompat stepsSwitch = (SwitchCompat) findViewById(R.id.steps_switch);
        final SwitchCompat notificationSwitch = (SwitchCompat) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.notification_item)).findViewById(R.id.drawer_switch);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);

        if (profile != null) notificationSwitch.setChecked(profile.getNotifications());

        notificationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem() == 2){
                    if(!notificationSwitch.isChecked() ){
                        profile.setNotifications(notificationSwitch.isChecked());
                        stepsSwitch.setChecked(false);
                        sleepSwitch.setChecked(false);
                        dataBaseRepository.setProfile(profile);
                    } else {
                        profile.setNotifications(notificationSwitch.isChecked());
                        stepsSwitch.setChecked(true);
                        sleepSwitch.setChecked(true);
                        dataBaseRepository.setProfile(profile);
                    }
                }
            }
        });
    }

    private void setHeaderContent() {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final TextView name = (TextView) findViewById(R.id.user_name_google);
        final TextView email = (TextView) findViewById(R.id.user_email_google);
        final ImageView logo = (ImageView) findViewById(R.id.user_logo_google);
        name.setText(mAuth.getCurrentUser().getDisplayName());
        email.setText(mAuth.getCurrentUser().getEmail());
        Glide.with(MainActivity.this).load(mAuth.getCurrentUser().getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(logo);
    }

    public void signoutOnclick() {
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
        DialogFragment dlgf2 = new DonateDialogFragment();
        dlgf2.show(getSupportFragmentManager(), "dlgf2");
    }

    public void feedbackOnClick(MenuItem item) {
        DialogFragment dlgf1 = new FeedbackDialogFragment();
        dlgf1.show(getSupportFragmentManager(), "dlgf1");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_main_navigation_view);

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                if (viewPager.getCurrentItem() != 0) {
                    viewPager.setCurrentItem(0);
                    bottomNavigationView.getMenu().findItem(R.id.item_main).setChecked(true);
                }
            }

        }
    }

    private void setNotification() {


        Intent notifyIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (this, NOTIFICATION_REMINDER_NIGHT, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(this.ALARM_SERVICE);
    }

    private void newUserChecker() {
        Intent intent = getIntent();
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_main_navigation_view);

        if (intent.getBooleanExtra("fromLogin", false) && viewPager != null) {
            viewPager.setCurrentItem(2);
            bottomNavigationView.getMenu().findItem(R.id.item_settings).setChecked(true);
        }
    }
}
