package com.example.admin.miplus.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;

import com.example.admin.miplus.R;

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
                showStepsDialog();
            }
        });

        RelativeLayout sleepRelativeLayout = (RelativeLayout) view.findViewById(R.id.toSleepInformationCard);
        sleepRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSleepDialog();
            }
        });

        RelativeLayout pulseRelativeLayout = (RelativeLayout) view.findViewById(R.id.toPulseInformationCard);
        pulseRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPulseDialog();
            }
        });
        return view;
    }

    private void showStepsDialog(){
        final Dialog dialog = new Dialog(getActivity());

        dialog.setContentView(R.layout.steps_information_dialog);
        final NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.StepsPicker);
        Button confirmButton = (Button) dialog.findViewById(R.id.ok_button_steps_information_dialog);
        confirmButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showSleepDialog(){
        final Dialog dialog = new Dialog(getActivity());

        dialog.setContentView(R.layout.sleep_information_dialog);
        final NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.StepsPicker);
        Button confirmButton = (Button) dialog.findViewById(R.id.ok_button_sleep_information_dialog);
        confirmButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void showPulseDialog(){
        final Dialog dialog = new Dialog(getActivity());

        dialog.setContentView(R.layout.pulse_information_dialog);
        final NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.StepsPicker);
        Button confirmButton = (Button) dialog.findViewById(R.id.ok_button_pulse_information_dialog);
        confirmButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
