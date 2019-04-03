package com.example.admin.miplus.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.admin.miplus.R;
import com.example.admin.miplus.Services.AlarmReceiver;
import com.example.admin.miplus.data_base.DataBaseRepository;
import com.example.admin.miplus.data_base.models.Profile;
import com.example.admin.miplus.fragment.Dialogs.SleepRangeDialogFragment;
import com.example.admin.miplus.fragment.Dialogs.StepsTargetDialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ThirdFragment extends Fragment implements StepsTargetDialogFragment.PushStepsTarget, SleepRangeDialogFragment.PushSleepTarget {
    private View view;
    final DataBaseRepository dataBaseRepository = new DataBaseRepository();
    private Profile profile = new Profile();
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
        view = inflater.inflate(R.layout.third_activity, container, false);

        if (dataBaseRepository.getProfile() != null) {
            profile = dataBaseRepository.getProfile();
            viewSetter(view);
        } else {
            dataBaseRepository.getProfileTask()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            profile = task.getResult().toObject(Profile.class);
                            viewSetter(view);
                        }
                    });

        }


        Button stepsButton = (Button) view.findViewById(R.id.steps_watch_button);
        Button sleepButton = (Button) view.findViewById(R.id.waking_watch_button);

        sleepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profile != null) {
                    DialogFragment dlgf2 = new SleepRangeDialogFragment(profile.getSleepTarget(), profile.getStartSleep(), profile.getEndSleep(), profile.getStartRadian(), profile.getEndRadian(), ThirdFragment.this);
                    dlgf2.show(getFragmentManager(), "dlgf2");
                }
            }
        });

        stepsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profile != null) {
                    DialogFragment dlgf1 = new StepsTargetDialogFragment(profile.getStepsTarget(), ThirdFragment.this);
                    dlgf1.show(getFragmentManager(), "dlgf1");
                }

            }
        });
        return view;
    }

    private void viewSetter(View view){
        TextView stepsText = (TextView) view.findViewById(R.id.quantity_of_steps_text);
        stepsText.setText(String.valueOf(profile.getStepsTarget()));
        TextView sleepText = (TextView) view.findViewById(R.id.sleep_length_text);
        sleepText.setText(String.valueOf(profile.getSleepTarget()));
    }

    private void setAlarm(){
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, Integer.parseInt(profile.getEndSleep()), pendingIntent);
    }

    @Override
    public void stepsTarget(int stepsTarget) {
        profile.setStepsTarget(stepsTarget);
        dataBaseRepository.setProfile(profile);
        TextView stepsText = (TextView) view.findViewById(R.id.quantity_of_steps_text);
        stepsText.setText(String.valueOf(stepsTarget));
    }

    @Override
    public void startRadian(float startRadian) {
        profile.setStartRadian(startRadian);
        dataBaseRepository.setProfile(profile);
    }

    @Override
    public void endRadian(float endRadian) {
        profile.setEndRadian(endRadian);
        dataBaseRepository.setProfile(profile);
    }

    @Override
    public void sleepTarget(String sleepTarget) {
        profile.setSleepTarget(sleepTarget);
        dataBaseRepository.setProfile(profile);
        TextView sleepText = (TextView) view.findViewById(R.id.sleep_length_text);
        sleepText.setText(String.valueOf(sleepTarget));
    }

    @Override
    public void startSleep(String startSleep) {
        profile.setStartSleep(startSleep);
        dataBaseRepository.setProfile(profile);
    }

    @Override
    public void endSleep(String endSleep) {
        profile.setEndSleep(endSleep);
        dataBaseRepository.setProfile(profile);
    }
}
