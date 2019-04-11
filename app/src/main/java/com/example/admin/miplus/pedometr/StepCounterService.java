package com.example.admin.miplus.pedometr;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.example.admin.miplus.CustomXML.CircleProgressBar;
import com.example.admin.miplus.R;
import com.example.admin.miplus.data_base.DataBaseRepository;
import com.example.admin.miplus.data_base.models.Profile;
import com.example.admin.miplus.fragment.FirstFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class StepCounterService extends Service implements SensorEventListener {

    final DataBaseRepository dataBaseRepository = new DataBaseRepository();
    private Profile profile = new Profile();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private int steps = 0;
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

    private MyBinder mLocalbinder = new MyBinder();
    private CallBack mCallBack;

    private Date currentTime;

    public StepCounterService() {
        int h = 480;
        mYOffset = h * 0.5f;
        mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (dataBaseRepository.getProfile() != null) {
            profile = dataBaseRepository.getProfile();
            steps = profile.getSteps();
            if(mCallBack != null) mCallBack.setSteps(steps);
        } else {
            dataBaseRepository.getProfileTask()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            profile = task.getResult().toObject(Profile.class);
                            steps = profile.getSteps();
                            if(mCallBack != null) mCallBack.setSteps(steps);
                        }
                    });
        }



        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        if (sSensor != null) {
            sensorManager.registerListener(this, sSensor, SensorManager.SENSOR_DELAY_UI);

        } else {
            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        }



        return Service.START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
            Sensor sensor = event.sensor;
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                synchronized (this) {
                    float vSum = 0;
                    for (int i = 0; i < 3; i++) {
                        final float v = mYOffset + event.values[i] * mScale[1];
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
                                if(mCallBack != null) mCallBack.setSteps(steps);
                                currentTime = Calendar.getInstance().getTime();
                                if (profile != null && steps % 30 == 0) {
                                    profile.setSteps(steps);
                                    dataBaseRepository.setProfile(profile);
                                }

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
            } else {}
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onDestroy() {
        if (profile != null) {
            profile.setSteps(steps);
            dataBaseRepository.setProfile(profile);
        }
        super.onDestroy();
    }

    @Nullable
    public IBinder onBind(Intent intent) {
        return mLocalbinder;
    }

    public class MyBinder extends Binder {
        public StepCounterService getService() {
            return StepCounterService.this;
        }
    }

    public void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }

    public interface CallBack {
        void setSteps(int steps);
    }
}


