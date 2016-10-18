package com.example.asiantech.videoedit.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.asiantech.videoedit.MainActivity;
import com.example.asiantech.videoedit.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


import static android.view.MotionEvent.ACTION_MOVE;

/**
 * Copyright © 2016 AsianTech inc.
 * Created by PhuongDN on 15/10/2016.
 */
public class CustomSeekBarView extends View {

    //max and current value
    private int maxValue = 100;
    private int currentValue = 0;
    private int barHeight;

    //width thumb
    private int widthThumb;
    private Paint barThumbPaint;
    private Paint barBackgroundPaint;
    private Paint barFramePaint;
    private Context mContext;

    private Bitmap[] listBitmaps;

    private boolean isTouch = false;

    private Paint barThumbCyclerPaint;
    private Paint barThumbTextPaint;
    private Paint barThumbPointPaint;
    private Paint barThumbProcessPaint;

    // draw ruler
    private Paint barRulerTimePaint;
    private Paint barRulerPointPaint;
    private Paint barRulerTextPaint;

    private boolean isEdit = false;
    private boolean isDrawThumb = false;

    private Paint barEditStartPaint;
    private Paint barEditEndPaint;

    private Points pointStart;
    private Points pointEnd;
    private int lengthRuler;

    public CustomSeekBarView(Context context) {
        super(context);
        init(context, null);
        this.mContext = context;
    }

