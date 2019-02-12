package com.example.admin.miplus.data_base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.admin.miplus.data_base.models.Profile;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class DataBaseRepository {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    final FirebaseUser currentUser = mAuth.getCurrentUser();

    public void setProfile(Profile profile){
        db.collection("profiles").document(mAuth.getUid()).set(profile);
    }

    public Task<DocumentSnapshot> getProfile() {
        return db.collection("profiles").document(mAuth.getUid()).get();
    }
}
