package com.example.asiantech.videoedit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.asiantech.videoedit.utils.CustomSeekBarView;

public class MainActivity extends AppCompatActivity {
    private CustomSeekBarView seekBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekBarLayout = (CustomSeekBarView) findViewById(R.id.valueBar);
        int[] arrImg = {R.mipmap.images, R.mipmap.images, R.mipmap.images, R.mipmap.images};
        Bitmap[] arrBitmaps = new Bitmap[arrImg.length];
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        for (int i = 0; i < arrImg.length; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), arrImg[i]);
            arrBitmaps[i] = bitmap;
        }

        seekBarLayout.setListBitmapBit(arrBitmaps);
        VideoView videoView = (VideoView) findViewById(R.id.videoView);
        MediaController mediaControls = new MediaController(this);

        try {
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video);
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, uri);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeTotalVideo = Long.parseLong(time);
            seekBarLayout.setTimeDuration(timeTotalVideo);
            videoView.setMediaController(mediaControls);
            videoView.setVideoURI(uri);
            videoView.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnuPlayVideo:
                seekBarLayout.setIsEdit(false);
                break;
            case R.id.mnuEditVideo:
                seekBarLayout.setIsEdit(true);
                break;
            case R.id.mnuSaveState:
                seekBarLayout.setSaveState(true);
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
}