    public CustomSeekBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        this.mContext = context;

    }

    /**
     * This method setvalue bar
     *
     * @param newValue
     */
    public void setValue(int newValue) {
        if (newValue < 0) {
            currentValue = 0;
        } else if (newValue > maxValue) {
            currentValue = maxValue;
        } else {
            currentValue = newValue;
        }
        invalidate();
    }

    /**
     * This method init value bar
     *
     * @param context context
     * @param attrs   AttributeSet
     */
    private void init(Context context, AttributeSet attrs) {
        setSaveEnabled(true);
        //read xml attributes
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomValueBarView, 0, 0);
        barHeight = typedArray.getDimensionPixelSize(R.styleable.CustomValueBarView_barHeight, 0);

        //thumb
        widthThumb = typedArray.getDimensionPixelSize(R.styleable.CustomValueBarView_widthThumb, 0);

        //recycle
        typedArray.recycle();

        // setbar
        barThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barBackgroundPaint.setColor(Color.BLACK);
        barFramePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setColor(Color.BLACK);
        paint.setTextSize(80);

        //set thumb
        barThumbCyclerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barThumbCyclerPaint.setColor(Color.RED);
        barThumbCyclerPaint.setStyle(Paint.Style.FILL);

        barThumbTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barThumbTextPaint.setTextSize(14);
        barThumbTextPaint.setColor(Color.WHITE);
        barThumbTextPaint.setTextAlign(Paint.Align.CENTER);

        barThumbPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barThumbPointPaint.setColor(Color.RED);
        barThumbPointPaint.setStyle(Paint.Style.FILL);
        barThumbPointPaint.setStrokeWidth(5);

        barThumbProcessPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barThumbProcessPaint.setColor(Color.RED);
        barThumbProcessPaint.setStyle(Paint.Style.FILL);

        barRulerTimePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barRulerTimePaint.setColor(Color.WHITE);
        barRulerTimePaint.setStrokeWidth(2);

        barRulerPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barRulerPointPaint.setColor(Color.WHITE);
        barRulerTimePaint.setStrokeWidth(8);

        barRulerTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barRulerTextPaint.setColor(Color.WHITE);
        barRulerTextPaint.setTextSize(24);
        barRulerTextPaint.setTextAlign(Paint.Align.CENTER);

        //edit video
        barEditStartPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barEditStartPaint.setColor(Color.RED);
        barEditStartPaint.setStrokeWidth(5);

        barEditEndPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barEditEndPaint.setColor(Color.BLUE);
        barEditEndPaint.setStrokeWidth(5);

        pointStart = new Points(0, 0);
        pointEnd = new Points(670, 0);
    }


    @Override
    protected void onDraw(final Canvas canvas) {
        drawBar(canvas);
        drawRulerTime(canvas);
        drawImage(canvas, listBitmaps);
        if (!isEdit) {
            drawThumb(canvas);
            invalidate();
        } else {
            drawPointStart(canvas);
            drawPointEnd(canvas);
        }
    }

    private void drawBar(Canvas canvas) {
        //draw background
        canvas.drawRect(0, 0, getWidth(), barHeight + 70, barBackgroundPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case ACTION_MOVE:
                currentValue = (int) event.getX() - (widthThumb);
                invalidate();
                isTouch = true;
                if (!stateSave) {
                    pointStart.setX(currentValue);
                    invalidate();
                } else {
                    pointEnd.setX(currentValue);
                    invalidate();
                }
                break;
            default:
                isTouch = false;
                break;
        }

        return true;
    }

    public void drawImage(Canvas canvas, Bitmap... bitmaps) {
        int width = getWidth() - 10;
        int w = width / bitmaps.length;
        for (int i = 0; i < bitmaps.length; i++) {
            canvas.drawBitmap(bitmaps[i], i * w, 70,
                    barFramePaint);
        }
    }

    /**
     * @param canvas draw ruler
     */
    private void drawRulerTime(Canvas canvas) {
        lengthRuler = getWidth() - widthThumb;
        //  canvas.drawLine(0, 60, getWidth(), 60, barRulerTimePaint);
        canvas.drawLine(widthThumb / 2, 60, widthThumb / 2, 20, barRulerPointPaint);
        canvas.drawText("00:00", widthThumb / 2, 20, barRulerTextPaint);
        canvas.drawLine(widthThumb / 2, 60, lengthRuler, 60, barRulerPointPaint);
        canvas.drawText(milliSecondsToTimer(timeDuration), lengthRuler, 20, barRulerTextPaint);

        for (int i = widthThumb / 2; i < lengthRuler; i++) {
            if (i % 60 == 0) {
                canvas.drawLine(widthThumb / 2 + i, 60, widthThumb / 2 + i, 30, barRulerPointPaint);
            }
        }
    }

    public void drawThumb(Canvas canvas) {
        barThumbPaint.setColor(Color.WHITE);
        barThumbPaint.setStyle(Paint.Style.STROKE);
        barThumbPaint.setStrokeWidth(10);

        if (!isTouch) {
            canvas.drawRect(widthThumb / 2, barHeight + 50, widthThumb / 2 + count, barHeight + 70, barThumbProcessPaint);
            canvas.drawRect(count, 65, widthThumb + count, barHeight + 55, barThumbPaint);
            canvas.drawCircle(widthThumb / 2 + count, barHeight - barHeight / 8 + 45, widthThumb / 4, barThumbCyclerPaint);
            canvas.drawLine(widthThumb / 2 + count, barHeight - barHeight / 8 + 45, widthThumb / 2 + count, barHeight + 70, barThumbPointPaint);
            canvas.drawText(milliSecondsToTimer(timeCurrent), widthThumb / 2 + count, barHeight - barHeight / 8 + 45, barThumbTextPaint);
            //draw line time
            canvas.drawLine(widthThumb / 2 + count, 60, widthThumb / 2 + count, 0, barThumbPointPaint);
            invalidate();
        } else {
            canvas.drawCircle(widthThumb / 2 + currentValue, barHeight - barHeight / 8 + 45, widthThumb / 4, barThumbCyclerPaint);
            canvas.drawLine(widthThumb / 2 + currentValue, barHeight - barHeight / 8 + 45, widthThumb / 2 + currentValue, barHeight + 70, barThumbPointPaint);
            canvas.drawText(milliSecondsToTimer(timeCurrent), widthThumb / 2 + currentValue, barHeight - barHeight / 8 + 45, barThumbTextPaint);
            Log.d("tag", "time curent " + timeCurrent);
            canvas.drawLine(widthThumb / 2 + currentValue, 60, widthThumb / 2 + currentValue, 0, barThumbPointPaint);
            invalidate();

        }
    }


    public void setListBitmapBit(Bitmap... bitmaps) {
        listBitmaps = new Bitmap[bitmaps.length];
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((MainActivity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int mWidthScreen = displaymetrics.widthPixels;
        for (int i = 0; i < bitmaps.length; i++) {
            listBitmaps[i] = reSizeImageView(bitmaps[i], mWidthScreen, barHeight);
        }
    }

    public Bitmap reSizeImageView(Bitmap bitmap, int widthImg, int heightImg) {
        int widthBitmap = bitmap.getWidth();
        int heightBitmap = bitmap.getHeight();
        float scaleWidth = widthImg / widthBitmap;
        float scaleHeight = heightImg / heightBitmap;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmapReSize = Bitmap.createBitmap(bitmap, 0, 0, widthBitmap - 20, heightBitmap - 20, matrix, false);
        return bitmapReSize;
    }

    private static final long DELAY_TIME_MILLIS = 1000L;
    private boolean updateView = false;
    private UpdateViewRunnable updateViewRunnable = new UpdateViewRunnable();
    private Paint paint = new Paint();
    private float count;
    private long timeCurrent = 0;

    private class UpdateViewRunnable implements Runnable {
        public void run() {
            if (isEdit) {
                count = 0;
                timeCurrent = 0;
            }
            if (isTouch) {
                count = currentValue;
                updateView = true;
                timeCurrent = ((currentValue * timeDuration) / (lengthRuler));
                if (count > lengthRuler) {
                    updateView = false;
                }
            } else {
                if (count <= lengthRuler && timeCurrent <= timeDuration) {
                    count = count + (lengthRuler * 1000 / (float) timeDuration);
                    timeCurrent += DELAY_TIME_MILLIS;
                    invalidate();
                } else {
                    updateView = false;
                }

            }

            if (updateView)

            {
                postDelayed(this, DELAY_TIME_MILLIS);
                Log.d("tag", "update view" + updateView);
            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateView = true;
        postDelayed(updateViewRunnable, DELAY_TIME_MILLIS);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        updateView = false;
    }

    private long timeDuration;

    public void setTimeDuration(long time) {
        this.timeDuration = time;
    }

    private String milliSecondsToTimer(long milliseconds) {
        return TimeUnit.MILLISECONDS.toHours(milliseconds) > 0 ? (new SimpleDateFormat("HH:mm:ss",
                Locale.getDefault())).format(new Date(milliseconds)) : (new SimpleDateFormat("mm:ss",
                Locale.getDefault())).format(new Date(milliseconds));
    }

    public void setIsEdit(boolean edit) {
        isEdit = edit;
    }

    public void setIsDrawThumb(boolean drawThumb) {
        isDrawThumb = drawThumb;
    }

    private void drawPointStart(Canvas canvas) {
        canvas.drawLine(widthThumb / 2 + pointStart.getX(), 0, widthThumb / 2 + pointStart.getX(), barHeight + 70, barEditStartPaint);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_edit);
        canvas.drawBitmap(bitmap, widthThumb / 2 + pointStart.getX() - 26, (barHeight + 70) / 2, null);
        invalidate();
    }

    private void drawPointEnd(Canvas canvas) {
        canvas.drawLine(pointEnd.getX(), 0, pointEnd.getX(), barHeight + 70, barEditEndPaint);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_edit);
        canvas.drawBitmap(bitmap, pointEnd.getX() - 26, (barHeight + 70) / 2, null);
        invalidate();
    }

    private static class Points {
        private int x, y;

        Points(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int getX() {
            return x;
        }

        void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

    }

    private boolean stateSave = false;

    public void setSaveState(boolean state) {
        this.stateSave = state;
    }
}
