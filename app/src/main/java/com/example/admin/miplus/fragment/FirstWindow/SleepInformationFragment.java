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
import android.widget.TextView;

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

public class SleepInformationFragment extends Fragment {

    final DataBaseRepository dataBaseRepository = new DataBaseRepository();
    private StepsData stepsData = new StepsData();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.sleep_information_fragment, container, false);

        adviceSetter(view);
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

    private void adviceSetter(View view){
        TextView firstAdvice = (TextView) view.findViewById(R.id.first_advice);
        TextView secondAdvice = (TextView) view.findViewById(R.id.second_advice);
        TextView thirdAdvice = (TextView) view.findViewById(R.id.third_advice);

        firstAdvice.setText("Fell asleep " + "1h 2m" + " early/later");
        secondAdvice.setText("Woke up " + "1h 2m" + " early/later");
        thirdAdvice.setText("Sleep " + "increased by " + "1h");
    }
}
