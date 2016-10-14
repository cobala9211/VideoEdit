package com.example.asiantech.videoedit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.example.asiantech.videoedit.utils.CustomSeekBarView;

public class MainActivity extends AppCompatActivity {
    private CustomSeekBarView seekBarLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekBarLayout = (CustomSeekBarView) findViewById(R.id.llSeekbar);
        seekBarLayout.setOnLick(onCanvas);
    }

    CustomSeekBarView.referDrawView onCanvas = new CustomSeekBarView.referDrawView() {
        @Override
        public void getCanvasDraw(Canvas canvas) {
            int[] arrImg = {R.mipmap.images, R.mipmap.images, R.mipmap.images, R.mipmap.images,R.mipmap.images, R.mipmap.images, R.mipmap.images, R.mipmap.images,R.mipmap.images, R.mipmap.images, R.mipmap.images, R.mipmap.images,R.mipmap.images, R.mipmap.images, R.mipmap.images, R.mipmap.images,R.mipmap.images, R.mipmap.images, R.mipmap.images, R.mipmap.images};
            Bitmap[] arrBitmaps = new Bitmap[arrImg.length];
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int mWidthScreen = displaymetrics.widthPixels;
            for (int i = 0; i < arrImg.length; i++) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), arrImg[i]);
                Bitmap img = Bitmap.createScaledBitmap(bitmap, mWidthScreen/arrImg.length, 300, false);
                arrBitmaps[i]=img;
            }
            seekBarLayout.drawImage(canvas, arrBitmaps);
            seekBarLayout.drawThumb(canvas);
        }
    } ;
}
