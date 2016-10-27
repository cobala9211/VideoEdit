package com.example.asiantech.videoedit.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

import com.example.asiantech.videoedit.listeners.IPlayVideoListener;

/**
 * Copyright © 2016 AsianTech inc.
 * Created by tranglt  on 21/10/2016.
 */

public class CustomVideoView extends VideoView {

    private IPlayVideoListener mIPlayVideoListener;

    public CustomVideoView(Context context) {
        super(context);
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPlayPauseListener(IPlayVideoListener listener) {
        mIPlayVideoListener = listener;
    }


    @Override
    public void pause() {
        super.pause();
        if (mIPlayVideoListener != null) {
            mIPlayVideoListener.onPause();
        }
    }

    @Override
    public void start() {
        super.start();
        if (mIPlayVideoListener != null) {
            mIPlayVideoListener.onPlay();
        }
    }

    @Override
    public void seekTo(int msec) {
        super.seekTo(msec);
        if (mIPlayVideoListener != null) {
            mIPlayVideoListener.seekTo(msec);
        }
    }

    @Override
    public void stopPlayback() {
        super.stopPlayback();
        if (mIPlayVideoListener != null) {
            mIPlayVideoListener.onStop();
        }
    }
}