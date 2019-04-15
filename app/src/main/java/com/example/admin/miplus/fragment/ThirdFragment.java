package com.example.admin.miplus.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.miplus.R;
import com.example.admin.miplus.Services.AlarmReceiver;
import com.example.admin.miplus.data_base.DataBaseRepository;
import com.example.admin.miplus.data_base.models.Profile;
import com.example.admin.miplus.fragment.Dialogs.HeightDialogFragment;
import com.example.admin.miplus.fragment.Dialogs.SleepRangeDialogFragment;
import com.example.admin.miplus.fragment.Dialogs.StepsTargetDialogFragment;
import com.example.admin.miplus.fragment.Dialogs.WeightDialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ThirdFragment extends Fragment implements StepsTargetDialogFragment.PushStepsTarget, SleepRangeDialogFragment.PushSleepTarget, HeightDialogFragment.PushHeight, WeightDialogFragment.PushWeight {
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
            switchSetter(view);
        } else {
            dataBaseRepository.getProfileTask()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            profile = task.getResult().toObject(Profile.class);
                            viewSetter(view);
                            switchSetter(view);
                        }
                    });

        }


        TextView stepsButton = (TextView) view.findViewById(R.id.steps_watch_button);
        TextView sleepButton = (TextView) view.findViewById(R.id.waking_watch_button);
        RelativeLayout heightbutton = (RelativeLayout) view.findViewById(R.id.height_container);
        RelativeLayout weigthbutton = (RelativeLayout) view.findViewById(R.id.weight_container);

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

        heightbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profile != null) {
                    DialogFragment dlgf3 = new HeightDialogFragment(profile.getHeight(), ThirdFragment.this);
                    dlgf3.show(getFragmentManager(), "dlgf3");
                }
            }
        });

        weigthbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profile != null) {
                    DialogFragment dlgf4 = new WeightDialogFragment(profile.getWeight(), ThirdFragment.this);
                    dlgf4.show(getFragmentManager(), "dlgf4");
                }
            }
        });
        return view;
    }

    private void viewSetter(View view) {
        TextView stepsText = (TextView) view.findViewById(R.id.quantity_of_steps_text);
        TextView sleepText = (TextView) view.findViewById(R.id.sleep_length_text);
        TextView heightText = (TextView) view.findViewById(R.id.height_text);
        TextView weightText = (TextView) view.findViewById(R.id.weight_text);
        stepsText.setText(String.valueOf(profile.getStepsTarget()));
        sleepText.setText(String.valueOf(profile.getSleepTarget()));
        heightText.setText(String.valueOf(profile.getHeight() + " cm"));
        weightText.setText(String.valueOf(profile.getWeight() + " kg"));

    }

    private void switchSetter(View view) {
        if (getView() != null) {
            final SwitchCompat sleepSwitch = (SwitchCompat) view.findViewById(R.id.sleep_switch);
            final SwitchCompat stepsSwitch = (SwitchCompat) view.findViewById(R.id.steps_switch);
            final SwitchCompat lightThemeSwitch = (SwitchCompat) view.findViewById(R.id.light_theme_switch);
            final SwitchCompat darkThemeSwitch = (SwitchCompat) view.findViewById(R.id.dark_theme_switch);


            lightThemeSwitch.setChecked(profile.getLightTheme());
            darkThemeSwitch.setChecked(!profile.getLightTheme());

            if(!profile.getNotifications() ){
                stepsSwitch.setChecked(false);
                sleepSwitch.setChecked(false);
            } else {
                stepsSwitch.setChecked(profile.getStepsNotification());
                sleepSwitch.setChecked(profile.getSleepNotification());
            }

            stepsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    profile.setStepsNotification(stepsSwitch.isChecked());
                    if(isChecked) profile.setNotifications(true);
                    dataBaseRepository.setProfile(profile);
                }
            });
            sleepSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    profile.setSleepNotification(sleepSwitch.isChecked());
                    if(isChecked) profile.setNotifications(true);
                    dataBaseRepository.setProfile(profile);
                }
            });

            lightThemeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    darkThemeSwitch.setChecked(!isChecked);
                    profile.setLightTheme(isChecked);
                    dataBaseRepository.setProfile(profile);
                }
            });

            darkThemeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    lightThemeSwitch.setChecked(!isChecked);
                    profile.setLightTheme(!isChecked);
                    dataBaseRepository.setProfile(profile);
                }
            });
        }
    }

    private void setAlarm() {
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

    @Override
    public void height(int height) {
        profile.setHeight(height);
        dataBaseRepository.setProfile(profile);
        TextView heightText = (TextView) view.findViewById(R.id.height_text);
        heightText.setText(String.valueOf(profile.getHeight() + " cm"));
    }

    @Override
    public void weight(int weight) {
        profile.setWeight(weight);
        dataBaseRepository.setProfile(profile);
        TextView weightText = (TextView) view.findViewById(R.id.weight_text);
        weightText.setText(String.valueOf(profile.getWeight() + " kg"));
    }

}
