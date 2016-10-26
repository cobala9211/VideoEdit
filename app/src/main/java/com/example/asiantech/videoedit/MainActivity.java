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
    private CustomSeekBarView seekBarLayout;
    private EditText mEdtNumber;
    private Button mBtnCut;
    private long mTimeDurationVideo;
    Handler mHandler;
    int mTimeStart;
    int mTimeEnd;
    CustomVideoView videoView;
    MediaController mediaControls;

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
        seekBarLayout = (CustomSeekBarView) findViewById(R.id.valueBar);
        mEdtNumber = (EditText) findViewById(R.id.edtNumber);
        mBtnCut = (Button) findViewById(R.id.btnCut);
        int[] arrImg = {R.mipmap.images, R.mipmap.images, R.mipmap.images, R.mipmap.images};
        Bitmap[] arrBitmaps = new Bitmap[arrImg.length];
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        for (int i = 0; i < arrImg.length; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), arrImg[i]);
            arrBitmaps[i] = bitmap;
        }

        seekBarLayout.setListBitmapBit(arrBitmaps);
        videoView = (CustomVideoView) findViewById(R.id.videoView);
        mediaControls = new MediaController(this);

        try {
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video);
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, uri);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            mTimeDurationVideo = Long.parseLong(time);
            seekBarLayout.setTimeDuration(mTimeDurationVideo);
            assert videoView != null;
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    seekBarLayout.setIsUpdateView(true);
                    seekBarLayout.mTimeCurrent = 0;
                    videoView.start();
                }
            });
            mHandler = new Handler();
            Log.d("time", "time start" + mTimeStart);
            Log.d("time", "time end " + mTimeEnd);
            seekBarLayout.setSendTimeListener(new ISendTime() {
                @Override
                public void timeToCut(int timeX, int timeY) {
                    videoView.seekTo(timeX * 1000);
                    videoView.postDelayed(stopVideo, timeY * 1000);

                }
            });


            videoView.setPlayPauseListener(new IPlayVideoListener() {
                @Override
                public void onPlay() {
                    Log.d("tag", "on PLay");
                    seekBarLayout.mIsPLay = true;
                }

                @Override
                public void onPause() {
                    Log.d("tag", "on Pause");
                    seekBarLayout.mIsPLay = false;
                }

                @Override
                public void seekTo(int time) {
                    Log.d("tag", "time" + time);
                    seekBarLayout.mIsSeekTo = true;
                    seekBarLayout.mTimeCurrent = time;
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
                if (Long.valueOf(mEdtNumber.getText().toString()) > (mTimeDurationVideo / 1000)) {
                    //  seekBarLayout.mIsTouch = false;
                    Toast.makeText(getApplicationContext(),
                            "over time duration of video", Toast.LENGTH_SHORT).show();
                } else {
                    // seekBarLayout.mIsTouch = true;
                    seekBarLayout.setIsEditHard(true);
                    seekBarLayout.setTimesToCut(Integer.valueOf(mEdtNumber.getText().toString()));
                    seekBarLayout.setIsEdit(false);
                }
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnuPlayVideo:
                mBtnCut.setVisibility(View.GONE);
                mEdtNumber.setVisibility(View.GONE);
                seekBarLayout.mIsUpdateView = true;
                seekBarLayout.setIsEdit(false);
                seekBarLayout.setIsEditHard(false);
                break;
            case R.id.mnuEditVideo:
                mBtnCut.setVisibility(View.GONE);
                mEdtNumber.setVisibility(View.GONE);
                seekBarLayout.setIsEdit(true);
                seekBarLayout.setIsEditHard(false);
                break;
            case R.id.mnuEditVideos:
                mBtnCut.setVisibility(View.VISIBLE);
                mEdtNumber.setVisibility(View.VISIBLE);
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