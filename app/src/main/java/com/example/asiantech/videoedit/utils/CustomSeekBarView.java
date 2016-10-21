package com.example.asiantech.videoedit.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

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

    private int mCurrentPosition = 0;
    private int mBarHeight;

    //width thumb
    private int mWidthThumb;
    private Paint mBarThumbPaint;
    private Paint mBarFramePaint;
    private Context mContext;

    //arrays bitmaps
    private Bitmap[] mListBitmaps;

    // touch
    private boolean mIsTouch = false;


    private Paint mBarThumbCyclePaint;
    private Paint mBarThumbTextPaint;
    private Paint mBarThumbPointPaint;

    private Paint mBarRulerPointPaint;
    private Paint mBarRulerTextPaint;

    // edit
    private boolean mIsEdit = false;

    private Paint mBarEditStartPaint;
    private Paint mBarEditEndPaint;

    // draw point start and point end to cut video
    private Points mPointStart;
    private Points mPointEnd;

    // length ruler
    private int mLengthRuler;

    //width screen
    private int mWidthScreen;

    // time duration
    private long mTimeDuration;
    private boolean mIsEditHard = false;


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
     * This method init value bar
     *
     * @param context context
     * @param attrs   AttributeSet
     */
    private void init(Context context, AttributeSet attrs) {
        setSaveEnabled(true);
        //read xml attributes
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomValueBarView, 0, 0);
        mBarHeight = typedArray.getDimensionPixelSize(R.styleable.CustomValueBarView_barHeight, 0);

        //thumb
        mWidthThumb = typedArray.getDimensionPixelSize(R.styleable.CustomValueBarView_widthThumb, 0);

        //recycle
        typedArray.recycle();

        // setbar
        mBarThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint mBarBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarBackgroundPaint.setColor(Color.BLACK);
        mBarFramePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setColor(Color.BLACK);
        paint.setTextSize(80);

        //set thumb
        mBarThumbCyclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarThumbCyclePaint.setColor(Color.RED);
        mBarThumbCyclePaint.setStyle(Paint.Style.FILL);

        mBarThumbTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarThumbTextPaint.setTextSize(14);
        mBarThumbTextPaint.setColor(Color.WHITE);
        mBarThumbTextPaint.setTextAlign(Paint.Align.CENTER);

        mBarThumbPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarThumbPointPaint.setColor(Color.RED);
        mBarThumbPointPaint.setStyle(Paint.Style.FILL);
        mBarThumbPointPaint.setStrokeWidth(5);

        Paint mBarThumbProcessPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarThumbProcessPaint.setColor(Color.RED);
        mBarThumbProcessPaint.setStyle(Paint.Style.FILL);

        Paint mBarRulerTimePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarRulerTimePaint.setColor(Color.WHITE);
        mBarRulerTimePaint.setStrokeWidth(2);

        mBarRulerPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarRulerPointPaint.setColor(Color.BLACK);
        mBarRulerTimePaint.setStrokeWidth(10);

        mBarRulerTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarRulerTextPaint.setColor(Color.BLACK);
        mBarRulerTextPaint.setTextSize(24);
        mBarRulerTextPaint.setTextAlign(Paint.Align.CENTER);

        //edit video
        mBarEditStartPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarEditStartPaint.setColor(Color.RED);
        mBarEditStartPaint.setStrokeWidth(5);

        mBarEditEndPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarEditEndPaint.setColor(Color.BLUE);
        mBarEditEndPaint.setStrokeWidth(5);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mWidthScreen = displaymetrics.widthPixels;
        mPointStart = new Points(mWidthThumb / 4);
        mPointEnd = new Points(mWidthScreen - (mWidthThumb / 4));
    }


    @Override
    protected void onDraw(final Canvas canvas) {
        drawRulerTime(canvas);
        drawImage(canvas, mListBitmaps);
        if (mIsEditHard) {
            mPointStart.setX(mWidthThumb / 4 + dem);
            mPointEnd.setX(mPointStart.getX() + mTextNumber);
            drawEditCurrent(canvas);
            drawPointStart(canvas);
            drawPointEnd(canvas);
            invalidate();
        } else if (!mIsEdit) {
            drawThumb(canvas);
            invalidate();
        } else {
            drawEditCurrent(canvas);
            drawPointStart(canvas);
            drawPointEnd(canvas);
            invalidate();
        }
    }

    private int dem = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case ACTION_MOVE:
                mCurrentPosition = (int) event.getX() - (mWidthThumb / 2);
                invalidate();
                mIsTouch = true;
                if (mCurrentPosition < 0) {
                    mCurrentPosition = 0;
                } else if (mCurrentPosition > mLengthRuler - (mWidthThumb / 2)) {
                    mCurrentPosition = mLengthRuler;
                }
                if (mIsEdit) {
                    if (mCurrentPosition < (mPointEnd.getX() - (mWidthThumb * 2))) {
                        if (mCurrentPosition <= (mWidthThumb / 4)) {
                            mCurrentPosition = mWidthThumb / 4;
                        }
                        mPointStart.setX(mCurrentPosition);
                        invalidate();
                    } else if (mCurrentPosition > (mPointStart.getX() + mWidthThumb * 2)) {
                        if (mCurrentPosition >= mLengthRuler - (mWidthThumb / 2)) {
                            mCurrentPosition = mWidthScreen - (mWidthThumb / 4);
                        }
                        mPointEnd.setX(mCurrentPosition);
                        invalidate();
                    }
                    return true;
                }
                if (mIsEditHard) {
                    if (mCurrentPosition <= mWidthThumb / 4) {
                        dem = 0;
                        Log.d("tag", "a");
                    } else if (mCurrentPosition > 0 && mCurrentPosition <= (mLengthRuler - mTextNumber)) {
                        dem = mCurrentPosition;
                        Log.d("tag", "b");
                        invalidate();
                    } else {
                        dem = mLengthRuler - mTextNumber;
                        Log.d("tag", "c");
                    }


                }
                break;
        }

        return true;
    }

    /**
     * @param canvas  canvas object
     * @param bitmaps return each bitmap object to draw
     */
    public void drawImage(Canvas canvas, Bitmap... bitmaps) {
        int widthImage = mWidthScreen / bitmaps.length;
        for (int i = 0; i < bitmaps.length; i++) {
            canvas.drawBitmap(bitmaps[i], i * widthImage, mBarHeight / 3,
                    mBarFramePaint);
        }
    }

    /**
     * @param canvas draw ruler
     */
    private void drawRulerTime(Canvas canvas) {
        mLengthRuler = mWidthScreen - (mWidthThumb / 2);
        canvas.drawLine(mWidthThumb / 4, 60, mWidthThumb / 4, 30, mBarRulerPointPaint);
        canvas.drawText("00:00", mWidthThumb / 4, 30, mBarRulerTextPaint);
        canvas.drawText(milliSecondsToTimer(mTimeDuration), mLengthRuler, 30, mBarRulerTextPaint);
        for (int i = mWidthThumb / 4; i < mLengthRuler; i++) {
            if (i % 60 == 0) {
                canvas.drawLine(mWidthThumb / 4 + i, 60, mWidthThumb / 4 + i, 30, mBarRulerPointPaint);
            }
        }
    }

    /**
     * @param canvas canvas object
     *               draw thumb
     */
    public void drawThumb(Canvas canvas) {
        mBarThumbPaint.setColor(Color.WHITE);
        mBarThumbPaint.setStyle(Paint.Style.STROKE);
        mBarThumbPaint.setStrokeWidth(10);

        if (!mIsTouch) {
            canvas.drawCircle(mWidthThumb / 4 + mCount, mBarHeight - mBarHeight / 8 + 45, mWidthThumb / 4, mBarThumbCyclePaint);
            canvas.drawLine(mWidthThumb / 4 + mCount, 0, mWidthThumb / 4 + mCount, mBarHeight, mBarThumbPointPaint);
            canvas.drawText(milliSecondsToTimer(mTimeCurrent), mWidthThumb / 4 + mCount, mBarHeight - mBarHeight / 8 + 45, mBarThumbTextPaint);
            invalidate();
        } else {
            canvas.drawCircle(mWidthThumb / 4 + mCurrentPosition, mBarHeight - mBarHeight / 8 + 45, mWidthThumb / 4, mBarThumbCyclePaint);
            canvas.drawLine(mWidthThumb / 4 + mCurrentPosition, 0, mWidthThumb / 4 + mCurrentPosition, mBarHeight, mBarThumbPointPaint);
            canvas.drawText(milliSecondsToTimer(mTimeCurrent), mWidthThumb / 4 + mCurrentPosition, mBarHeight - mBarHeight / 8 + 45, mBarThumbTextPaint);
            invalidate();

        }

    }

    /**
     * @param bitmaps return array bitmaps
     */
    public void setListBitmapBit(Bitmap... bitmaps) {
        mListBitmaps = new Bitmap[bitmaps.length];
        for (int i = 0; i < bitmaps.length; i++) {
            mListBitmaps[i] = reSizeImageView(bitmaps[i], mWidthScreen, mBarHeight);
        }
    }

    /**
     * @param bitmap    return bitmap object
     * @param widthImg  return width image bitmap
     * @param heightImg return height image bitmap
     * @return bitmap resized
     */
    public Bitmap reSizeImageView(Bitmap bitmap, int widthImg, int heightImg) {
        int widthBitmap = bitmap.getWidth();
        int heightBitmap = bitmap.getHeight();
        float scaleWidth = widthImg / widthBitmap;
        float scaleHeight = heightImg / heightBitmap;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, widthBitmap - 20, heightBitmap - 20, matrix, false);
    }

    /**
     * class update view
     */
    private static final long DELAY_TIME_MILLIS = 1000L;
    // check status update view
    private boolean mIsUpdateView = false;
    private UpdateViewRunnable updateViewRunnable = new UpdateViewRunnable();
    private Paint paint = new Paint();
    private float mCount = 0;
    private long mTimeCurrent = 0;

    public void setIsEditHard(boolean isEditHard) {
        this.mIsEditHard = isEditHard;
    }

    private class UpdateViewRunnable implements Runnable {
        public void run() {
            if (mIsEditHard) {
                mIsUpdateView = false;
                return;
            }
            if (mIsTouch) {
                mCount = mCurrentPosition;
                mIsUpdateView = true;
                mTimeCurrent = ((mCurrentPosition * mTimeDuration) / (mLengthRuler));
                mIsTouch = false;
            } else {
                if (mCount <= mLengthRuler && mTimeCurrent <= mTimeDuration) {
                    mCount = mCount + (mLengthRuler * 1000 / (float) mTimeDuration);
                    mTimeCurrent += DELAY_TIME_MILLIS;
                    invalidate();
                }
            }

            if (mIsEdit) {
                mIsUpdateView = false;
                return;

            }

            if (mIsUpdateView) {
                postDelayed(this, DELAY_TIME_MILLIS);
            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mIsUpdateView = true;
        postDelayed(updateViewRunnable, DELAY_TIME_MILLIS);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsUpdateView = false;
    }

    /**
     * @param time return time duration of video
     */
    public void setTimeDuration(long time) {
        this.mTimeDuration = time;
    }

    /**
     * @param milliseconds return time current of video
     * @return
     */
    private String milliSecondsToTimer(long milliseconds) {
        return TimeUnit.MILLISECONDS.toHours(milliseconds) > 0 ? (new SimpleDateFormat("HH:mm:ss",
                Locale.getDefault())).format(new Date(milliseconds)) : (new SimpleDateFormat("mm:ss",
                Locale.getDefault())).format(new Date(milliseconds));
    }

    /**
     * @param edit variable to check edit or not
     */
    public void setIsEdit(boolean edit) {
        mIsEdit = edit;
    }

    /**
     * @param canvas canvas object
     *               draw point start to drag and cut
     */
    private void drawPointStart(Canvas canvas) {
        canvas.drawLine(mPointStart.getX(), 0, mPointStart.getX(), mBarHeight + 51, mBarEditStartPaint);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_edit);
        canvas.drawBitmap(bitmap, mPointStart.getX() - (bitmap.getWidth() / 2), (mBarHeight) / 2, null);
        invalidate();
    }

    /**
     * @param canvas canvas object
     *               draw point start to drag and cut
     */
    private void drawPointEnd(Canvas canvas) {
        canvas.drawLine(mPointEnd.getX(), 0, mPointEnd.getX(), mBarHeight + 51, mBarEditEndPaint);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_edit);
        canvas.drawBitmap(bitmap, mPointEnd.getX() - (bitmap.getWidth() / 2), (mBarHeight) / 2, null);
        invalidate();
    }

    /**
     * class Points object
     */
    private static class Points {
        private int x;

        Points(int x) {
            this.x = x;
        }

        int getX() {
            return x;
        }

        void setX(int x) {
            this.x = x;
        }

    }

    /**
     * @param canvas canvas object
     *               draw area where you choose to cut video
     */
    private void drawEditCurrent(Canvas canvas) {
        mBarEditStartPaint.setColor(ContextCompat.getColor(mContext, R.color.colorThumb));
        mBarEditStartPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));
        canvas.drawRect(mPointStart.getX(), (mBarHeight / 3), mPointEnd.getX(), mBarHeight + 51, mBarEditStartPaint);
        invalidate();
    }

    private int mTextNumber = 0;

    public void setTextNumber(int number) {
        this.mTextNumber = number;
    }
}
