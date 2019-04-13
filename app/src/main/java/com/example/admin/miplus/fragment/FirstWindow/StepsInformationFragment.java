package com.example.admin.miplus.fragment.FirstWindow;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.admin.miplus.R;
import com.example.admin.miplus.data_base.DataBaseRepository;
import com.example.admin.miplus.data_base.models.StepsData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class StepsInformationFragment extends Fragment {

    final DataBaseRepository dataBaseRepository = new DataBaseRepository();
    private StepsData stepsData = new StepsData();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.steps_information_fragment, container, false);

       /* if (dataBaseRepository.getProfile() != null) {
            stepsData = dataBaseRepository.getProfile();
        } else {
            dataBaseRepository.getProfileTask()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            stepsData = task.getResult().toObject(StepsData.class);
                        }
                    });
        }*/
        initToolbar();
        initChart(view);

       /* dataBaseRepository.getGeoDataTask()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getResult() != null) {
                            geoDataList = task.getResult().toObjects(GeoData.class);
                            for(int i = 0; i < geoDataList.size(); i++){
                                geoData = geoDataList.get(i);
                                LatLng latLng = new LatLng(geoData.getLatitude(), geoData.getLongitude());
                                points.add(latLng);*/
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

    private void  initChart(View view){
        List<PointValue> values = new ArrayList<PointValue>();
        values.add(new PointValue(0.2f, 0.4f));
        values.add(new PointValue(1, 2.5f));
        values.add(new PointValue(2, 1));
        values.add(new PointValue(3, 4));
        values.add(new PointValue(4, 4));
        values.add(new PointValue(5, 4));
        values.add(new PointValue(6, 20));

        //In most cased you can call data model methods in builder-pattern-like manner.
        Line line = new Line(values).setColor(getResources().getColor(R.color.colorPrimary)).setCubic(true).setHasPoints(false);
        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        LineChartView chart = new LineChartView(getContext());
        chart.setLineChartData(data);

        LineChartView chartView = (LineChartView) view.findViewById(R.id.chart);
        chartView.setLineChartData(data);
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
