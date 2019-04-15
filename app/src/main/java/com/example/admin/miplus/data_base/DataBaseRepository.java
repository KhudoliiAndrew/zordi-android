package com.example.admin.miplus.data_base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.admin.miplus.data_base.models.GeoData;
import com.example.admin.miplus.data_base.models.GeoSettings;
import com.example.admin.miplus.data_base.models.Profile;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DataBaseRepository {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Profile profile;
    private GeoData geoData;
    private GeoSettings mapType;

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

    DateFormat df = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss");
    String date = df.format(Calendar.getInstance().getTime());

    public void setGeoData(GeoData geoData){
        db.collection("geopositions").document(mAuth.getUid()).collection("LocationHistory").document().set(geoData);
    }

    public Task<QuerySnapshot> getGeoDataTask() {
        final Task<QuerySnapshot> task = db.collection("geopositions").document(mAuth.getUid()).collection("LocationHistory").get();
        task.onSuccessTask(new SuccessContinuation<QuerySnapshot, GeoData>() {
            @NonNull
            @Override
            public Task<GeoData> then(@Nullable QuerySnapshot querySnapshot) throws Exception {
                Query dateFilter = db.collection("geopositions").document(mAuth.getUid()).collection("LocationHistory").orderBy(date);
                geoData = (GeoData) querySnapshot.toObjects(GeoData.class);
                return null;
            }
        });
        return task;
    }

    public GeoData getGeoData(){
        return geoData;
    }

    public void setMapSettings(GeoSettings mapType){
        db.collection("geopositions").document(mAuth.getUid()).collection("MapSettings").document("MapType").set(mapType);
    }

    public Task<DocumentSnapshot> getMapSettingsTask() {
        final Task<DocumentSnapshot> task = db.collection("geopositions").document(mAuth.getUid()).collection("MapSettings").document("MapType").get();
        task.onSuccessTask(new SuccessContinuation<DocumentSnapshot, GeoSettings>(){

            @NonNull
            @Override
            public Task<GeoSettings> then(@Nullable DocumentSnapshot documentSnapshot) throws Exception {
                mapType = documentSnapshot.toObject(GeoSettings.class);
                return null;
            }
        });
        return task;
    }

    public GeoSettings getMapSettings(){
        return mapType;
    }
}
