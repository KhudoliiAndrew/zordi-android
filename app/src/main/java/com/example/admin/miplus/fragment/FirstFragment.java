package com.example.admin.miplus.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.miplus.CustomXML.CircleProgressBar;
import com.example.admin.miplus.R;
import com.example.admin.miplus.data_base.DataBaseRepository;
import com.example.admin.miplus.data_base.models.Profile;
import com.example.admin.miplus.data_base.models.SleepData;
import com.example.admin.miplus.data_base.models.StepsData;
import com.example.admin.miplus.fragment.FirstWindow.FriendsFragment;
import com.example.admin.miplus.fragment.FirstWindow.SleepInformationFragment;
import com.example.admin.miplus.fragment.FirstWindow.StepsInformationFragment;
import com.example.admin.miplus.pedometr.StepCounterService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class FirstFragment extends Fragment implements StepCounterService.CallBack {

    final DataBaseRepository dataBaseRepository = new DataBaseRepository();
    private Profile profile = new Profile();
    private StepsData stepsData = new StepsData();
    private StepsData stepsDayData = new StepsData();
    private CircleProgressBar circleProgressBar;
    private int steps;
    private StepCounterService stepCounterService;
    private List<StepsData> stepsDataList = new ArrayList<>();
    private SleepData sleepData = new SleepData();
    private List<SleepData> sleepDataList = new ArrayList<>();

    private TextToSpeech textSay;

    public void setSteps(int steps) {
        this.steps = steps;
        if (getView() != null) {
            TextView stepsText = getView().findViewById(R.id.steps_cuantity_text);
            stepsText.setText(String.valueOf(steps));
            circleProgressBar = getView().findViewById(R.id.circle_progress_bar);
            if(profile.getStepsTarget() != 0){
                circleProgressBar.progressChange(steps, profile.getStepsTarget());
            }
            TextView cardDistanceText = getView().findViewById(R.id.distance_card_text);
            float distance = ((((profile.getHeight() * 0.01f) / 4) + 0.37f) * steps) * 0.001f;
            String formattedDouble = new DecimalFormat("#0.00").format(distance);
            cardDistanceText.setText(formattedDouble + " km");
        }
    }

    public static FirstFragment getInstance() {
        Bundle args = new Bundle();
        FirstFragment fragment = new FirstFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.first_activity, container, false);

        if (dataBaseRepository.getProfile() != null) {
            profile = dataBaseRepository.getProfile();
        } else {
            dataBaseRepository.getProfileTask()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            profile = Objects.requireNonNull(task.getResult()).toObject(Profile.class);
                        }
                    });
        }

        dataBaseRepository.getStepsDataListOrderedDate()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            Date date = new Date();
                            stepsDataList = task.getResult().toObjects(StepsData.class);
                            stepsData = stepsDataList.get(stepsDataList.size() - 1);
                            if (date.getDate() != stepsData.getDate().getDate()) {
                                stepsData.setSteps(stepsData.getSteps());
                                stepsData.setDate(stepsData.getDate());
                                dataBaseRepository.setStepsDataByDay(stepsData);
                                stepsData.setDefaultInstance();
                                dataBaseRepository.setStepsData(stepsData);
                                textToSpeech(stepsData.getSteps() + "steps completed");
                            } else {
                                steps = stepsData.getSteps();
                                viewSetter(view);
                                textToSpeech(stepsData.getSteps() + "steps completed");
                            }
                        } else {
                            stepsData.setDefaultInstance();
                            steps = stepsData.getSteps();
                            dataBaseRepository.setStepsData(stepsData);
                            textToSpeech(stepsData.getSteps() + "steps completed");
                        }
                    }

                });

        dataBaseRepository.getStepsDataByDay()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            stepsDataList = task.getResult().toObjects(StepsData.class);
                            stepsDayData = stepsDataList.get(stepsDataList.size() - 1);
                            viewSetter(view);
                        } else {
                            viewSetter(view);
                        }
                    }
                });

        dataBaseRepository.getSleepData()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            Date date = new Date();
                            sleepDataList = task.getResult().toObjects(SleepData.class);
                            sleepData = sleepDataList.get(sleepDataList.size() - 1);
                            if (date.getDate() != sleepData.getDate().getDate()) {
                                if (profile != null) {
                                    sleepData.setStartSleep(setRandomSleep(profile.getStartSleep()));
                                    sleepData.setEndSleep(setRandomSleep(profile.getEndSleep()));
                                    sleepData.setDate(date);
                                    dataBaseRepository.setSleepData(sleepData);
                                }
                            }
                            sleepCardSetter(view);
                        } else {
                            Date date = new Date();
                            if (profile != null) {
                                sleepData.setStartSleep(setRandomSleep(profile.getStartSleep()));
                                sleepData.setEndSleep(setRandomSleep(profile.getEndSleep()));
                                sleepData.setDate(date);
                                dataBaseRepository.setSleepData(sleepData);
                                sleepCardSetter(view);
                            }
                        }
                    }
                });

        bindService();

        listenerSet(view);

        return view;
    }

    private void listenerSet(View view) {
        RelativeLayout stepsRelativeLayout = view.findViewById(R.id.toStepsInformationCard);
        stepsRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.requireNonNull(getActivity()).getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    StepsInformationFragment stepsInformationFragment = new StepsInformationFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    Objects.requireNonNull(fragmentManager).beginTransaction().replace(R.id.fragments_container, stepsInformationFragment).addToBackStack(null).commit();
                }
            }
        });

        RelativeLayout sleepRelativeLayout = view.findViewById(R.id.toSleepInformationCard);
        sleepRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.requireNonNull(getActivity()).getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    SleepInformationFragment sleepInformationFragment = new SleepInformationFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    Objects.requireNonNull(fragmentManager).beginTransaction().replace(R.id.fragments_container, sleepInformationFragment).addToBackStack(null).commit();

                }
            }
        });

        RelativeLayout friendsRelativeLayout = view.findViewById(R.id.toFriendsCard);
        friendsRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.requireNonNull(getActivity()).getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    FriendsFragment friendsFragment = new FriendsFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    Objects.requireNonNull(fragmentManager).beginTransaction().replace(R.id.fragments_container, friendsFragment).addToBackStack(null).commit();
                }
            }
        });
    }

    public void viewSetter(View view) {
        TextView stepsText = view.findViewById(R.id.steps_cuantity_text);
        circleProgressBar = view.findViewById(R.id.circle_progress_bar);
        TextView cardDistanceText = view.findViewById(R.id.distance_card_text);
        TextView yestedayStepsText = view.findViewById(R.id.yesterday_steps_text);


        if (stepsData != null && profile != null && stepsDayData != null) {
            stepsText.setText(String.valueOf(stepsData.getSteps()));
            if(profile.getStepsTarget() != 0){
                circleProgressBar.progressChange(stepsData.getSteps(), profile.getStepsTarget());
            }
            float distance = ((((profile.getHeight() * 0.01f) / 4) + 0.37f) * stepsData.getSteps()) * 0.001f;
            String formattedDouble = new DecimalFormat("#0.00").format(distance);
            cardDistanceText.setText(formattedDouble + " km");
            yestedayStepsText.setText(stepsDayData.getSteps() + " steps");
        }

    }

    private void sleepCardSetter(View view) {
        TextView startSleepText = view.findViewById(R.id.start_sleep_text_first_fragment);
        TextView endSleepText = view.findViewById(R.id.end_sleep_text_first_fragment);

        startSleepText.setText(new SimpleDateFormat("HH:mm").format(sleepData.getStartSleep()));
        endSleepText.setText(new SimpleDateFormat("HH:mm").format(sleepData.getEndSleep()));
    }

    private Date setRandomSleep(final Date startSleep) {
        final Date whenStartTime = new Date();

        if (dataBaseRepository.getProfile() != null) {
            profile = dataBaseRepository.getProfile();
            whenStartTime.setTime((long) (Math.random() * ((startSleep.getTime() + 7200000) - (startSleep.getTime() - 3600000) + 3600000) + (startSleep.getTime() - 3600000)));
            return whenStartTime;
        } else {
            dataBaseRepository.getProfileTask()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            profile = Objects.requireNonNull(task.getResult()).toObject(Profile.class);
                            if (startSleep != null) {
                                whenStartTime.setTime((long) (Math.random() * ((startSleep.getTime() + 7200000) - (startSleep.getTime() - 3600000) + 3600000) + (startSleep.getTime() - 3600000)));
                            }
                        }
                    });
            return whenStartTime;
        }
    }

    private void textToSpeech(final String text) {
            if (profile.getSpeak()) {
                textSay = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS && Locale.UK != null) {
                            textSay.setLanguage(Locale.UK);
                            textSay.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                        } else {
                            Toast.makeText(getActivity(), "Feature not supported in your device", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            StepCounterService.MyBinder binder = (StepCounterService.MyBinder) service;
            stepCounterService = binder.getService();
            stepCounterService.setCallBack(FirstFragment.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            if (stepCounterService != null) stepCounterService.setCallBack(null);
        }
    };

    private void bindService() {
        Intent intent = new Intent(getActivity(), StepCounterService.class);
        if(getActivity() != null){
            getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onPause() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (stepsData != null && steps != stepsData.getSteps() && mAuth.getUid() != null) {
            stepsData.setSteps(steps);
            stepsData.setDate(new Date());
            dataBaseRepository.setStepsData(stepsData);
        }

        if (textSay != null) {
            textSay.stop();
            textSay.shutdown();
        }
        super.onPause();
    }
}
