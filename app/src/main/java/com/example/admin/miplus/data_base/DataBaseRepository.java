package com.example.admin.miplus.data_base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.admin.miplus.data_base.models.GeoPoint;
import com.example.admin.miplus.data_base.models.Profile;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class DataBaseRepository {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Profile profile;
    private GeoPoint geoPoint;
    final FirebaseUser currentUser = mAuth.getCurrentUser();

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
}

