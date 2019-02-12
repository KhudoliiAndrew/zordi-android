package com.example.admin.miplus.fragment.Dialogs;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.miplus.R;
import com.example.admin.miplus.data_base.DataBaseRepository;
import com.example.admin.miplus.data_base.models.Profile;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Field;

import static com.facebook.FacebookSdk.getApplicationContext;

public class StepsTargetDialogFragment extends DialogFragment implements View.OnClickListener {
    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        view = inflater.inflate(R.layout.steps_dialog, null);
        view.findViewById(R.id.ok_button_picker).setOnClickListener(this);

        int step = 100;
        int min = 1;
        int max = 50;

        final NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.StepsPicker);

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

        return view;
    }

    @Override
    public void onClick(View v) {
        final NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.StepsPicker);
        final DataBaseRepository dataBaseRepository = new DataBaseRepository();
        final Profile profile = new Profile();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        profile.setStepsTarget(numberPicker.getValue() * 1000);
        dataBaseRepository.setProfile(profile);
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
}
