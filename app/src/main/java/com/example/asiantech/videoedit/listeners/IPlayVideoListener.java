package com.example.asiantech.videoedit.listeners;

/**
 * Copyright © 2016 AsianTech inc.
 * Created by TrangLT on 24/10/2016.
 */

public interface IPlayVideoListener {
    void onPlay();

    void onPause();

    void seekTo(int time);

    void onStop();


}
