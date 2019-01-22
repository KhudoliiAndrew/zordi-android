package com.example.admin.miplus.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.admin.miplus.R;
import com.example.admin.miplus.activity.activity_in_main.MainActivity;
import com.example.admin.miplus.activity.activivity_from_main.InformationPulseActivity;
import com.example.admin.miplus.activity.activivity_from_main.InformationSleepActivity;
import com.example.admin.miplus.activity.activivity_from_main.InformationStepsActivity;

public class FirstFragment extends Fragment {
        private static final int LAYOUT = R.layout.first_activity;

    public static FirstFragment getInstance(){
        Bundle args = new Bundle();
        FirstFragment fragment = new FirstFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(LAYOUT, container, false);

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
                toSleepInformation();
            }
        });

        RelativeLayout pulseRelativeLayout = (RelativeLayout) view.findViewById(R.id.toPulseInformationCard);
        pulseRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toPulseInformation();
            }
        });
        return view;
    }

    private void toStepsInformation(){
        Intent infStepsIntent = new Intent(getActivity(), InformationStepsActivity.class );
        startActivity(infStepsIntent);
    }

    private void toSleepInformation(){
        Intent infSleepIntent = new Intent(getActivity(), InformationSleepActivity.class );
        startActivity(infSleepIntent);
    }

    private void toPulseInformation(){
        Intent infPulseIntent = new Intent(getActivity(), InformationPulseActivity.class );
        startActivity(infPulseIntent);
    }

}
