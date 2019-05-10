package com.example.admin.miplus.pedometr;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;

import com.example.admin.miplus.CustomXML.CircleProgressBar;
import com.example.admin.miplus.R;
import com.example.admin.miplus.activity.SplashActivity;
import com.example.admin.miplus.data_base.DataBaseRepository;
import com.example.admin.miplus.data_base.models.Profile;
import com.example.admin.miplus.data_base.models.StepsData;
import com.example.admin.miplus.fragment.FirstFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class StepCounterService extends Service implements SensorEventListener {

    final DataBaseRepository dataBaseRepository = new DataBaseRepository();
    private Profile profile = new Profile();

    private int steps = 0;
    private float mLimit = 10;
    private float mLastValues[] = new float[3 * 2];
    private float mScale[] = new float[2];
    private float mYOffset;
    private float mLastDirections[] = new float[3 * 2];
    private float mLastExtremes[][] = {new float[3 * 2], new float[3 * 2]};
    private float mLastDiff[] = new float[3 * 2];
    private int mLastMatch = -1;

    private ArrayList<StepListener> mStepListeners = new ArrayList<StepListener>();
    private List<StepsData> stepsDataList = new ArrayList<StepsData>();
    private StepsData stepsData = new StepsData();

    private MyBinder mLocalbinder = new MyBinder();
    private CallBack mCallBack;

    public StepCounterService() {
        int h = 480;
        mYOffset = h * 0.5f;
        mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        dataBaseRepository.getStepsDataListOrderedDate()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            final Date date = new Date();
                            stepsDataList = task.getResult().toObjects(StepsData.class);
                            stepsData = stepsDataList.get(stepsDataList.size() - 1);
                            if (stepsData.getDate().getDay() == date.getDay()) {
                                steps = stepsData.getSteps();
                            }
                        } else {
                            stepsData.setDefaultInstance();
                            dataBaseRepository.setStepsData(stepsData);
                        }
                    }
                });

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Sensor sensor = null;
        if (sensorManager != null) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        }


        /*if (sSensor != null) {
            sensorManager.registerListener(this, sSensor, SensorManager.SENSOR_DELAY_UI);

        } else {

        }*/
        return Service.START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (stepsData.getSteps() != 0) {
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
                                if (mCallBack != null) mCallBack.setSteps(steps);
                                setStepsToBd(steps);
                                goalNotification();

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
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onDestroy() {
        if (stepsData != null) {
            stepsData.setSteps(steps);
            stepsData.setDate(new Date());
            dataBaseRepository.setStepsData(stepsData);
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {

        super.onLowMemory();
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

    private void goalNotification() {
        if (profile != null && steps == profile.getStepsTarget() && profile.getNotifications() && profile.getStepsNotification()) {
            Intent resultIntent = new Intent(this, SplashActivity.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Congratulations!")
                            .setContentText("You have passed the necessary number of steps")
                            .setContentIntent(resultPendingIntent)
                            .setAutoCancel(true)
                            .setVibrate(new long[]{10, 60});

            Notification notification = builder.build();

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.notify(1, notification);
            }
        }
    }

    private void setStepsToBd(int steps) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (stepsData != null && stepsData.getDate() != null && System.currentTimeMillis() != 0 && mAuth.getUid() != null) {
            if (System.currentTimeMillis() > stepsData.getDate().getTime() + 60000) {
                stepsData.setSteps(steps);
                stepsData.setDate(new Date());
                dataBaseRepository.setStepsData(stepsData);
            }
        }
    }

    public interface CallBack {
        void setSteps(int steps);
    }
}


