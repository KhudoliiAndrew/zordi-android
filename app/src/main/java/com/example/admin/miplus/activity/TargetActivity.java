package com.example.admin.miplus.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;

import com.example.admin.miplus.R;
import com.example.admin.miplus.activity.activity_in_main.MainActivity;

import java.lang.reflect.Field;

public class TargetActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private NumberPicker numberPicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.target_steps_activity);
        numberPicker = (NumberPicker) findViewById(R.id.StepsPicker);

        initToolbar();
        isPicker();
        changeDividerColor(numberPicker, Color.parseColor("#00000000"));
    }
    private void initToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Target Activity_in_main");
        toolbar.setTitleTextColor(Color.BLACK);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
        toolbar.inflateMenu(R.menu.menu);

        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent Go_to_firstActivity = new Intent(TargetActivity.this, MainActivity.class);
                TargetActivity.this.startActivity(Go_to_firstActivity);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void isPicker(){
        numberPicker.setMaxValue(12000);
        numberPicker.setMinValue(1000);
        numberPicker.setWrapSelectorWheel(false);
    }

    public void onCLick_Steps_Cuantity_Button(View v){
        Intent Go_to_firstActivity = new Intent(TargetActivity.this, MainActivity.class);
        Go_to_firstActivity.putExtra("StepsQuantity", numberPicker.getValue());
        TargetActivity.this.startActivity(Go_to_firstActivity);

    }
    private void changeDividerColor(NumberPicker picker, int color) {
        try {
            Field mField = NumberPicker.class.getDeclaredField("mSelectionDivider");
            mField.setAccessible(true);
            ColorDrawable colorDrawable = new ColorDrawable(color);
            mField.set(picker, colorDrawable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
