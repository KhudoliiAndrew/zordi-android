package com.example.admin.miplus.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.admin.miplus.R;
import com.example.admin.miplus.activity.SplashActivity;
import com.example.admin.miplus.activity.activity_in_main.BluetoothConnectionActivity;
import com.example.admin.miplus.activity.activity_in_main.MainActivity;
import com.example.admin.miplus.data_base.DataBaseRepository;
import com.example.admin.miplus.data_base.models.Profile;

import java.lang.reflect.Field;

public class ThirdFragment extends Fragment {
    private static final int LAYOUT = R.layout.third_activity;
    private View view;
    static Dialog dialog;


    public static ThirdFragment getInstance() {
        Bundle args = new Bundle();
        ThirdFragment fragment = new ThirdFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        final Profile profile = new Profile();
        profile.setSleepTarget(11111);
        profile.setStepsTarget(22222);
        final DataBaseRepository dataBaseRepository = new DataBaseRepository();
        Button stepsButton = (Button) view.findViewById(R.id.steps_watch_button);
        Button sleepButton = (Button) view.findViewById(R.id.waking_watch_button);
        ConstraintLayout connectiionField = (ConstraintLayout) view.findViewById(R.id.connection_field);

        connectiionField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Intent userIntent = new Intent(getActivity(), BluetoothConnectionActivity.class);
                startActivity(userIntent);*/
                dataBaseRepository.setProfile(profile);
            }
        });
        sleepButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                showSleepDialog();
            }
        });

        stepsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                showStepsDialog();
            }
        });

        return view;
    }

    private void showStepsDialog() {
        int step = 100;
        int min = 1;
        int max = 50;

        final Dialog dialog = new Dialog(getActivity());

        dialog.setContentView(R.layout.steps_dialog);
        final NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.StepsPicker);
        Button confirmButton = (Button) dialog.findViewById(R.id.ok_button_picker);

        numberPicker.setMaxValue(max);
        numberPicker.setValue(8000);
        numberPicker.setMinValue(min);

        NumberPicker.Formatter formatter = new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                int temp = value * 1000;

                return "" + temp;
            }
        };
        numberPicker.setFormatter(formatter);
        numberPicker.setWrapSelectorWheel(false);
        changePickerColor(numberPicker, Color.TRANSPARENT);

        EditText numberPickerChild = (EditText) numberPicker.getChildAt(0);
        numberPickerChild.setFocusable(false);
        numberPickerChild.setInputType(InputType.TYPE_NULL);

        confirmButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                TextView stepsText = (TextView) view.findViewById(R.id.quantity_of_steps_text);
                stepsText.setText(String.valueOf(numberPicker.getValue() * 1000));
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showSleepDialog(){
        final Dialog dialog = new Dialog(getActivity());

        dialog.setContentView(R.layout.sleep_dialog);
        final NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.StepsPicker);
        Button confirmButton = (Button) dialog.findViewById(R.id.ok_button_sleep_picker);
        confirmButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void changePickerColor(NumberPicker picker, int color) {
        try {
            Field mField = NumberPicker.class.getDeclaredField("mSelectionDivider");
            mField.setAccessible(true);
            ColorDrawable colorDrawable = new ColorDrawable(color);
            mField.set(picker, colorDrawable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
