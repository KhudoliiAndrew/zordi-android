package com.example.admin.miplus.data_base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.admin.miplus.data_base.models.GeoData;
import com.example.admin.miplus.data_base.models.Profile;
import com.example.admin.miplus.data_base.models.StepsData;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;


public class DataBaseRepository {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Profile profile;
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

    public void setStepsData(StepsData stepsData){
        db.collection("stepsData").document(mAuth.getUid()).collection("stepsHistory").document().set(stepsData);
    }

    public Task<QuerySnapshot> getStepsDataListOrderedDate(){
        return db.collection("stepsData").document(mAuth.getUid()).collection("stepsHistory").orderBy("date").get();
    }

    public void setGeoData(GeoData geoData){
        db.collection("geopositions").document(mAuth.getUid()).collection("LocationHistory").document().set(geoData);
    }

    public void deleteStepsDataDocuments(){
        db.collection("stepsData").document(mAuth.getUid()).collection("stepsHistory").document().delete();
    }

    public void setStepsDataByDay(StepsData stepsData){
        db.collection("stepsData").document(mAuth.getUid()).collection("stepsDay").document().set(stepsData);
    }

    public Task<QuerySnapshot> getStepsDataByDay(){
        return db.collection("stepsData").document(mAuth.getUid()).collection("stepsDay").orderBy("date").get();
    }
}

