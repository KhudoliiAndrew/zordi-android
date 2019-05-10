package com.example.admin.miplus.fragment.FirstWindow;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.miplus.R;
import com.example.admin.miplus.data_base.DataBaseRepository;
import com.example.admin.miplus.data_base.models.Profile;
import com.example.admin.miplus.data_base.models.SleepData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

public class SleepInformationFragment extends Fragment {

    final DataBaseRepository dataBaseRepository = new DataBaseRepository();
    private SleepData sleepData = new SleepData();
    private List<SleepData> sleepDataList = new ArrayList<SleepData>();
    private Profile profile = new Profile();
    private TextToSpeech textSay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.sleep_information_fragment, container, false);

        if (dataBaseRepository.getProfile() != null) {
            profile = dataBaseRepository.getProfile();
            dataBaseRepository.getSleepData()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.getResult() != null && !task.getResult().isEmpty()) {
                                Date date = new Date();
                                sleepDataList = task.getResult().toObjects(SleepData.class);
                                sleepData = sleepDataList.get(sleepDataList.size() - 1);
                                if (profile != null) {
                                    if (date.getDate() != sleepData.getDate().getDate()) {
                                        sleepData.setStartSleep(profile.getStartSleep());
                                        sleepData.setEndSleep(profile.getEndSleep());
                                        sleepData.setDate(date);
                                        dataBaseRepository.setSleepData(sleepData);
                                    }
                                    adviceSetter(view);
                                }

                                viewSetter(view);
                                initMonthChart(view);
                            } else {
                                Date date = new Date();
                                if (profile != null) {
                                    sleepData.setStartSleep(profile.getStartSleep());
                                    sleepData.setEndSleep(profile.getEndSleep());
                                    sleepData.setDate(date);
                                    dataBaseRepository.setSleepData(sleepData);
                                    adviceSetter(view);
                                }
                                viewSetter(view);
                                initMonthChart(view);
                            }
                        }
                    });
        } else {
            dataBaseRepository.getProfileTask()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            profile = Objects.requireNonNull(task.getResult()).toObject(Profile.class);
                            dataBaseRepository.getSleepData()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.getResult() != null && !task.getResult().isEmpty()) {
                                                sleepDataList = task.getResult().toObjects(SleepData.class);
                                                sleepData = sleepDataList.get(sleepDataList.size() - 1);
                                                if (profile != null) {

                                                    adviceSetter(view);
                                                }

                                                viewSetter(view);
                                                initMonthChart(view);
                                            } else {
                                                if (profile != null) {
                                                    adviceSetter(view);
                                                }
                                                viewSetter(view);
                                                initMonthChart(view);
                                            }
                                        }
                                    });
                        }
                    });
        }


        initToolbar();

        return view;
    }

    private void textToSpeech(final String text, final String text2, final String text3, final String text4, final String text5) {
        if (profile.getSpeak()) {
            textSay = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS && Locale.UK != null) {
                        textSay.setLanguage(Locale.UK);
                        textSay.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                        textSay.speak(text2, TextToSpeech.QUEUE_ADD, null);
                        textSay.speak(text3, TextToSpeech.QUEUE_ADD, null);
                        textSay.speak(text4, TextToSpeech.QUEUE_ADD, null);
                        textSay.speak(text5, TextToSpeech.QUEUE_ADD, null);
                    } else {
                        Toast.makeText(getActivity(), "Feature not supported in your device", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void initToolbar() {
        Toolbar toolbar = Objects.requireNonNull(getActivity()).findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        Objects.requireNonNull(actionbar).setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        actionbar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getActivity() != null) {
            if (textSay != null) {
                textSay.stop();
                textSay.shutdown();
            }
            Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            Objects.requireNonNull(actionbar).setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            //getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        }
    }

    private void adviceSetter(View view) {
        TextView firstAdvice = view.findViewById(R.id.first_advice);
        TextView secondAdvice = view.findViewById(R.id.second_advice);
        TextView thirdAdvice = view.findViewById(R.id.third_advice);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

        if (sleepData != null) {
            if (sleepData.getStartSleep().getTime() < profile.getStartSleep().getTime()) {
                long range = profile.getStartSleep().getTime() - sleepData.getStartSleep().getTime() - 10800000;
                firstAdvice.setText("Fell asleep " + simpleDateFormat.format(range) + " early");

            }
            if (sleepData.getStartSleep().getTime() > profile.getStartSleep().getTime()) {
                long range = sleepData.getStartSleep().getTime() - profile.getStartSleep().getTime() - 10800000;
                if (simpleDateFormat.format(range).compareTo("00:00") == 0) {
                    firstAdvice.setText("You went to bed on time");
                } else {
                    firstAdvice.setText("Fell asleep " + simpleDateFormat.format(range) + " later");
                }
            }

            if (sleepData.getEndSleep().getTime() < profile.getEndSleep().getTime()) {
                long range = profile.getEndSleep().getTime() - sleepData.getEndSleep().getTime() - 10800000;
                secondAdvice.setText("Woke up " + simpleDateFormat.format(range) + " early");
            }
            if (sleepData.getEndSleep().getTime() > profile.getEndSleep().getTime()) {
                long range = sleepData.getEndSleep().getTime() - profile.getEndSleep().getTime() - 10800000;
                if (simpleDateFormat.format(range).compareTo("00:00") == 0) {
                    secondAdvice.setText("You woke up on time");
                } else {
                    secondAdvice.setText("Woke up " + simpleDateFormat.format(range) + " later");
                }
            }

            if (sleepData.getEndSleep().getTime() < profile.getEndSleep().getTime()) {
                long range = profile.getEndSleep().getTime() - sleepData.getEndSleep().getTime() - 10800000;
                secondAdvice.setText("Woke up " + simpleDateFormat.format(range) + " early");
            }
            if (sleepData.getEndSleep().getTime() > profile.getEndSleep().getTime()) {
                long range = sleepData.getEndSleep().getTime() - profile.getEndSleep().getTime() - 10800000;
                if (simpleDateFormat.format(range).compareTo("00:00") == 0) {
                    secondAdvice.setText("You woke up on time");
                } else {
                    secondAdvice.setText("Woke up " + simpleDateFormat.format(range) + " later");
                }
            }

            long sleepRangeReal = sleepLongDate(sleepData.getStartSleep(), sleepData.getEndSleep()).getTime();
            long sleepRangeMust = sleepLongDate(profile.getStartSleep(), profile.getEndSleep()).getTime();

            if (sleepRangeReal > sleepRangeMust) {
                long range = sleepRangeReal - sleepRangeMust - 10800000;
                if (simpleDateFormat.format(range).compareTo("00:00") == 0) {
                    thirdAdvice.setText("Sleep has not changed");
                } else {
                    thirdAdvice.setText("Sleep " + "increased by " + simpleDateFormat.format(range));
                }
            }
            if (sleepRangeReal < sleepRangeMust) {
                long range = sleepRangeMust - sleepRangeReal - 10800000;
                thirdAdvice.setText("Sleep " + "reduced by " + simpleDateFormat.format(range));
            }
            textToSpeech("Start sleeping in" + simpleDateFormat.format(sleepData.getStartSleep()), "End sleeping in" +simpleDateFormat.format(sleepData.getEndSleep()), firstAdvice.getText().toString(), secondAdvice.getText().toString(), thirdAdvice.getText().toString());
        }
    }


    private void viewSetter(View view) {
        TextView sleepLength = view.findViewById(R.id.sleep_cuantity_fragment);
        TextView sleepStart = view.findViewById(R.id.text_start_sleep);
        TextView sleepEnd = view.findViewById(R.id.end_sleep_text);

        sleepLength.setText(new SimpleDateFormat("HH:mm").format(sleepLongDate(sleepData.getStartSleep(), sleepData.getEndSleep())));
        sleepStart.setText(new SimpleDateFormat("HH:mm").format(sleepData.getStartSleep()));
        sleepEnd.setText(new SimpleDateFormat("HH:mm").format(sleepData.getEndSleep()));
    }

    private void initMonthChart(View view) {
        ColumnChartView chart = view.findViewById(R.id.column_day_sleep_chart);
        List<SubcolumnValue> values = new ArrayList<>();
        List<Column> columns = new ArrayList<>();

        TextView firstDay = view.findViewById(R.id.four_day_before_text);
        TextView endDay = view.findViewById(R.id.today_text);

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM");

        if (getContext() != null) {
            boolean a = true;
            for (int i = 4; i > 0; i--) {
                if (sleepDataList.size() >= 4) {
                    sleepData = sleepDataList.get(sleepDataList.size() - i);
                    values.add(new SubcolumnValue(sleepLongDate(sleepData.getStartSleep(), sleepData.getEndSleep()).getTime(), getResources().getColor(R.color.colorPrimary)));
                    if (a) {
                        firstDay.setText(String.valueOf(formatter.format(sleepData.getDate())));
                        endDay.setText(String.valueOf(formatter.format(sleepDataList.get(sleepDataList.size() - 1).getDate())));
                        a = false;
                    }
                } else {
                    if (sleepDataList.size() - i <= -1) {
                        values.add(new SubcolumnValue(10, getResources().getColor(R.color.colorBackgroundChart)));
                    } else {
                        sleepData = sleepDataList.get(sleepDataList.size() - i);
                        values.add(new SubcolumnValue(sleepLongDate(sleepData.getStartSleep(), sleepData.getEndSleep()).getTime(), getResources().getColor(R.color.colorPrimary)));
                        if (a) {
                            firstDay.setText(String.valueOf(formatter.format(sleepData.getDate())));
                            endDay.setText(String.valueOf(formatter.format(sleepDataList.get(sleepDataList.size() - 1).getDate())));
                            a = false;
                        }
                    }
                }
            }
        }


        columns.add(new Column(values));
        ColumnChartData data = new ColumnChartData(columns);

        chart.setColumnChartData(data);
        chart.setZoomEnabled(false);
        chart.setScrollEnabled(false);
        chart.setInteractive(false);
    }

    private Date sleepLongDate(Date startSleep, Date endSleep) {
        Date date = new Date();
        if (startSleep != null && endSleep != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            if (startSleep.getTime() >= endSleep.getTime()) {
                date.setTime(75600000 - startSleep.getTime() + endSleep.getTime());
                Log.d("<><><><>", "start " + startSleep.getTime() + "  " + simpleDateFormat.format(startSleep.getTime()) + "   end " + endSleep.getTime() + "  " + simpleDateFormat.format(endSleep.getTime()) + "   range " + (86400000 - startSleep.getTime() + endSleep.getTime()) + "  " + simpleDateFormat.format(86400000 - startSleep.getTime() + endSleep.getTime()));
            } else {
                if (startSleep.getTime() < endSleep.getTime()) {
                    date.setTime(endSleep.getTime() - startSleep.getTime() - 10800000);
                    Log.d("<><><><>", "start " + startSleep.getTime() + "  " + simpleDateFormat.format(startSleep.getTime()) + "   end " + endSleep.getTime() + "  " + simpleDateFormat.format(endSleep.getTime()) + "   range " + (endSleep.getTime() - startSleep.getTime()) + "  " + simpleDateFormat.format(endSleep.getTime() - startSleep.getTime()));
                }
            }
            return date;
        } else {
            return date;
        }
    }
}
