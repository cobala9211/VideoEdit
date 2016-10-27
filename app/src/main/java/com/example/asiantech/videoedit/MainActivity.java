package com.example.asiantech.videoedit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;

import com.example.asiantech.videoedit.listeners.IPlayVideoListener;
import com.example.asiantech.videoedit.listeners.ISendTime;
import com.example.asiantech.videoedit.utils.CustomSeekBarView;
import com.example.asiantech.videoedit.utils.CustomVideoView;


public class MainActivity extends AppCompatActivity {
    private CustomSeekBarView mSeekBarCustomLayout;
    private EditText mEdtTimeToCut;
    private Button mBtnCut;
    private long mTimeDurationVideo;
    private Handler mHandler;
    private int mTimeStart;
    private int mTimeEnd;
    private CustomVideoView videoView;
    private MediaController mediaControls;

    Runnable stopVideo = new Runnable() {
        @Override
        public void run() {
            videoView.pause();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSeekBarCustomLayout = (CustomSeekBarView) findViewById(R.id.valueBar);
        mEdtTimeToCut = (EditText) findViewById(R.id.edtNumber);
        mBtnCut = (Button) findViewById(R.id.btnCut);
        int[] arrImg = {R.mipmap.images, R.mipmap.images, R.mipmap.images, R.mipmap.images};
        Bitmap[] arrBitmaps = new Bitmap[arrImg.length];
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        for (int i = 0; i < arrImg.length; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), arrImg[i]);
            arrBitmaps[i] = bitmap;
        }

        mSeekBarCustomLayout.setListBitmapBit(arrBitmaps);
        videoView = (CustomVideoView) findViewById(R.id.videoView);
        mediaControls = new MediaController(this);
        mHandler = new Handler();
        try {
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video);
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, uri);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            mTimeDurationVideo = Long.parseLong(time);
            mSeekBarCustomLayout.setTimeDuration(mTimeDurationVideo);
            assert videoView != null;
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mSeekBarCustomLayout.setIsUpdateView(true);
                    mSeekBarCustomLayout.mTimeCurrent = 0;
                    videoView.start();
                }
            });

            mSeekBarCustomLayout.setSendTimeListener(new ISendTime() {
                @Override
                public void timeToCut(int timeX, int timeY) {
                    videoView.seekTo(timeX * 1000);
                    mHandler.postDelayed(stopVideo, timeY * 1000);
                    mTimeStart = timeX * 1000;
                    mTimeEnd = timeY * 1000;
                    Log.d("tag", "time start " + mTimeStart);
                    Log.d("tag", "time end " + mTimeEnd);
                }
            });


            videoView.setPlayPauseListener(new IPlayVideoListener() {
                @Override
                public void onPlay() {
                    Log.d("tag", "on PLay");
                    if (mSeekBarCustomLayout.mIsCut || mSeekBarCustomLayout.mIsCutWithTimes) {
                        videoView.seekTo(mTimeStart);
                        Log.d("tag", "cut");
                        // mHandler.postDelayed(stopVideo, mTimeEnd);
                        Log.d("tag", "time start" + mTimeStart);
                        Log.d("tag", "time end" + mTimeEnd);
                    } else {
                        mSeekBarCustomLayout.mIsPLay = true;
                        // TODO send current pos for customseekbarview class
                        mSeekBarCustomLayout.mTimeCurrent = videoView.getCurrentPosition();
                    }
                }

                @Override
                public void onPause() {
                    Log.d("tag", "on Pause");
                    mSeekBarCustomLayout.mIsPLay = false;
                }

                @Override
                public void seekTo(int time) {
                    Log.d("tag", "time" + time);
                    mSeekBarCustomLayout.mIsSeekTo = true;
                    mSeekBarCustomLayout.mTimeCurrent = time;
                }

                @Override
                public void onStop() {
                    Log.d("tag", "stop");
                }
            });
            mediaControls.setAnchorView(videoView);
            videoView.setMediaController(mediaControls);
            videoView.setVideoURI(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mBtnCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Long.valueOf(mEdtTimeToCut.getText().toString()) > (mTimeDurationVideo / 1000)) {
                    //  mSeekBarCustomLayout.mIsTouch = false;
                    Toast.makeText(getApplicationContext(),
                            "over time duration of video", Toast.LENGTH_SHORT).show();
                } else {
                    // mSeekBarCustomLayout.mIsTouch = true;
                    mSeekBarCustomLayout.setIsCutWithTime(true);
                    mSeekBarCustomLayout.setTimesToCut(Integer.valueOf(mEdtTimeToCut.getText().toString()));
                    mSeekBarCustomLayout.setIsCut(false);
                }
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnuPlayVideo:
                mBtnCut.setVisibility(View.GONE);
                mEdtTimeToCut.setVisibility(View.GONE);
                mSeekBarCustomLayout.mIsUpdateView = true;
                mSeekBarCustomLayout.setIsCut(false);
                mSeekBarCustomLayout.setIsCutWithTime(false);
                break;
            case R.id.mnuEditVideo:
                mBtnCut.setVisibility(View.GONE);
                mEdtTimeToCut.setVisibility(View.GONE);
                mSeekBarCustomLayout.setIsCut(true);
                mSeekBarCustomLayout.setIsCutWithTime(false);
                break;
            case R.id.mnuEditVideos:
                mBtnCut.setVisibility(View.VISIBLE);
                mEdtTimeToCut.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("tag", "onresume ");


    }
}