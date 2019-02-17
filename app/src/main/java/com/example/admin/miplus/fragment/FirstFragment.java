package com.example.admin.miplus.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.admin.miplus.R;

public class FirstFragment extends Fragment  {

    public static FirstFragment getInstance() {
        Bundle args = new Bundle();
        FirstFragment fragment = new FirstFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_activity, container, false);

        RelativeLayout stepsRelativeLayout = (RelativeLayout) view.findViewById(R.id.toStepsInformationCard);
        stepsRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
}
