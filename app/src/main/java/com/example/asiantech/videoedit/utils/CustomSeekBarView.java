package com.example.asiantech.videoedit.utils;

import android.content.Context;
import android.graphics.Bitmap;
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
        if (canvas != null) {
            mReferDrawView.getCanvasDraw(canvas);
        }
        DrawRightView(canvas);
        DrawLeftView(canvas);
    }

    private void drawBackground(Canvas canvas) {
        float mWidthMax = getWidth() - 20;
        setPaint(Color.GRAY, 20, Paint.Style.FILL);
        canvas.drawRect(mWidthImgStart, mHightImgStart, mWidthMax, mHightMax, mPaint);
    }

    public void drawThumb(Canvas canvas) {
        setPaint(Color.BLACK, 20, Paint.Style.STROKE);
        canvas.drawRect(getWidth() / 2 - 75, 10, getWidth() / 2 + 75, mHightMax, mPaint);
        invalidate();
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

    public void drawImage(Canvas mCanvas, Bitmap... bitmaps) {
        int width = getWidth() - 30;
        int w = width / bitmaps.length;
        for (int i = 0; i < bitmaps.length; i++) {
            mCanvas.drawBitmap(bitmaps[i], 10 + i * w, 10, null);
        }
    }

    public interface referDrawView {
        void getCanvasDraw(Canvas canvas);
    }

    public referDrawView mReferDrawView;

    public void setOnLick(referDrawView referDrawView) {
        this.mReferDrawView = referDrawView;
    }

    public void DrawLeftView(Canvas canvas) {
        setPaint(Color.BLUE, 20, Paint.Style.FILL);
        canvas.drawRect(10 + mCurrentWidth, 10, 50 + mCurrentWidth, 300, mPaint);
    }

    public void DrawRightView(Canvas canvas) {
        setPaint(Color.BLUE, 20, Paint.Style.FILL);
        canvas.drawRect(getWidth() - mCurrentWidth - 50, 10, getWidth() - mCurrentWidth, 300, mPaint);
    }


}
