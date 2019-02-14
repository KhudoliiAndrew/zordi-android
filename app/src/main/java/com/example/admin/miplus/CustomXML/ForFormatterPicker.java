package com.example.admin.miplus.CustomXML;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.NumberPicker;

public class ForFormatterPicker extends NumberPicker {

    public ForFormatterPicker(Context context) {
        super(context);
    }

    public ForFormatterPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ForFormatterPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        return false;
    }

    @Override
    public boolean performLongClick() {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return false;
    }
}