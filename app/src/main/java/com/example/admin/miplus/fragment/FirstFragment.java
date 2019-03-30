package com.example.admin.miplus.fragment;

import android.content.Context;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.admin.miplus.CustomXML.CircleProgressBar;
import com.example.admin.miplus.R;
import com.example.admin.miplus.data_base.DataBaseRepository;
import com.example.admin.miplus.data_base.models.Profile;
import com.example.admin.miplus.fragment.FirstWindow.StepsInformationFragment;
import com.example.admin.miplus.pedometr.StepCounterService;
import com.example.admin.miplus.pedometr.StepListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FirstFragment extends Fragment  {

    final DataBaseRepository dataBaseRepository = new DataBaseRepository();
    private Profile profile = new Profile();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private CircleProgressBar circleProgressBar;
    private int steps;
    private StepCounterService stepCounterService = new StepCounterService();

    public void setSteps(int steps) {
        this.steps = steps;
    }


    public FirstFragment() {

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
            viewSetter();
        } else {
            dataBaseRepository.getProfileTask()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            profile = task.getResult().toObject(Profile.class);
                            viewSetter();
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
        fragmentManager.beginTransaction().replace(R.id.fragments_container, stepsInformationFragment).addToBackStack(null).commit();
    }

    public void viewSetter() {
        /*View completeCircle = (View) getView().findViewById(R.id.complete_circle);
        completeCircle.setActivated(false);*/
        steps = stepCounterService.getSteps();
      //  TextView stepsText = (TextView) getView().findViewById(R.id.steps_cuantity_text);
      //  stepsText.setText(String.valueOf(steps));
      ///  circleProgressBar = (CircleProgressBar) getView().findViewById(R.id.circle_progress_bar);
      //  circleProgressBar.progressChange(steps, profile.getStepsTarget());
    }


}
