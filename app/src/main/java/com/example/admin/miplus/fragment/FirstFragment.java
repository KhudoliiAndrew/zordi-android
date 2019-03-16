package com.example.admin.miplus.fragment;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.util.Util;
import com.example.admin.miplus.BuildConfig;
import com.example.admin.miplus.CustomXML.CircleProgressBar;
import com.example.admin.miplus.Pedometr.StepListener;
import com.example.admin.miplus.R;
import com.example.admin.miplus.data_base.DataBaseRepository;
import com.example.admin.miplus.data_base.models.Profile;
import com.example.admin.miplus.fragment.FirstWindow.StepsInformationFragment;
import com.facebook.internal.Logger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class FirstFragment extends Fragment implements SensorEventListener{

    final DataBaseRepository dataBaseRepository = new DataBaseRepository();
    private Profile profile = new Profile();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private int steps;

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

    public FirstFragment() {
        int h = 480;
        mYOffset = h * 0.5f;
        mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
    }

    public static FirstFragment getInstance() {
        Bundle args = new Bundle();
        FirstFragment fragment = new FirstFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.first_activity, container, false);

        if (dataBaseRepository.getProfile() != null) {
            profile = dataBaseRepository.getProfile();
            viewSetter(view);
        } else {
            dataBaseRepository.getProfileTask()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            profile = task.getResult().toObject(Profile.class);
                            viewSetter(view);
                        }
                    });

        }

        RelativeLayout stepsRelativeLayout = (RelativeLayout) view.findViewById(R.id.toStepsInformationCard);
        stepsRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toStepsInformation();
            }
        });

        RelativeLayout sleepRelativeLayout = (RelativeLayout) view.findViewById(R.id.toSleepInformationCard);
        sleepRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        RelativeLayout pulseRelativeLayout = (RelativeLayout) view.findViewById(R.id.toPulseInformationCard);
        pulseRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    private void toStepsInformation() {
        StepsInformationFragment stepsInformationFragment = new StepsInformationFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(android.R.id.content, stepsInformationFragment);
        fragmentTransaction.commit();
    }

    private void viewSetter(View view){
        steps = profile.getSteps();
        TextView stepsText = (TextView) view.findViewById(R.id.steps_cuantity_text);
        if(stepsText != null) stepsText.setText(String.valueOf(profile.getSteps()));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(getActivity() != null) {
            SensorManager sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
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
                                        profile.setSteps(steps);
                                        dataBaseRepository.setProfile(profile);
                                        TextView stepsText = (TextView) getActivity().findViewById(R.id.steps_cuantity_text);
                                        if (stepsText != null)
                                            stepsText.setText(String.valueOf(profile.getSteps()));
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
                profile.setSteps(steps);
                dataBaseRepository.setProfile(profile);
                TextView stepsText = (TextView) getActivity().findViewById(R.id.steps_cuantity_text);
                if (stepsText != null) stepsText.setText(String.valueOf(profile.getSteps()));
            }
        } else {
            return;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();
        SensorManager sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        Sensor sSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (sSensor != null) {
            sensorManager.registerListener(this, sSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        }
    }
}
