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


import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

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
    private Paint mBarBackgroundPaint;
    private Paint mBarFramePaint;
    private Context mContext;

    //arrays bitmaps
    private Bitmap[] mListBitmaps;

    // touch
    private boolean mIsTouch = false;


    private Paint mBarThumbCyclePaint;
    private Paint mBarThumbTextPaint;
    private Paint mBarThumbPointPaint;
    private Paint mBarThumbProcessPaint;

    // draw ruler
    private Paint mBarRulerTimePaint;
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
        mBarBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
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

        mBarThumbProcessPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarThumbProcessPaint.setColor(Color.RED);
        mBarThumbProcessPaint.setStyle(Paint.Style.FILL);

        mBarRulerTimePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarRulerTimePaint.setColor(Color.WHITE);
        mBarRulerTimePaint.setStrokeWidth(2);

        mBarRulerPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarRulerPointPaint.setColor(Color.WHITE);
        mBarRulerTimePaint.setStrokeWidth(8);

        mBarRulerTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarRulerTextPaint.setColor(Color.WHITE);
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
        mPointStart = new Points(mWidthThumb / 2);
        mPointEnd = new Points(mWidthScreen - (mWidthThumb / 2));
    }


    @Override
    protected void onDraw(final Canvas canvas) {
        drawBar(canvas);
        drawRulerTime(canvas);
        drawImage(canvas, mListBitmaps);
        if (!mIsEdit) {
            drawThumb(canvas);
            invalidate();
        } else {
            drawEditCurrent(canvas);
            drawPointStart(canvas);
            drawPointEnd(canvas);
        }
    }

    private void drawBar(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), mBarHeight, mBarBackgroundPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case ACTION_MOVE:
                mCurrentPosition = (int) event.getX() - (mWidthThumb);
                invalidate();
                mIsTouch = true;
                if (mIsEdit) {
                    if (mCurrentPosition >= (mWidthThumb / 2)
                            && mCurrentPosition < (mPointEnd.getX() - (mWidthThumb * 2))) {
                        mPointStart.setX(mCurrentPosition);
                        Log.d("tag", "aa");
                        invalidate();
                    } else if (mCurrentPosition > (mPointStart.getX() + mWidthThumb * 2)) {
                        mPointEnd.setX(mCurrentPosition);
                        Log.d("tag", "bb");
                        invalidate();
                    }
                    return true;
                }
                break;
            case ACTION_UP:
                mCurrentPosition = (int) event.getX() - (mWidthThumb);
                invalidate();
                mIsTouch = true;
                if (mIsEdit) {
                    if (mCurrentPosition >= (mWidthThumb / 2)
                            && mCurrentPosition < (mPointEnd.getX() - (mWidthThumb * 2))) {
                        mPointStart.setX(mCurrentPosition);
                        Log.d("tag", "aa");
                        invalidate();
                    } else if (mCurrentPosition > (mPointStart.getX() + mWidthThumb * 2)) {
                        mPointEnd.setX(mCurrentPosition);
                        Log.d("tag", "bb");
                        invalidate();
                    }

                }
                return true;
            default:
                mIsTouch = false;
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
        mLengthRuler = mWidthScreen - mWidthThumb;
        canvas.drawLine(mWidthThumb / 2, 0, mWidthThumb / 2, (mBarHeight / 3), mBarRulerPointPaint);
        canvas.drawText("00:00", mWidthThumb / 2, 20, mBarRulerTextPaint);
        //  canvas.drawLine(mWidthThumb / 2, 60, mLengthRuler, 60, mBarRulerPointPaint);
        canvas.drawText(milliSecondsToTimer(mTimeDuration), mLengthRuler, 20, mBarRulerTextPaint);
        for (int i = mWidthThumb / 2; i < mLengthRuler; i++) {
            if (i % 60 == 0) {
                canvas.drawLine(mWidthThumb / 2 + i, 60, mWidthThumb / 2 + i, 30, mBarRulerPointPaint);
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
            canvas.drawCircle(mWidthThumb / 2 + mCount, mBarHeight - mBarHeight / 8 + 45, mWidthThumb / 4, mBarThumbCyclePaint);
            canvas.drawLine(mWidthThumb / 2 + mCount, 0, mWidthThumb / 2 + mCount, mBarHeight + 51, mBarThumbPointPaint);
            canvas.drawText(milliSecondsToTimer(mTimeCurrent), mWidthThumb / 2 + mCount, mBarHeight - mBarHeight / 8 + 45, mBarThumbTextPaint);
            invalidate();
        } else {
            canvas.drawCircle(mWidthThumb / 2 + mCurrentPosition, mBarHeight - mBarHeight / 8 + 45, mWidthThumb / 4, mBarThumbCyclePaint);
            canvas.drawLine(mWidthThumb / 2 + mCurrentPosition, 0, mWidthThumb / 2 + mCurrentPosition, mBarHeight + 51, mBarThumbPointPaint);
            canvas.drawText(milliSecondsToTimer(mTimeCurrent), mWidthThumb / 2 + mCurrentPosition, mBarHeight - mBarHeight / 8 + 45, mBarThumbTextPaint);
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
        Bitmap bitmapReSize = Bitmap.createBitmap(bitmap, 0, 0, widthBitmap - 20, heightBitmap - 20, matrix, false);
        return bitmapReSize;
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

    private class UpdateViewRunnable implements Runnable {
        public void run() {

            if (mIsTouch) {
                mCount = mCurrentPosition;
                //   mPointStart.setX((int) mCount);
                mIsUpdateView = true;
                mTimeCurrent = ((mCurrentPosition * mTimeDuration) / (mLengthRuler));
                if (mCount > mLengthRuler) {
                    mIsUpdateView = false;
                }
            } else {
                if (mCount <= mLengthRuler && mTimeCurrent <= mTimeDuration) {
                    mCount = mCount + (mLengthRuler * 1000 / (float) mTimeDuration);
                    //    mPointStart.setX((int) mCount);
                    mTimeCurrent += DELAY_TIME_MILLIS;
                    invalidate();
                } else {
                    mIsUpdateView = false;
                }
            }

            if (mIsEdit) {
                //  mPointStart.setX((int) mCount);
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
}
