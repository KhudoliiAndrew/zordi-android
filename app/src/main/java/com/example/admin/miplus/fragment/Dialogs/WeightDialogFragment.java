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

public class WeightDialogFragment extends DialogFragment implements View.OnClickListener {

    private View view;
    private int previsiourWeight;
    private PushWeight pushWeight;

    @SuppressLint("ValidFragment")
    public WeightDialogFragment(int previsiourWeight, PushWeight pushWeight) {
        this.previsiourWeight = previsiourWeight;
        this.pushWeight = pushWeight;
    }

    public WeightDialogFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        view = inflater.inflate(R.layout.weight_dialog, null);
        view.findViewById(R.id.ok_button_picker).setOnClickListener(this);

        int min = 20;
        int max = 210;
        final NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.weight_picker);
        numberPicker.setMaxValue(max);
        numberPicker.setValue(previsiourWeight);
        numberPicker.setMinValue(min);

        NumberPicker.Formatter formatter = new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                int temp = value;
                return  temp + " kg" ;
            }
        };
        numberPicker.setFormatter(formatter);
        numberPicker.setWrapSelectorWheel(false);
        changePickerColor(numberPicker, Color.TRANSPARENT);

        View firstItem = (View) numberPicker.getChildAt(0);
        if (firstItem != null) {
            firstItem.setVisibility(View.INVISIBLE);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        final NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.weight_picker);
        int weightNumber = numberPicker.getValue() ;
        if (pushWeight != null) {
            pushWeight.weight(weightNumber);
        }
        dismiss();
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

    public interface PushWeight {
        void weight(int stepsTarget);
    }
}
