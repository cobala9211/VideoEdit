package com.example.asiantech.videoedit.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Copyright © 2016 AsianTech inc.
 * Created by PhuongDN on 13/10/2016.
 */
public class CustomSeekBarView extends View {
    private float mWidthImgStart = 10;
    private float mHightImgStart = 10;
    private float mHightMax = 300;
    private Paint mPaint;
    public float mCurrentWidth = 0;

    public CustomSeekBarView(Context context) {
        super(context);
        mPaint = new Paint();
    }

    public CustomSeekBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
    }

    private void setPaint(int color, float stroke, Paint.Style style) {
        mPaint.setColor(color);
        mPaint.setStrokeWidth(stroke);
        mPaint.setStyle(style);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawThumb(canvas);
    }

    private void drawBackground(Canvas canvas) {
        float mWidthMax = getWidth() - 20;
        setPaint(Color.GRAY, 20, Paint.Style.FILL);
        canvas.drawRect(mWidthImgStart, mHightImgStart, mWidthMax, mHightMax, mPaint);
    }

    private void drawThumb(Canvas canvas) {
        setPaint(Color.BLACK, 20, Paint.Style.FILL);
        canvas.drawRect(mWidthImgStart + mCurrentWidth, mHightImgStart, 150 + mCurrentWidth, mHightMax, mPaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                mCurrentWidth = event.getX();
                invalidate();
                break;
        }
        return true;
    }

}
