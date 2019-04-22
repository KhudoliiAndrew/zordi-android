package com.example.admin.miplus.fragment.FirstWindow;

import android.os.Bundle;
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

import com.example.admin.miplus.R;
import com.example.admin.miplus.data_base.DataBaseRepository;
import com.example.admin.miplus.data_base.models.Profile;
import com.example.admin.miplus.data_base.models.SleepData;
import com.example.admin.miplus.data_base.models.StepsData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

public class SleepInformationFragment extends Fragment {

    final DataBaseRepository dataBaseRepository = new DataBaseRepository();
    private SleepData sleepData = new SleepData();
    private List<SleepData> sleepDataList = new ArrayList<SleepData>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Profile profile = new Profile();
    private String sleepTarget;

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
                                        sleepData.setStartSleep(setStartSleep(profile.getStartSleep()));
                                        sleepData.setEndSleep(setEndSleep(profile.getEndSleep()));
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
                                    sleepData.setStartSleep(setStartSleep(profile.getStartSleep()));
                                    sleepData.setEndSleep(setEndSleep(profile.getEndSleep()));
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
                            profile = task.getResult().toObject(Profile.class);
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
                                                        sleepData.setStartSleep(setStartSleep(profile.getStartSleep()));
                                                        sleepData.setEndSleep(setEndSleep(profile.getEndSleep()));
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
                                                    sleepData.setStartSleep(setStartSleep(profile.getStartSleep()));
                                                    sleepData.setEndSleep(setEndSleep(profile.getEndSleep()));
                                                    sleepData.setDate(date);
                                                    dataBaseRepository.setSleepData(sleepData);
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

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        actionbar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    private void adviceSetter(View view) {
        TextView firstAdvice = (TextView) view.findViewById(R.id.first_advice);
        TextView secondAdvice = (TextView) view.findViewById(R.id.second_advice);
      //  TextView thirdAdvice = (TextView) view.findViewById(R.id.third_advice);

        if(sleepData != null) {
           // if(profile.getStartSleep() != null && profile.getEndSleep() != null){
                String[] startSleep = sleepData.getStartSleep().split(":");
                String[] endSleep = sleepData.getEndSleep().split(":");

                String[] mustStartSleep = profile.getStartSleep().split(":");
                String[] mustEndSleep = profile.getEndSleep().split(":");

                if (Integer.parseInt(startSleep[0]) >= Integer.parseInt(mustStartSleep[0])) {
                    if (Integer.parseInt(startSleep[1]) >= Integer.parseInt(mustStartSleep[1])) {
                        if(Integer.parseInt(startSleep[0]) - Integer.parseInt(mustStartSleep[0]) > 9){
                            firstAdvice.setText("Fell asleep " + String.valueOf(Integer.parseInt(startSleep[0]) - Integer.parseInt(mustStartSleep[0])) + ":" + String.valueOf(Integer.parseInt(startSleep[1]) - Integer.parseInt(mustStartSleep[1])) + " later");
                        } else{
                            if(Integer.parseInt(startSleep[0]) - Integer.parseInt(mustStartSleep[0]) < 10){
                                firstAdvice.setText("Fell asleep " + "0" + String.valueOf(Integer.parseInt(startSleep[0]) - Integer.parseInt(mustStartSleep[0])) + ":" + String.valueOf(Integer.parseInt(startSleep[1]) - Integer.parseInt(mustStartSleep[1])) + " later");
                            }
                        }

                    } else {
                        if (Integer.parseInt(startSleep[1]) < Integer.parseInt(mustStartSleep[1])) {
                            firstAdvice.setText("Fell asleep " + String.valueOf(Integer.parseInt(startSleep[0]) - Integer.parseInt(mustStartSleep[0])) + ":" + String.valueOf(Integer.parseInt(startSleep[1]) + (60 - Integer.parseInt(mustStartSleep[1]))) + " later");
                        }
                    }
                } else {
                    if (Integer.parseInt(startSleep[0]) < Integer.parseInt(mustStartSleep[0])) {
                        if (Integer.parseInt(startSleep[1]) >= Integer.parseInt(mustStartSleep[1])) {
                            firstAdvice.setText("Fell asleep " + String.valueOf(24 - Integer.parseInt(mustStartSleep[0]) + Integer.parseInt(startSleep[0])) + ":" + String.valueOf(Integer.parseInt(startSleep[1]) - Integer.parseInt(mustStartSleep[1])) + " early");
                        } else {
                            if (Integer.parseInt(startSleep[1]) < Integer.parseInt(mustStartSleep[1])) {
                                firstAdvice.setText("Fell asleep " + String.valueOf(24 - Integer.parseInt(mustStartSleep[0]) + Integer.parseInt(startSleep[0])) + ":" + String.valueOf(Integer.parseInt(startSleep[1]) + (60 - Integer.parseInt(mustStartSleep[1]))) + " early");
                            }
                        }
                    }
                }

                if (Integer.parseInt(endSleep[0]) >= Integer.parseInt(mustEndSleep[0])) {
                    if (Integer.parseInt(endSleep[1]) >= Integer.parseInt(mustEndSleep[1])) {
                        if(Integer.parseInt(endSleep[0]) - Integer.parseInt(mustEndSleep[0]) > 9){
                            secondAdvice.setText("Woke up " + String.valueOf(24 - Integer.parseInt(mustEndSleep[0]) + Integer.parseInt(endSleep[0])) + ":" + String.valueOf(Integer.parseInt(endSleep[1]) - Integer.parseInt(mustEndSleep[1])) + " later");
                        } else{
                            if(Integer.parseInt(endSleep[0]) - Integer.parseInt(mustEndSleep[0]) < 10){
                                secondAdvice.setText("Woke up " + String.valueOf(24 - Integer.parseInt(mustEndSleep[0]) + Integer.parseInt(endSleep[0])) + ":" + String.valueOf(Integer.parseInt(endSleep[1]) - Integer.parseInt(mustEndSleep[1])) + " later");
                            }
                        }

                    } else {
                        if (Integer.parseInt(endSleep[1]) < Integer.parseInt(mustEndSleep[1])) {
                            secondAdvice.setText("Woke up " + String.valueOf(Integer.parseInt(endSleep[0]) - Integer.parseInt(mustEndSleep[0])) + ":" + String.valueOf(Integer.parseInt(endSleep[1]) + (60 - Integer.parseInt(mustEndSleep[1]))) + " early");
                        }
                    }
                } else {
                    if (Integer.parseInt(endSleep[0]) < Integer.parseInt(mustEndSleep[0])) {
                        if (Integer.parseInt(endSleep[1]) >= Integer.parseInt(mustEndSleep[1])) {
                            secondAdvice.setText("Woke up " + String.valueOf(Integer.parseInt(mustEndSleep[0]) - Integer.parseInt(endSleep[0])) + ":" + String.valueOf(Integer.parseInt(endSleep[1]) +(60 - Integer.parseInt(mustEndSleep[1]))) + " early");
                        } else {
                            if (Integer.parseInt(endSleep[1]) < Integer.parseInt(mustEndSleep[1])) {
                                secondAdvice.setText("Woke up " + "0" + String.valueOf(Integer.parseInt(mustEndSleep[0]) -Integer.parseInt(endSleep[0])) + ":" + String.valueOf(Integer.parseInt(endSleep[1]) + (60 - Integer.parseInt(mustEndSleep[1]))) + " early");
                            }
                        }
                    }
                }
            }
      //  }

      //  firstAdvice.setText("Fell asleep " + "1h 2m" + " early/later");
      //  secondAdvice.setText("Woke up " + "1h 2m" + " early/later");
       /* thirdAdvice.setText("Sleep " + "increased by " + "1h");*/
    }

