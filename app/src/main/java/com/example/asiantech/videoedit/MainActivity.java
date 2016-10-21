package com.example.asiantech.videoedit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.asiantech.videoedit.utils.CustomSeekBarView;


public class MainActivity extends AppCompatActivity {
    private CustomSeekBarView seekBarLayout;
    private EditText mEdtNumber;
    private Button mBtnCut;

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
        VideoView videoView = (VideoView) findViewById(R.id.videoView);
        MediaController mediaControls = new MediaController(this);

        try {
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video);
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, uri);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeTotalVideo = Long.parseLong(time);
            seekBarLayout.setTimeDuration(timeTotalVideo);
            assert videoView != null;
            videoView.setMediaController(mediaControls);
            videoView.setVideoURI(uri);
            videoView.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mBtnCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBarLayout.setIsEditHard(true);
                seekBarLayout.setTextNumber(Integer.valueOf(mEdtNumber.getText().toString()));
                seekBarLayout.setIsEdit(false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnuPlayVideo:
                mBtnCut.setVisibility(View.GONE);
                mEdtNumber.setVisibility(View.GONE);
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
}
