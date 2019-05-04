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
import com.example.admin.miplus.data_base.models.StepsData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

public class StepsInformationFragment extends Fragment {

    final DataBaseRepository dataBaseRepository = new DataBaseRepository();
    private StepsData stepsData = new StepsData();
    private StepsData monthStepsData = new StepsData();
    private List<StepsData> stepsDataList = new ArrayList<StepsData>();
    private List<StepsData> monthStepsDataList = new ArrayList<StepsData>();
    private int steps = 0;
    private Profile profile = new Profile();

    boolean hasLabel = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.steps_information_fragment, container, false);

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
                                initChart(view);
                            } else {
                                steps = stepsData.getSteps();
                                viewSetter(view);
                                initChart(view);
                            }
                        } else {
                            stepsData.setDefaultInstance();
                            steps = stepsData.getSteps();
                            initChart(view);
                        }
                    }

                });

        dataBaseRepository.getStepsDataByDay()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            final Date date = new Date();
                            monthStepsDataList = task.getResult().toObjects(StepsData.class);
                            initMonthChart(view);
                        } else {
                            TextView firstDay = (TextView) view.findViewById(R.id.four_day_before_text);
                            TextView endDay = (TextView) view.findViewById(R.id.today_text);
                            TextView noHistory = (TextView) view.findViewById(R.id.no_steps_history);
                            ColumnChartView chart = (ColumnChartView) view.findViewById(R.id.column_chart);

                            chart.setZoomEnabled(false);
                            chart.setScrollEnabled(false);
                            chart.setInteractive(false);
                            noHistory.setText("No history of your steps");
                            firstDay.setText("");
                            endDay.setText("");
                        }
                    }
                });

        initToolbar();
        return view;
    }

    private void viewSetter(View view) {
        TextView stepsText = (TextView) view.findViewById(R.id.steps_cuantity_fragment);
        TextView callText = (TextView) view.findViewById(R.id.text_calories);
        TextView distanceText = (TextView) view.findViewById(R.id.end_sleep__text);

        if (stepsData != null && profile != null) {
            stepsText.setText(String.valueOf(steps));
            String formattedDoubleCal = new DecimalFormat("#0.00").format(0.035f * profile.getWeight() + (1.38889f * 1.38889f / profile.getHeight()) * 0.029f * profile.getWeight());
            callText.setText(formattedDoubleCal);
            float distance = ((((profile.getHeight() * 0.01f) / 4) + 0.37f) * stepsData.getSteps()) * 0.001f;
            String formattedDouble = new DecimalFormat("#0.00").format(distance);
            distanceText.setText(formattedDouble);
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        actionbar.setDisplayShowHomeEnabled(true);
    }

    private void initChart( View view) {
        Date date = new Date();
        TextView startActivity = (TextView) view.findViewById(R.id.start_activity_text);
        TextView endActivity = (TextView) view.findViewById(R.id.end_activity_text);
        TextView noSteps = (TextView) view.findViewById(R.id.no_steps_today);

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        if(stepsDataList.size() != 0) {
            endActivity.setText(String.valueOf(formatter.format(stepsDataList.get(stepsDataList.size() - 1).getDate().getTime())));
        } else{
            noSteps.setText("No progress of your steps");
            startActivity.setText("");
            endActivity.setText("");
        }

        List<PointValue> values = new ArrayList<PointValue>();

        if (getContext() != null) {
            boolean a = true;
            for (int i = 0; i < stepsDataList.size(); i++) {
                stepsData = stepsDataList.get(i);
                if (stepsData.getDate().getDate() == date.getDate()) {
                    values.add(new PointValue(i, stepsData.getSteps()));

                    final Line line = new Line(values).setColor(getResources().getColor(R.color.colorPrimary)).setCubic(true).setHasPoints(hasLabel).setHasLabels(hasLabel).setHasLabelsOnlyForSelected(true);
                    List<Line> lines = new ArrayList<Line>();
                    lines.add(line);

                    LineChartData data = new LineChartData();
                    data.setLines(lines);


                    LineChartView chartView = (LineChartView) view.findViewById(R.id.chart);
                    chartView.setLineChartData(data);


                    chartView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hasLabel = !hasLabel;
                            line.setHasLabels(hasLabel);
                            line.setHasPoints(hasLabel);
                        }
                    });

                    if (a) {
                        if (stepsDataList.size() != 0) {
                            startActivity.setText(String.valueOf(formatter.format(stepsDataList.get(i).getDate().getTime())));
                        } else {
                            noSteps.setText("No progress of your steps");
                            startActivity.setText("");
                            endActivity.setText("");
                        }
                        a = false;
                    }
                }
            }
        }

    }

    private void initMonthChart(View view) {
        ColumnChartView chart = (ColumnChartView) view.findViewById(R.id.column_chart);
        List<SubcolumnValue> values = new ArrayList<SubcolumnValue>();
        List<Column> columns = new ArrayList<Column>();

        Date date = new Date();
        TextView firstDay = (TextView) view.findViewById(R.id.four_day_before_text);
        TextView endDay = (TextView) view.findViewById(R.id.today_text);
        TextView noHistory = (TextView) view.findViewById(R.id.no_steps_history);

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM");

        if (getContext() != null) {
        boolean a = true;
        for (int i = 4; i > 0; i--) {
            if (monthStepsDataList.size() >= 4) {
                monthStepsData = monthStepsDataList.get(monthStepsDataList.size() - i);
                values.add(new SubcolumnValue(monthStepsData.getSteps(), getResources().getColor(R.color.colorPrimary)));
                if (a) {
                    firstDay.setText(String.valueOf(formatter.format(monthStepsData.getDate())));
                    endDay.setText(String.valueOf(formatter.format(monthStepsDataList.get(monthStepsDataList.size() - 1).getDate())));
                    a = false;
                }
            } else {
                if (monthStepsDataList.size() - i <= -1) {
                    //values.add(new SubcolumnValue(10, getResources().getColor(R.color.colorBackgroundChart)));
                } else {
                    monthStepsData = monthStepsDataList.get(monthStepsDataList.size() - i);
                    values.add(new SubcolumnValue(monthStepsData.getSteps(), getResources().getColor(R.color.colorPrimary)));
                    if (a) {
                        firstDay.setText(String.valueOf(formatter.format(monthStepsData.getDate())));
                        endDay.setText(String.valueOf(formatter.format(monthStepsDataList.get(monthStepsDataList.size() - 1).getDate())));
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

    @Override
    public void onDetach() {
        super.onDetach();
        if(getActivity() != null) {
            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            //getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        }
    }
}
