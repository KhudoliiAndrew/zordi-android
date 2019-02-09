package com.example.admin.miplus.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
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
import android.widget.TextView;

import com.example.admin.miplus.R;
import com.example.admin.miplus.activity.activity_in_main.StepsInformationActivity;

public class FirstFragment extends Fragment implements SensorEventListener {
    private static final int LAYOUT = R.layout.first_activity;
    private int steps = 10;

    public static FirstFragment getInstance() {
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
                showStepsActivity();
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

    private void showStepsActivity() {
        Intent intent = new Intent(getActivity(), StepsInformationActivity.class);
        startActivity(intent);
    }

    private void showSleepDialog() {
        final Dialog dialog = new Dialog(getActivity());

        dialog.setContentView(R.layout.sleep_information_dialog);
        final NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.StepsPicker);
        Button confirmButton = (Button) dialog.findViewById(R.id.ok_button_sleep_information_dialog);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showPulseDialog() {
        final Dialog dialog = new Dialog(getActivity());

        dialog.setContentView(R.layout.pulse_information_dialog);
        final NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.StepsPicker);
        Button confirmButton = (Button) dialog.findViewById(R.id.ok_button_pulse_information_dialog);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor sensor = event.sensor;
        float[] values = event.values;
        int value = -1;

        if (values.length > 0) {
            value = (int) values[0];
        }


        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            steps++;
            TextView textView = (TextView)  getView().findViewById(R.id.how_many_steps_text);
            textView.setText(steps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
