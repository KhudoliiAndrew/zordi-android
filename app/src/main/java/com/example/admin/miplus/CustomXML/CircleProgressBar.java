package com.example.admin.miplus.CustomXML;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.example.admin.miplus.R;


public class CircleProgressBar extends View {
    private static final String TAG = "CircleProgressBar";

    // Status
    private static final String INSTANCE_STATUS = "instance_status";
    private static final String STATUS_RADIAN = "status_radian";

    // Default dimension in dp/pt
    private static final float DEFAULT_GAP_BETWEEN_CIRCLE_AND_LINE = 60;
    private static final float DEFAULT_NUMBER_SIZE = 10;
    private static final float DEFAULT_LINE_WIDTH = 0.5f;
    private static final float DEFAULT_CIRCLE_BUTTON_RADIUS = 3;
    private static final float DEFAULT_CIRCLE_STROKE_WIDTH = 1;
    private static final float DEFAULT_TIMER_NUMBER_SIZE = 38;
    private static final float DEFAULT_TIMER_TEXT_SIZE = 18;

    // Default color
    private final int DEFAULT_CIRCLE_COLOR = getResources().getColor(R.color.colorPrimaryDark);
    private static final int DEFAULT_CIRCLE_BUTTON_COLOR = Color.TRANSPARENT; // end of sleep circle
    private final int DEFAULT_LINE_COLOR = getResources().getColor(R.color.colorPrimaryDark); //Background of circle
    private final int PROGRESS_LINE_COLOR = getResources().getColor(R.color.colorBackground); //Progress part of circle
    private final int DEFAULT_HIGHLIGHT_LINE_COLOR = getResources().getColor(R.color.colorPrimaryDark); //nothing
    private final int DEFAULT_NUMBER_COLOR = getResources().getColor(R.color.colorBackgroundCircleSleepPicker); //CircleBackground
    private final int DEFAULT_TIMER_NUMBER_COLOR = getResources().getColor(R.color.colorPrimaryDark); //mid number color
    private static final int DEFAULT_TIMER_COLON_COLOR = Color.TRANSPARENT; // start of sleep circle
    private final int DEFAULT_TIMER_TEXT_COLOR = getResources().getColor(R.color.colorPrimaryDark);

    private Paint mLinePaint;
    private Paint mProgressLinePaint;
    private Paint mCircleButtonPaint;
    private Paint mTimerColonPaint;

    // Dimension
    private float mGapBetweenCircleAndLine;
    private float mCircleButtonRadius;
    private float mCircleStrokeWidth;

    // Parameters
    private float mCx;
    private float mCy;
    private float mRadius;
    private float mCurrentRadian = 4;
    private float mCurrentRadian1;
    private boolean ismInCircleButton;

    public float getmCurrentRadian() {
        return mCurrentRadian;
    }

    public void setmCurrentRadian(float mCurrentRadian) {
        this.mCurrentRadian = mCurrentRadian;
    }

