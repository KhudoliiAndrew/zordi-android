package com.example.admin.miplus.activity.activity_in_main;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.PermissionChecker;
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
import com.example.admin.miplus.BuildConfig;
import com.example.admin.miplus.Pedometr.SensorListener;
import com.example.admin.miplus.Pedometr.StepListener;
import com.example.admin.miplus.R;
import com.example.admin.miplus.activity.SplashActivity;
import com.example.admin.miplus.adapter.TabsPagerFragmentAdapter;
import com.example.admin.miplus.fragment.Dialogs.DonateDialogFragment;
import com.example.admin.miplus.fragment.Dialogs.FeedbackDialogFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private long steps = 0;

    private final static String TAG = "StepDetector";
    private float mLimit = 10;
    private float mLastValues[] = new float[3 * 2];
    private float mScale[] = new float[2];
    private float mYOffset;

    private float mLastDirections[] = new float[3 * 2];
    private float mLastExtremes[][] = {new float[3 * 2], new float[3 * 2]};
    private float mLastDiff[] = new float[3 * 2];
    private int mLastMatch = -1;

    private ArrayList<StepListener> mStepListeners = new ArrayList<StepListener>();

    public MainActivity() {
        int h = 480;
        mYOffset = h * 0.5f;
        mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNotification();

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.commit();
        }

        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= 23 && PermissionChecker
                .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PermissionChecker.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        setTheme(R.style.AppTheme);
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
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void setNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Title")
                        .setContentText("Notification text");

        Notification notification = builder.build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        if (sSensor == null) {
            Sensor sensor = event.sensor;
            synchronized (this) {
                if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
                } else {
                    int j = (sensor.getType() == Sensor.TYPE_ACCELEROMETER) ? 1 : 0;
                    if (j == 1) {
                        float vSum = 0;
                        for (int i = 0; i < 3; i++) {
                            final float v = mYOffset + event.values[i] * mScale[j];
                            vSum += v;
                        }
                        int k = 0;
                        float v = vSum / 3;

                        float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
                        if (direction == -mLastDirections[k]) {
                            // Direction changed
                            int extType = (direction > 0 ? 0 : 1); // minumum or maximum?
                            mLastExtremes[extType][k] = mLastValues[k];
                            float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);

                            if (diff > mLimit) {

                                boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k] * 2 / 3);
                                boolean isPreviousLargeEnough = mLastDiff[k] > (diff / 3);
                                boolean isNotContra = (mLastMatch != 1 - extType);

                                if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                                    steps++;
                                    Toast.makeText(getBaseContext(), "Steps: " + steps, Toast.LENGTH_LONG).show();
                                    for (StepListener stepListener : mStepListeners) {
                                        stepListener.onStep();
                                    }
                                    mLastMatch = extType;
                                } else {
                                    mLastMatch = -1;
                                }
                            }
                            mLastDiff[k] = diff;
                        }
                        mLastDirections[k] = direction;
                        mLastValues[k] = v;
                    }
                }
            }
        } else {
            Sensor sensor = event.sensor;
            float[] values = event.values;
            int value = -1;

            if (values.length > 0) {
                value = (int) values[0];
            }
            steps++;
            Toast.makeText(getBaseContext(), "Steps: " + steps, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (sSensor != null) {
            sensorManager.registerListener(this, sSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        }

    }
}
