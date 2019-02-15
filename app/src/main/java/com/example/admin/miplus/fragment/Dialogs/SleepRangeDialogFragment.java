package com.example.admin.miplus.fragment.Dialogs;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.miplus.CustomXML.CircleAlarmTimerView;
import com.example.admin.miplus.R;
import com.example.admin.miplus.fragment.ThirdFragment;

public class SleepRangeDialogFragment extends DialogFragment implements View.OnClickListener {
    View view;
    private int previsiourSleepStart;
    private int previsiourSleepEnd;
    private PushSleepTarget pushSleepTarget;
    private TextView textView1;
    private TextView textView2;
    private CircleAlarmTimerView circleAlarmTimerView;

    @SuppressLint("ValidFragment")
    public SleepRangeDialogFragment(int previsiourSleepStart, int previsiourSleepEnd, PushSleepTarget pushSleepTarget) {
        this.previsiourSleepStart = previsiourSleepStart;
        this.previsiourSleepEnd = previsiourSleepEnd;

        this.pushSleepTarget = pushSleepTarget;
    }

    public SleepRangeDialogFragment() {
    }

    public SleepRangeDialogFragment(int sleepTarget, ThirdFragment thirdFragment) {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        view = inflater.inflate(R.layout.sleep_dialog, null);
        view.findViewById(R.id.ok_button_picker).setOnClickListener(this);
        initView();
        return view;
    }

    @Override
    public void onClick(View v) {
        if (pushSleepTarget != null) {
            // pushSleepTarget.stepsTarget();
        }
        dismiss();
    }

    private void initView() {
       // textView1 = (TextView) view.findViewById(R.id.start);
       // textView2 = (TextView) view.findViewById(R.id.end);

        circleAlarmTimerView = (CircleAlarmTimerView) view.findViewById(R.id.circle_timer_picker);
        circleAlarmTimerView.setOnTimeChangedListener(new CircleAlarmTimerView.OnTimeChangedListener() {
            @Override
            public void start(String starting) {
              //  textView1.setText(starting);
            }

            @Override
            public void end(String ending) {
               // textView2.setText(ending);
            }
        });
    }

    public interface PushSleepTarget {
        void sleepTarget(int sleepTarget);

        void previsiourSleepStart(int sleepTarget);

        void previsiourSleepEnd(int sleepTarget);
    }
}
