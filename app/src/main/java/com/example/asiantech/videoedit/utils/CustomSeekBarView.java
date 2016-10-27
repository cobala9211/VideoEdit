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
import com.example.asiantech.videoedit.listeners.ISendTime;

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

    /*_____________________ Draw Bar Thumb__________________*/
    // draw circle
    private Paint mBarThumbCirclePaint;
    // draw text inside thumb circle
    private Paint mBarThumbTextPaint;
    // draw thumb point
    private Paint mBarThumbPointPaint;
    // draw image
    private Paint mBarFramePaint;

    /*_____________________ Draw Bar Ruler ---------------*/
    // draw ruler
    private Paint mBarRulerPointPaint;
    // draw text on ruler
    private Paint mBarRulerTextPaint;

    /* _____________________ Draw Point Start And Point End To Cut Video--------------------*/
    //draw point start
    private Paint mBarEditStartPaint;
    // draw point end
    private Paint mBarEditEndPaint;
    // declare two points ( point start and point end )
    // point start
    private Points mPointStart;
    // point end
    private Points mPointEnd;

    //___________ Flags____________________//
    // test action edit or not
    public boolean mIsCut = false;
    // test action touch or not
    public boolean mIsTouch = false;
    // test action cut video with period of time
    public boolean mIsCutWithTimes = false;

    /*_________________________Variables_____________________*/
    // current position
    private int mCurrentPosition = 0;
    // height of bar
    private int mBarHeight;
    //width of thumb
    private int mWidthThumb;
    private Context mContext;
    //arrays bitmap images
    private Bitmap[] mListBitmaps;
    // length ruler
    private int mLengthRuler;
    //width of screen
    private int mWidthScreen;
    // time duration of video
    private long mTimeDuration;
    // create new paint object
    private Paint paint = new Paint();

    /*_______________________Listener___________________*/
    private ISendTime mISendTime;
    /*_______________________Cut Video With A Period of Time _____________*/
    // get distance from edit text
    private int mDistancesToCut = 0;
    // get distance when touch and drag
    private int mCountDistancesToCut = 0;
    // get times to cut video
    private long mTimesToCut = 0;

    // constructor
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
        // get bar height
        mBarHeight = typedArray.getDimensionPixelSize(R.styleable.CustomValueBarView_barHeight, 0);
        //get width thumb
        mWidthThumb = typedArray.getDimensionPixelSize(R.styleable.CustomValueBarView_widthThumb, 0);
        //recycle
        typedArray.recycle();
        //set up thumb circle
        mBarThumbCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarThumbCirclePaint.setColor(Color.RED);
        mBarThumbCirclePaint.setStyle(Paint.Style.FILL);
        //set up text in thumb circle
        mBarThumbTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarThumbTextPaint.setTextSize(14);
        mBarThumbTextPaint.setColor(Color.WHITE);
        mBarThumbTextPaint.setTextAlign(Paint.Align.CENTER);
        // set up thumb point ( red line thumb)
        mBarThumbPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarThumbPointPaint.setColor(Color.RED);
        mBarThumbPointPaint.setStyle(Paint.Style.FILL);
        mBarThumbPointPaint.setStrokeWidth(5);
        //set up draw image
        mBarFramePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarFramePaint.setColor(Color.BLACK);
        //set up draw ruler
        mBarRulerPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarRulerPointPaint.setColor(Color.BLACK);
        mBarRulerPointPaint.setStrokeWidth(3);
        // set up draw text on bar ruler
        mBarRulerTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarRulerTextPaint.setColor(Color.BLACK);
        mBarRulerTextPaint.setTextSize(24);
        mBarRulerTextPaint.setTextAlign(Paint.Align.CENTER);
        //draw point start to draw
        mBarEditStartPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarEditStartPaint.setColor(Color.RED);
        mBarEditStartPaint.setStrokeWidth(5);
        // draw point end to draw
        mBarEditEndPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarEditEndPaint.setColor(Color.BLUE);
        mBarEditEndPaint.setStrokeWidth(5);
        // get point start and point end  coordinate to drag and cut video
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mWidthScreen = displaymetrics.widthPixels;
        mPointStart = new Points(mWidthThumb / 4);
        mPointEnd = new Points(mWidthScreen - (mWidthThumb / 4));
        paint.setColor(Color.BLACK);
        paint.setTextSize(80);
    }


    /**
     * @param iSendTime interfaces
     */
    public void setSendTimeListener(ISendTime iSendTime) {
        mISendTime = iSendTime;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        drawRulerTime(canvas);
        drawImage(canvas, mListBitmaps);
        if (mIsCutWithTimes) {
            mDistancesToCut = (int) ((mTimesToCut * mLengthRuler) / (int) (mTimeDuration / 1000));
            mPointStart.setX(mWidthThumb / 4 + mCountDistancesToCut);
            mPointEnd.setX(mPointStart.getX() + mDistancesToCut);
            drawAreaTimeCut(canvas);
            drawPointStart(canvas);
            drawPointEnd(canvas);
            invalidate();
        } else if (!mIsCut) {
            drawThumb(canvas);
            invalidate();
        } else {
            drawAreaTimeCut(canvas);
            drawPointStart(canvas);
            drawPointEnd(canvas);
            invalidate();
        }
    }

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

                mISendTime.
                        timeToCut((int) ((mCurrentPosition * (mTimeDuration / 1000)) / (mLengthRuler)), (int) (mTimeDuration / 1000));
                if (mIsCut) {
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
                    mISendTime.timeToCut((int) (((mPointStart.getX()) * (mTimeDuration / 1000)) / (mLengthRuler)),
                            (int) (((mPointEnd.getX()) * (mTimeDuration / 1000)) / (mLengthRuler)));
                } else if (mIsCutWithTimes) {
                    if (mCurrentPosition <= mWidthThumb / 4) {
                        mCountDistancesToCut = 0;
                    } else if (mCurrentPosition > 0 && mCurrentPosition <= (mLengthRuler - mDistancesToCut)) {
                        mCountDistancesToCut = mCurrentPosition;
                        invalidate();
                    } else {
                        mCountDistancesToCut = mLengthRuler - mDistancesToCut;
                    }
                    mISendTime.timeToCut((int) (((mCountDistancesToCut) * (mTimeDuration / 1000)) / (mLengthRuler)),
                            (int) (((mCountDistancesToCut +
                                    mDistancesToCut) * (mTimeDuration / 1000)) / (mLengthRuler)));
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
        int widthImage = mLengthRuler / bitmaps.length;
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
        if (!mIsTouch) {
            canvas.drawCircle(mWidthThumb / 4 + mCount, mBarHeight - mBarHeight / 8 + 45, mWidthThumb / 4, mBarThumbCirclePaint);
            canvas.drawLine(mWidthThumb / 4 + mCount, 0, mWidthThumb / 4 + mCount, mBarHeight, mBarThumbPointPaint);
            canvas.drawText(milliSecondsToTimer(mTimeCurrent), mWidthThumb / 4 + mCount, mBarHeight - mBarHeight / 8 + 45, mBarThumbTextPaint);
            invalidate();
        } else {
            canvas.drawCircle(mWidthThumb / 4 + mCurrentPosition, mBarHeight - mBarHeight / 8 + 45, mWidthThumb / 4, mBarThumbCirclePaint);
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
     * @param isEditHard return true or false
     */
    public void setIsCutWithTime(boolean isEditHard) {
        this.mIsCutWithTimes = isEditHard;
    }

    /**
     * @param isUpdateView return true of false
     */
    public void setIsUpdateView(boolean isUpdateView) {
        this.mIsUpdateView = isUpdateView;
    }

    /**
     * @param edit variable to check cut action  or not
     */
    public void setIsCut(boolean edit) {
        mIsCut = edit;
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
     * @param timesToCut time to cut video
     */
    public void setTimesToCut(long timesToCut) {
        this.mTimesToCut = timesToCut;
    }

    /**
     * class update view
     */
    private static final long DELAY_TIME_MILLIS = 1000L;
    // check status update view
    public boolean mIsUpdateView = false;
    private UpdateViewRunnable updateViewRunnable = new UpdateViewRunnable();
    // count of thumb when touch or not
    private float mCount = 0;
    // get time current of current pos
    public long mTimeCurrent;
    // check status listen event play , seek to from video view
    public boolean mIsPLay = false;
    public boolean mIsSeekTo = false;

    private class UpdateViewRunnable implements Runnable {
        public void run() {
            //  if (mIsCutWithTimes) {
            //  mIsUpdateView = false;
            // return;
            // }
            if (mIsTouch) {
                mCount = mCurrentPosition;
                mIsUpdateView = true;
                mTimeCurrent = ((mCurrentPosition * mTimeDuration) / (mLengthRuler));
                mIsTouch = false;
            }
            // in this case , is not touch
            else {
                Log.d("tag", "update view" + mIsUpdateView);
                if (mIsPLay) {
                    if (mTimeCurrent <= mTimeDuration) {
                        mTimeCurrent += DELAY_TIME_MILLIS;
                        mCount = mCount + (mLengthRuler * 1000 / (float) mTimeDuration);
                        Log.d("tag", "seek bar is working ");
                        invalidate();
                    }
                }
               else if (mIsSeekTo) {
                    mCount = ((mTimeCurrent) * mLengthRuler) / (mTimeDuration);
                    Log.d("tag", "seek to");
                }
            }

            //if (mIsCut) {
            //  setIsUpdateView(false);
            //  return;

            //  }

            if (mIsUpdateView) {
                postDelayed(this, DELAY_TIME_MILLIS);
            }
        }
    }
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setIsUpdateView(true);
        postDelayed(updateViewRunnable, DELAY_TIME_MILLIS);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setIsUpdateView(false);
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
    private void drawAreaTimeCut(Canvas canvas) {
        mBarEditStartPaint.setColor(ContextCompat.getColor(mContext, R.color.colorThumb));
        mBarEditStartPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));
        canvas.drawRect(mPointStart.getX(), (mBarHeight / 3), mPointEnd.getX(), mBarHeight + 51, mBarEditStartPaint);
        invalidate();
    }


}
