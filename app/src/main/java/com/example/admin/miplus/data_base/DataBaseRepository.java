package com.example.admin.miplus.data_base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.admin.miplus.data_base.models.Profile;
import com.example.admin.miplus.data_base.models.StepsData;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


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

   /* public Task<QuerySnapshot> getStepsDataTask() {
        final ArrayList<StepsData> stepsDataList = null;
        final Task<QuerySnapshot> task = db.collection("stepsData").document(mAuth.getUid()).collection("stepsHistory").get();

        task.onSuccessTask(new SuccessContinuation<QuerySnapshot, StepsData>(){
            @NonNull
            @Override
            public Task<StepsData> then(@Nullable QuerySnapshot querySnapshot) throws Exception {
                stepsDataList = querySnapshot.toObjects(StepsData.class);
                return null;
            }
        });
        return task;
    }*/


}

