package com.example.admin.miplus.fragment.FirstWindow;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.admin.miplus.R;
import com.example.admin.miplus.data_base.DataBaseRepository;
import com.example.admin.miplus.data_base.models.Profile;
import com.example.admin.miplus.data_base.models.StepsData;
import com.example.admin.miplus.fragment.FirstFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
    private int steps = 0;
    private Profile profile = new Profile();

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
                            stepsDataList = task.getResult().toObjects(StepsData.class);
                            initMonthChart(view);
                        } else {
                            initMonthChart(view);
                        }
                    }
                });

        initToolbar();
        return view;
    }

    private void viewSetter(View view) {
        TextView stepsText = (TextView) view.findViewById(R.id.steps_cuantity_fragment);
        TextView callText = (TextView) view.findViewById(R.id.text_calories);
        TextView distanceText = (TextView) view.findViewById(R.id.traveled_distance_text);

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

    private void initChart(View view) {
        Date date = new Date();
        TextView startActivity = (TextView) view.findViewById(R.id.start_activity_text);
        TextView endActivity = (TextView) view.findViewById(R.id.end_activity_text);

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        endActivity.setText(String.valueOf(formatter.format(stepsDataList.get(stepsDataList.size() - 1).getDate().getTime())));
        List<PointValue> values = new ArrayList<PointValue>();

        boolean a = true;
        for (int i = 0; i < stepsDataList.size(); i++) {
            stepsData = stepsDataList.get(i);
            if(stepsData.getDate().getDate() == date.getDate() ) {
                values.add(new PointValue(i, stepsData.getSteps()));
                if (a) {
                    startActivity.setText(String.valueOf(formatter.format(stepsDataList.get(i).getDate().getTime())));
                    a = false;
                }
            }
        }

        Line line = new Line(values).setColor(getResources().getColor(R.color.colorPrimary)).setCubic(false).setHasPoints(false);
        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        LineChartView chart = new LineChartView(getContext());
        chart.setLineChartData(data);

        LineChartView chartView = (LineChartView) view.findViewById(R.id.chart);
        chartView.setLineChartData(data);
        chartView.setZoomEnabled(false);
        chartView.setScrollEnabled(false);
    }

    private void initMonthChart(View view) {
        ColumnChartView chart = (ColumnChartView) view.findViewById(R.id.column_chart);
        List<SubcolumnValue> values = new ArrayList<SubcolumnValue>();
        List<Column> columns = new ArrayList<Column>();

        Date date = new Date();
        TextView firstDay = (TextView) view.findViewById(R.id.four_day_before_text);
        TextView endDay = (TextView) view.findViewById(R.id.today_text);

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM");
        endDay.setText(String.valueOf(formatter.format(stepsDataList.get(stepsDataList.size() - 1).getDate())));

        boolean a = true;
        for (int i = 0; i < stepsDataList.size(); i++) {
            if (i < 4) {
                monthStepsData = stepsDataList.get(stepsDataList.size() - 4 + i);
                Log.d(">>>><<<<", "initMonthChart: " + monthStepsData.getDate());
                values.add(new SubcolumnValue(monthStepsData.getSteps(), getResources().getColor(R.color.colorPrimary)));
                if (a) {
                    firstDay.setText(String.valueOf(formatter.format(monthStepsData.getDate())));
                    a = false;
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
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}
