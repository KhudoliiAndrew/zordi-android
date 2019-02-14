package com.example.admin.miplus.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
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
import com.example.admin.miplus.fragment.Dialogs.StepsTargetDialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Field;

public class ThirdFragment extends Fragment implements StepsTargetDialogFragment.PushStepsTarget {
    private static final int LAYOUT = R.layout.third_activity;
    private View view;
    final DataBaseRepository dataBaseRepository = new DataBaseRepository();
    private Profile profile;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

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

        Button stepsButton = (Button) view.findViewById(R.id.steps_watch_button);
        Button sleepButton = (Button) view.findViewById(R.id.waking_watch_button);
        ConstraintLayout connectiionField = (ConstraintLayout) view.findViewById(R.id.connection_field);

        connectiionField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(getActivity(), BluetoothConnectionActivity.class);
                startActivity(userIntent);
            }
        });
        sleepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSleepDialog();
            }
        });

        stepsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(profile != null){
                    DialogFragment dlgf1 = new StepsTargetDialogFragment(profile.getStepsTarget(), ThirdFragment.this);
                    dlgf1.show(getFragmentManager(), "dlgf1");
                }

            }
        });

        dataBaseRepository.getProfile()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        profile = task.getResult().toObject(Profile.class);
                        TextView stepsText = (TextView) view.findViewById(R.id.quantity_of_steps_text);
                        stepsText.setText(String.valueOf(profile.getStepsTarget()));
                    }
                });
        return view;
    }

    private void showSleepDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.sleep_dialog);
        final NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.StepsPicker);
        Button confirmButton = (Button) dialog.findViewById(R.id.ok_button_sleep_picker);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void stepsTarget(int stepsTarget) {
        profile.setStepsTarget(stepsTarget);
        dataBaseRepository.setProfile(profile);
        TextView stepsText = (TextView) view.findViewById(R.id.quantity_of_steps_text);
        stepsText.setText(String.valueOf(stepsTarget));
    }
}
