package com.example.admin.miplus.fragment;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.miplus.R;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ThirdFragment extends Fragment implements StepsTargetDialogFragment.PushStepsTarget, SleepRangeDialogFragment.PushSleepTarget, HeightDialogFragment.PushHeight, WeightDialogFragment.PushWeight {
    private View view;
    final DataBaseRepository dataBaseRepository = new DataBaseRepository();
    private Profile profile = new Profile();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private TextToSpeech textSay;

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
            Date date = new Date();
            if (profile.getDate().getDate() != date.getDate()) {
                profile.setWaterCount(0);
                profile.setDate(date);
                dataBaseRepository.setProfile(profile);
                setWaterCounter(view);
                textToSpeech("Drank " + profile.getWaterCount() + " milliliters of water from " + (profile.getWeight() * 35) + " milliliters");
            } else {
                setWaterCounter(view);
                textToSpeech("Drank " + profile.getWaterCount() + " milliliters of water from " + (profile.getWeight() * 35) + " milliliters");
            }
        } else {
            dataBaseRepository.getProfileTask()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            profile = task.getResult().toObject(Profile.class);
                            viewSetter(view);
                            switchSetter(view);
                            Date date = new Date();
                            if (profile.getDate().getDate() != date.getDate()) {
                                profile.setWaterCount(0);
                                profile.setDate(date);
                                dataBaseRepository.setProfile(profile);
                                setWaterCounter(view);
                                textToSpeech("Drank " + profile.getWaterCount() + " milliliters of water from " + (profile.getWeight() * 35) + " milliliters");
                            } else {
                                setWaterCounter(view);
                                textToSpeech("Drank " + profile.getWaterCount() + " milliliters of water from " + (profile.getWeight() * 35) + " milliliters");
                            }
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
        if (profile != null) {
            TextView stepsText = (TextView) view.findViewById(R.id.quantity_of_steps_text);
            TextView sleepText = (TextView) view.findViewById(R.id.sleep_length_text);
            TextView heightText = (TextView) view.findViewById(R.id.height_text);
            TextView weightText = (TextView) view.findViewById(R.id.weight_text);
            stepsText.setText(String.valueOf(profile.getStepsTarget()));
            sleepText.setText(new SimpleDateFormat("HH:mm").format(profile.getSleepTarget()));
            heightText.setText(String.valueOf(profile.getHeight() + " cm"));
            weightText.setText(String.valueOf(profile.getWeight() + " kg"));
        }
    }

    private void switchSetter(View view) {
        if (profile != null) {
            final SwitchCompat sleepSwitch = (SwitchCompat) view.findViewById(R.id.sleep_switch);
            final SwitchCompat stepsSwitch = (SwitchCompat) view.findViewById(R.id.steps_switch);
          /*  final SwitchCompat lightThemeSwitch = (SwitchCompat) view.findViewById(R.id.light_theme_switch);
            final SwitchCompat darkThemeSwitch = (SwitchCompat) view.findViewById(R.id.dark_theme_switch);*/


           /* lightThemeSwitch.setChecked(profile.getLightTheme());
            darkThemeSwitch.setChecked(!profile.getLightTheme());*/

            if (!profile.getNotifications()) {
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
                    if (isChecked) profile.setNotifications(true);
                    dataBaseRepository.setProfile(profile);
                    if (!isChecked) textToSpeech("Steps notification disabled");
                    if (isChecked) textToSpeech("Steps notification enabled");
                }
            });
            sleepSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    profile.setSleepNotification(sleepSwitch.isChecked());
                    if (isChecked) profile.setNotifications(true);
                    dataBaseRepository.setProfile(profile);
                    if (!isChecked) textToSpeech("Alarm disabled");
                    if (isChecked) textToSpeech("Alarm enabled");
                }
            });

          /*  lightThemeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
            });*/
        }
    }

    private void setWaterCounter(View view) {
        TextView textView = (TextView) view.findViewById(R.id.all_water);
        textView.setText(" /" + (profile.getWeight() * 35) + " ml");
        Button button = (Button) view.findViewById(R.id.add_water_ml);
        final TextView textView1 = (TextView) view.findViewById(R.id.water_counter_text);
        textView1.setText(String.valueOf(profile.getWaterCount()));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profile.getWaterCount() < 10000) {
                    profile.setWaterCount(profile.getWaterCount() + 250);
                    textView1.setText(String.valueOf(profile.getWaterCount()));
                    dataBaseRepository.setProfile(profile);
                    textToSpeech("Drank " + profile.getWaterCount() + " milliliters of water from " + (profile.getWeight() * 35) + " milliliters");
                } else {
                    Toast.makeText(getActivity(), "You drank too much", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void textToSpeech(final String s) {
        if (profile.getSpeak()) {
            textSay = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS && Locale.UK != null) {
                        textSay.setLanguage(Locale.UK);
                        textSay.speak(s, TextToSpeech.QUEUE_FLUSH, null);
                    } else {
                        Toast.makeText(getActivity(), "Feature not supported in your device", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void stepsTarget(int stepsTarget) {
        profile.setStepsTarget(stepsTarget);
        dataBaseRepository.setProfile(profile);
        TextView stepsText = (TextView) view.findViewById(R.id.quantity_of_steps_text);
        stepsText.setText(String.valueOf(stepsTarget));
        textToSpeech("Your steps target is" + stepsTarget + " steps");
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
    public void sleepTarget(Date sleepTarget) {
        profile.setSleepTarget(sleepTarget);
        dataBaseRepository.setProfile(profile);
        TextView sleepText = (TextView) view.findViewById(R.id.sleep_length_text);
        sleepText.setText(new SimpleDateFormat("HH:mm").format(sleepTarget));
        textToSpeech("Your sleep target is" + new SimpleDateFormat("HH:mm").format(sleepTarget));
    }

    @Override
    public void startSleep(Date startSleep) {
        profile.setStartSleep(startSleep);
        dataBaseRepository.setProfile(profile);
    }

    @Override
    public void endSleep(Date endSleep) {
        profile.setEndSleep(endSleep);
        dataBaseRepository.setProfile(profile);
    }

    @Override
    public void height(int height) {
        profile.setHeight(height);
        dataBaseRepository.setProfile(profile);
        TextView heightText = (TextView) view.findViewById(R.id.height_text);
        heightText.setText(String.valueOf(profile.getHeight() + " cm"));
        textToSpeech("Your height" + height + " cm");
    }

    @Override
    public void weight(int weight) {
        profile.setWeight(weight);
        dataBaseRepository.setProfile(profile);
        TextView weightText = (TextView) view.findViewById(R.id.weight_text);
        weightText.setText(String.valueOf(profile.getWeight() + " kg"));
        TextView textView = (TextView) view.findViewById(R.id.all_water);
        textView.setText(" /" + (profile.getWeight() * 35) + " ml");
        textToSpeech("Your weight" + weight + " kg");
    }

    @Override
    public void onPause() {
        if (textSay != null) {
            textSay.stop();
            textSay.shutdown();
        }
        super.onPause();
    }
}
