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
import com.example.admin.miplus.fragment.ThirdFragment;

import java.lang.reflect.Field;

public class HeightDialogFragment extends DialogFragment implements View.OnClickListener {

    private View view;
    private int previsiourHeight;
    private PushHeight pushHeight;

    @SuppressLint("ValidFragment")
    public HeightDialogFragment(int previsiourHeight, PushHeight pushHeight) {
        this.previsiourHeight = previsiourHeight;
        this.pushHeight = pushHeight;
    }

    public HeightDialogFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        view = inflater.inflate(R.layout.height_dialog, null);
        view.findViewById(R.id.ok_button_picker).setOnClickListener(this);

        int min = 30;
        int max = 280;
        final NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.height_picker);
        numberPicker.setMaxValue(max);
        numberPicker.setValue(previsiourHeight);
        numberPicker.setMinValue(min);

        NumberPicker.Formatter formatter = new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                int temp = value;
                return  temp + " cm" ;
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
        final NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.height_picker);
        int heightNumber = numberPicker.getValue() ;
        if (pushHeight != null) {
            pushHeight.height(heightNumber);
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

    public interface PushHeight {
        void height(int stepsTarget);
    }
}