    private void viewSetter(View view) {
        TextView sleepLength = (TextView) view.findViewById(R.id.sleep_cuantity_fragment);
        TextView sleepStart = (TextView) view.findViewById(R.id.text_start_sleep);
        TextView sleepEnd = (TextView) view.findViewById(R.id.end_sleep_text);

        sleepLength.setText(sleepLongString(sleepData.getStartSleep(), sleepData.getEndSleep()));
        sleepStart.setText(sleepData.getStartSleep());
        sleepEnd.setText(sleepData.getEndSleep());
    }

    private void initMonthChart(View view) {
        ColumnChartView chart = (ColumnChartView) view.findViewById(R.id.column_day_sleep_chart);
        List<SubcolumnValue> values = new ArrayList<SubcolumnValue>();
        List<Column> columns = new ArrayList<Column>();

        Date date = new Date();
        TextView firstDay = (TextView) view.findViewById(R.id.four_day_before_text);
        TextView endDay = (TextView) view.findViewById(R.id.today_text);
        TextView noHistory = (TextView) view.findViewById(R.id.no_steps_history);

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM");


        boolean a = true;
        for (int i = 4; i > 0; i--) {
            if (sleepDataList.size() >= 4) {
                sleepData = sleepDataList.get(sleepDataList.size() - i);
                values.add(new SubcolumnValue(sleepLong(sleepData.getStartSleep(), sleepData.getEndSleep()), getResources().getColor(R.color.colorPrimary)));
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
                    values.add(new SubcolumnValue(sleepLong(sleepData.getStartSleep(), sleepData.getEndSleep()), getResources().getColor(R.color.colorPrimary)));
                    if (a) {
                        firstDay.setText(String.valueOf(formatter.format(sleepData.getDate())));
                        endDay.setText(String.valueOf(formatter.format(sleepDataList.get(sleepDataList.size() - 1).getDate())));
                        a = false;
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

    private String setStartSleep(String startSleep) {
        String realStartSleep;
        String[] partStartSleep = startSleep.split(":");

        int startHour = Integer.parseInt(partStartSleep[0]);
        int whenStartHour = (int) ((Math.random() * ((startHour + 2) - (startHour - 1) + 1) + (startHour - 1)));
        int whenStartMinute = (int) ((Math.random() * (60 - 0 + 1) + 0));

        if (whenStartHour > 23) {
            if (whenStartMinute > 9) {
                realStartSleep = "0" + String.valueOf(whenStartHour - 24) + ":" + String.valueOf(whenStartMinute);
            } else {
                realStartSleep = "0" + String.valueOf(whenStartHour - 24) + ":0" + String.valueOf(whenStartMinute);
            }
        } else {
            if (whenStartHour > 10) {
                if (whenStartMinute > 9) {
                    realStartSleep = String.valueOf(whenStartHour) + ":" + String.valueOf(whenStartMinute);
                } else {
                    realStartSleep = String.valueOf(whenStartHour) + ":0" + String.valueOf(whenStartMinute);
                }
            } else {
                if (whenStartMinute > 9) {
                    realStartSleep = "0" + String.valueOf(whenStartHour) + ":" + String.valueOf(whenStartMinute);
                } else {
                    realStartSleep = "0" + String.valueOf(whenStartHour) + ":0" + String.valueOf(whenStartMinute);
                }
            }
        }
        return realStartSleep;
    }

    private String setEndSleep(String endSleep) {
        String[] partEndSleep = endSleep.split(":");
        String realEndSleep;
        int endHour = Integer.parseInt(partEndSleep[0]);

        int whenEndHour = (int) ((Math.random() * ((endHour + 2) - (endHour - 1) + 1) + (endHour - 1)));
        int whenEndMinute = (int) ((Math.random() * (60 - 0 + 1) + 0));
        if (whenEndHour > 23) {
            if (whenEndMinute > 9) {
                realEndSleep = "0" + String.valueOf(whenEndHour - 24) + ":" + String.valueOf(whenEndMinute);
            } else {
                realEndSleep = "0" + String.valueOf(whenEndHour - 24) + ":0" + String.valueOf(whenEndMinute);
            }
        } else {
            if (whenEndHour > 10) {
                if (whenEndMinute > 9) {
                    realEndSleep = String.valueOf(whenEndHour) + ":" + String.valueOf(whenEndMinute);
                } else {
                    realEndSleep = String.valueOf(whenEndHour) + ":0" + String.valueOf(whenEndMinute);
                }
            } else {
                if (whenEndMinute > 9) {
                    realEndSleep = "0" + String.valueOf(whenEndHour) + ":" + String.valueOf(whenEndMinute);
                } else {
                    realEndSleep = "0" + String.valueOf(whenEndHour) + ":0" + String.valueOf(whenEndMinute);
                }
            }
        }
        return realEndSleep;
    }

    private Integer sleepLong(String startSleep, String endSleep) {
        String[] partStartSleep = startSleep.split(":");
        String[] partEndSleep = endSleep.split(":");
        if (Integer.parseInt(partEndSleep[0]) >= Integer.parseInt(partStartSleep[0])) {
            int differenceTime = Integer.parseInt(partEndSleep[0]) - Integer.parseInt(partStartSleep[0]);
            if (differenceTime < 10) {
                sleepTarget = "0" + String.valueOf(differenceTime);
            } else {
                sleepTarget = String.valueOf(differenceTime);
            }
        } else {
            int differenceTime = 24 - (Integer.parseInt(partStartSleep[0]) - Integer.parseInt(partEndSleep[0]));
            if (differenceTime < 10) {
                sleepTarget = "0" + String.valueOf(differenceTime);
            } else {
                sleepTarget = String.valueOf(differenceTime);
            }
        }

        if (Integer.parseInt(partEndSleep[1]) >= Integer.parseInt(partStartSleep[1])) {
            int differenceTime = Integer.parseInt(partEndSleep[1]) - Integer.parseInt(partStartSleep[1]);
            if (differenceTime < 10) {
                sleepTarget = sleepTarget + ":0" + differenceTime;
            } else {
                sleepTarget = sleepTarget + ":" + differenceTime;
            }
        } else {
            int differenceTime = 60 - (Integer.parseInt(partStartSleep[1]) + 1 - Integer.parseInt(partEndSleep[1]));
            if (differenceTime < 10) {
                if (Integer.parseInt(sleepTarget) - 1 < 10) {
                    sleepTarget = "0" + String.valueOf(Integer.parseInt(sleepTarget) - 1) + ":0" + differenceTime;
                } else {
                    sleepTarget = String.valueOf(Integer.parseInt(sleepTarget) - 1) + ":0" + differenceTime;
                }
            } else {
                if (Integer.parseInt(sleepTarget) - 1 != -1) {
                    if (Integer.parseInt(sleepTarget) - 1 < 10) {
                        sleepTarget = "0" + String.valueOf(Integer.parseInt(sleepTarget) - 1) + ":" + differenceTime;
                    } else {
                        sleepTarget = String.valueOf(Integer.parseInt(sleepTarget) - 1) + ":" + differenceTime;
                    }
                } else {
                    sleepTarget = "23:" + differenceTime;
                }

            }
        }

        String[] s = sleepTarget.split(":");
        return Integer.parseInt(s[0]);
    }

    private String sleepLongString(String startSleep, String endSleep) {
        String[] partStartSleep = startSleep.split(":");
        String[] partEndSleep = endSleep.split(":");
        if (Integer.parseInt(partEndSleep[0]) >= Integer.parseInt(partStartSleep[0])) {
            int differenceTime = Integer.parseInt(partEndSleep[0]) - Integer.parseInt(partStartSleep[0]);
            if (differenceTime < 10) {
                sleepTarget = "0" + String.valueOf(differenceTime);
            } else {
                sleepTarget = String.valueOf(differenceTime);
            }
        } else {
            int differenceTime = 24 - (Integer.parseInt(partStartSleep[0]) - Integer.parseInt(partEndSleep[0]));
            if (differenceTime < 10) {
                sleepTarget = "0" + String.valueOf(differenceTime);
            } else {
                sleepTarget = String.valueOf(differenceTime);
            }
        }

        if (Integer.parseInt(partEndSleep[1]) >= Integer.parseInt(partStartSleep[1])) {
            int differenceTime = Integer.parseInt(partEndSleep[1]) - Integer.parseInt(partStartSleep[1]);
            if (differenceTime < 10) {
                sleepTarget = sleepTarget + ":0" + differenceTime;
            } else {
                sleepTarget = sleepTarget + ":" + differenceTime;
            }
        } else {
            int differenceTime = 60 - (Integer.parseInt(partStartSleep[1]) + 1 - Integer.parseInt(partEndSleep[1]));
            if (differenceTime < 10) {
                if (Integer.parseInt(sleepTarget) - 1 < 10) {
                    sleepTarget = "0" + String.valueOf(Integer.parseInt(sleepTarget) - 1) + ":0" + differenceTime;
                } else {
                    sleepTarget = String.valueOf(Integer.parseInt(sleepTarget) - 1) + ":0" + differenceTime;
                }
            } else {
                if (Integer.parseInt(sleepTarget) - 1 != -1) {
                    if (Integer.parseInt(sleepTarget) - 1 < 10) {
                        sleepTarget = "0" + String.valueOf(Integer.parseInt(sleepTarget) - 1) + ":" + differenceTime;
                    } else {
                        sleepTarget = String.valueOf(Integer.parseInt(sleepTarget) - 1) + ":" + differenceTime;
                    }
                } else {
                    sleepTarget = "23:" + differenceTime;
                }

            }
        }
        return sleepTarget;
    }
}