    public void setmCurrentRadian1(float mCurrentRadian1) {
        this.mCurrentRadian1 = mCurrentRadian1;
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBar(Context context) {
        this(context, null);
    }

    private void initialize() {
        Log.d(TAG, "initialize");
        // Set default dimension or read xml attributes
        mGapBetweenCircleAndLine = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_GAP_BETWEEN_CIRCLE_AND_LINE,
                getContext().getResources().getDisplayMetrics());
        float mNumberSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_NUMBER_SIZE, getContext().getResources()
                .getDisplayMetrics());
        float mLineWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_LINE_WIDTH, getContext().getResources()
                .getDisplayMetrics());
        mCircleButtonRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CIRCLE_BUTTON_RADIUS, getContext()
                .getResources().getDisplayMetrics());
        mCircleStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CIRCLE_STROKE_WIDTH, getContext()
                .getResources().getDisplayMetrics());
        float mTimerNumberSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TIMER_NUMBER_SIZE, getContext()
                .getResources().getDisplayMetrics());
        float mTimerTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TIMER_TEXT_SIZE, getContext()
                .getResources().getDisplayMetrics());

        // Set default color or read xml attributes
        // Color

        // Init all paints
        // Paint
        Paint mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCircleButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint mHighlightLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint mNumberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint mTimerNumberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint mTimerTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimerColonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // CirclePaint
        mCirclePaint.setColor(DEFAULT_CIRCLE_COLOR);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(mCircleStrokeWidth);

        // CircleButtonPaint
        mCircleButtonPaint.setColor(DEFAULT_CIRCLE_BUTTON_COLOR);
        mCircleButtonPaint.setAntiAlias(true);
        mCircleButtonPaint.setStyle(Paint.Style.FILL);

        // LinePaint
        mLinePaint.setColor(DEFAULT_LINE_COLOR);
        mProgressLinePaint.setColor(PROGRESS_LINE_COLOR);
        mLinePaint.setStrokeWidth(mCircleButtonRadius * 2 + 8);
        mProgressLinePaint.setStrokeWidth(mCircleButtonRadius * 2 + 8);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mProgressLinePaint.setStyle(Paint.Style.STROKE);

        // HighlightLinePaint
        mHighlightLinePaint.setColor(DEFAULT_HIGHLIGHT_LINE_COLOR);
        mHighlightLinePaint.setStrokeWidth(mLineWidth);

        // NumberPaint
        mNumberPaint.setColor(DEFAULT_NUMBER_COLOR);
        mNumberPaint.setTextSize(mNumberSize);
        mNumberPaint.setTextAlign(Paint.Align.CENTER);
        mNumberPaint.setStyle(Paint.Style.STROKE);
        mNumberPaint.setStrokeWidth(mCircleButtonRadius * 2 + 8);

        // TimerNumberPaint
        mTimerNumberPaint.setColor(DEFAULT_TIMER_NUMBER_COLOR);
        mTimerNumberPaint.setTextSize(mTimerNumberSize);
        mTimerNumberPaint.setTextAlign(Paint.Align.CENTER);

        // TimerTextPaint
        mTimerTextPaint.setColor(DEFAULT_TIMER_TEXT_COLOR);
        mTimerTextPaint.setTextSize(mTimerTextSize);
        mTimerTextPaint.setTextAlign(Paint.Align.CENTER);

        // TimerColonPaint
        mTimerColonPaint.setColor(DEFAULT_TIMER_COLON_COLOR);
        mTimerColonPaint.setTextAlign(Paint.Align.CENTER);
        mTimerColonPaint.setTextSize(mTimerNumberSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setmCurrentRadian1(4);
        canvas.save();
        canvas.rotate(-90, mCx, mCy);
        RectF rect = new RectF(mCx - (mRadius - mCircleStrokeWidth / 2 - mGapBetweenCircleAndLine), mCy - (mRadius - mCircleStrokeWidth / 2 - mGapBetweenCircleAndLine), mCx + (mRadius - mCircleStrokeWidth / 2 - mGapBetweenCircleAndLine), mCy + (mRadius - mCircleStrokeWidth / 2 - mGapBetweenCircleAndLine));

        float staticRadian = 2.28f;
        float staticRadian1 = 4;
        if (staticRadian1 > staticRadian) {
            canvas.drawArc(rect, (float) Math.toDegrees(staticRadian1), (float) Math.toDegrees(2 * (float) Math.PI) - (float) Math.toDegrees(staticRadian1) + (float) Math.toDegrees(staticRadian), false, mLinePaint);
        } else {
            canvas.drawArc(rect, (float) Math.toDegrees(staticRadian1), (float) Math.toDegrees(staticRadian) - (float) Math.toDegrees(staticRadian1), false, mLinePaint);
        }

        if (mCurrentRadian1 > mCurrentRadian) {
            canvas.drawArc(rect, (float) Math.toDegrees(mCurrentRadian1), (float) Math.toDegrees(2 * (float) Math.PI) - (float) Math.toDegrees(mCurrentRadian1) + (float) Math.toDegrees(mCurrentRadian), false, mProgressLinePaint);
        } else {
            canvas.drawArc(rect, (float) Math.toDegrees(mCurrentRadian1), (float) Math.toDegrees(mCurrentRadian) - (float) Math.toDegrees(mCurrentRadian1), false, mProgressLinePaint);
        }
        canvas.restore();
        canvas.save();
        canvas.rotate((float) Math.toDegrees(staticRadian), mCx, mCy);
        if(getmCurrentRadian() != 2.28f) canvas.drawCircle(mCx, getMeasuredHeight() / 2 - mRadius + mCircleStrokeWidth / 2 + mGapBetweenCircleAndLine, 0.01f, mLinePaint);

        canvas.restore();
        canvas.save();
        canvas.rotate((float) Math.toDegrees(staticRadian1), mCx, mCy);
        //canvas.drawCircle(mCx, getMeasuredHeight() / 2 - mRadius + mCircleStrokeWidth / 2 + mGapBetweenCircleAndLine, 0.01f, mLinePaint);
        canvas.drawCircle(mCx, getMeasuredHeight() / 2 - mRadius + mCircleStrokeWidth / 2 + mGapBetweenCircleAndLine, 0.01f, mProgressLinePaint);
        canvas.restore();
        canvas.save();
        canvas.rotate((float) Math.toDegrees(mCurrentRadian), mCx, mCy);
        canvas.drawCircle(mCx, getMeasuredHeight() / 2 - mRadius + mCircleStrokeWidth / 2 + mGapBetweenCircleAndLine, 0.01f, mProgressLinePaint);
        canvas.restore();
        canvas.save();


        if (ismInCircleButton) {
            canvas.rotate((float) Math.toDegrees(mCurrentRadian), mCx, mCy);
            canvas.drawCircle(mCx, getMeasuredHeight() / 2 - mRadius + mCircleStrokeWidth / 2 + mGapBetweenCircleAndLine
                    , mCircleButtonRadius, mCircleButtonPaint);
            canvas.restore();
            canvas.save();
            canvas.rotate((float) Math.toDegrees(mCurrentRadian1), mCx, mCy);
            canvas.drawCircle(mCx, getMeasuredHeight() / 2 - mRadius + mCircleStrokeWidth / 2 + mGapBetweenCircleAndLine, mCircleButtonRadius, mTimerColonPaint);
            canvas.restore();
            canvas.save();
        } else {
            canvas.rotate((float) Math.toDegrees(mCurrentRadian1), mCx, mCy);
            canvas.drawCircle(mCx, getMeasuredHeight() / 2 - mRadius + mCircleStrokeWidth / 2 + mGapBetweenCircleAndLine, mCircleButtonRadius, mTimerColonPaint);
            canvas.restore();
            canvas.save();
            canvas.rotate((float) Math.toDegrees(mCurrentRadian), mCx, mCy);
            canvas.drawCircle(mCx, getMeasuredHeight() / 2 - mRadius + mCircleStrokeWidth / 2 + mGapBetweenCircleAndLine, mCircleButtonRadius, mCircleButtonPaint);
            canvas.restore();
            canvas.save();
        }
        canvas.restore();
        canvas.save();
        canvas.restore();
        super.onDraw(canvas);
    }




    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        this.mCx = width / 2;
        this.mCy = height / 2;
        if (mGapBetweenCircleAndLine + mCircleStrokeWidth >= mCircleButtonRadius) {
            this.mRadius = width / 2 - mCircleStrokeWidth / 2;
        } else {
            this.mRadius = width / 2 - (mCircleButtonRadius - mGapBetweenCircleAndLine -
                    mCircleStrokeWidth / 2);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATUS, super.onSaveInstanceState());
        bundle.putFloat(STATUS_RADIAN, mCurrentRadian);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATUS));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void progressChange(int steps, int maxSteps) {
        setmCurrentRadian(((4.56f / maxSteps) * steps) + 4f);
        if (steps >= maxSteps) {
            setmCurrentRadian(2.28f);
        }
        invalidate();
    }
}
