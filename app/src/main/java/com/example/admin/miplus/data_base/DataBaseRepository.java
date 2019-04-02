package com.example.admin.miplus.data_base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.admin.miplus.data_base.models.GeoPoint;
import com.example.admin.miplus.data_base.models.Profile;
import com.example.admin.miplus.data_base.models.StepsData;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DataBaseRepository {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Profile profile;
    private GeoPoint geoPoint;
    private StepsData stepsData;

    public void setProfile(Profile profile){
        db.collection("profiles").document(mAuth.getUid()).set(profile);
    }

    public Task<DocumentSnapshot> getProfileTask() {
        final Task<DocumentSnapshot> task = db.collection("profiles").document(mAuth.getUid()).get();
        task.onSuccessTask(new SuccessContinuation<DocumentSnapshot, Profile>(){
            @NonNull
            @Override
            public Task<Profile> then(@Nullable DocumentSnapshot documentSnapshot) throws Exception {
                profile = documentSnapshot.toObject(Profile.class);
                return null;
            }
        });
        return task;
    }

    public Profile getProfile(){
        return profile;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        db.collection("geopositions").document(mAuth.getUid()).set(geoPoint);
    }

    public Task<DocumentSnapshot> getGeopointTask(){
        final Task<DocumentSnapshot> task = db.collection("geopositions").document(mAuth.getUid()).get();
        task.onSuccessTask(new SuccessContinuation<DocumentSnapshot, GeoPoint>(){

            @NonNull
            @Override
            public Task<GeoPoint> then(@Nullable DocumentSnapshot documentSnapshot) throws Exception {
                geoPoint = documentSnapshot.toObject(GeoPoint.class);
                return null;
            }
        });
        return task;
    }

    public GeoPoint getGeoPoint(){
        return geoPoint;
    }

    public void setStepsData(StepsData stepsData){
        Date currentDate = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(currentDate);
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        db.collection("stepsInformation").document(mAuth.getUid()).collection(String.valueOf(formattedDate)).document(timeFormat.format(currentDate)).set(stepsData);
        Log.d("TIMETIMETIMETIMETIME",  "DATA: " + String.valueOf(formattedDate) + "Time: "  + timeFormat.format(currentDate));
    }
}

