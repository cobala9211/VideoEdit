package com.example.asiantech.videoedit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.asiantech.videoedit.utils.CustomSeekBarView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

//import com.example.asiantech.videoedit.utils.BitmapUtil;


public class MainActivity extends AppCompatActivity {
    private CustomSeekBarView seekBarLayout;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekBarLayout = (CustomSeekBarView) findViewById(R.id.valueBar);
        int[] arrImg = {R.mipmap.images, R.mipmap.images, R.mipmap.images, R.mipmap.images};
        int[] arrImages = {R.drawable.anh, R.drawable.anh, R.drawable.anh, R.drawable.anh, R.drawable.anh, R.drawable.anh};
        Bitmap[] arrBitmaps = new Bitmap[arrImages.length];
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        for (int i = 0; i < arrImages.length; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), arrImages[i]);
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
//        BitmapUtil bitmapss = BitmapUtil.centerCrop(arrBitmaps[0], 100, 100);

    }

    private static int changeBitmapArr(BitmapFactory.Options option, int reqWidth, int reqHeight) {
        final int height = option.outHeight;
        final int width = option.outWidth;
        int simepleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            simepleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return simepleSize;
    }

    public static Bitmap[] reSizeBitmap(Bitmap[] arrBitmaps, float maxImageSize, boolean filter) {
        Bitmap[] tamps = new Bitmap[arrBitmaps.length];
        for (int i = 0; i < arrBitmaps.length; i++) {
            float radio = Math.min((float) maxImageSize / arrBitmaps[i].getWidth(), (float) maxImageSize / arrBitmaps[i].getHeight());
            int width = Math.round((float) radio / (float) arrBitmaps[i].getWidth());
            int height = Math.round((float) radio / (float) arrBitmaps[i].getHeight());
            tamps[i] = Bitmap.createScaledBitmap(arrBitmaps[i], width, height, filter);
        }
        return tamps;
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
