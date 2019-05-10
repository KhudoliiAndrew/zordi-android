package com.example.admin.miplus.fragment.Dialogs;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.example.admin.miplus.R;

import java.lang.reflect.Field;
import java.util.Objects;

public class StepsTargetDialogFragment extends DialogFragment implements View.OnClickListener {
    View view;
    private final int MULTIPLIER_RESULT = 1000;
    private int previsiourSteps;
    private PushStepsTarget pushStepsTarget;

    @SuppressLint("ValidFragment")
    public StepsTargetDialogFragment(int previsiourSteps, PushStepsTarget pushStepsTarget) {
        this.previsiourSteps = previsiourSteps;
        this.pushStepsTarget = pushStepsTarget;
    }

    public StepsTargetDialogFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        view = inflater.inflate(R.layout.steps_dialog, null);
        view.findViewById(R.id.ok_button_picker).setOnClickListener(this);

        int min = 1;
        int max = 50;
        final NumberPicker numberPicker = view.findViewById(R.id.StepsPicker);
        numberPicker.setMaxValue(max);
        numberPicker.setValue(previsiourSteps / MULTIPLIER_RESULT);
        numberPicker.setMinValue(min);

        NumberPicker.Formatter formatter = new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                int temp = value * MULTIPLIER_RESULT;
                return "" + temp;
            }
        };
        numberPicker.setFormatter(formatter);
        numberPicker.setWrapSelectorWheel(false);
        changePickerColor(numberPicker);

        View firstItem = numberPicker.getChildAt(0);
        if (firstItem != null) {
            firstItem.setVisibility(View.INVISIBLE);
        }
         return view;
    }

    @Override
    public void onClick(View v) {
        final NumberPicker numberPicker = view.findViewById(R.id.StepsPicker);
        int stepsTargetNumber = numberPicker.getValue() * MULTIPLIER_RESULT;
        if (pushStepsTarget != null) {
            pushStepsTarget.stepsTarget(stepsTargetNumber);
        }
        dismiss();
    }

    private void changePickerColor(NumberPicker picker) {
        try {
            Field mField = NumberPicker.class.getDeclaredField("mSelectionDivider");
            mField.setAccessible(true);
            ColorDrawable colorDrawable = new ColorDrawable(Color.TRANSPARENT);
            mField.set(picker, colorDrawable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface PushStepsTarget {
        void stepsTarget(int stepsTarget);
    }
}
