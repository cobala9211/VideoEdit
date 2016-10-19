package com.example.asiantech.videoedit.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Copyright © 2016 AsianTech inc.
 * Created by PhuongDN on 19/10/2016.
 */
public class Graphics {
    public static Bitmap ScaleDownBitmap(Bitmap originalImage, float maxImageSize, boolean filter)
    {
        float ratio = Math.min((float)maxImageSize / originalImage.getWidth(), (float)maxImageSize / originalImage.getHeight());
        int width = (int)Math.round(ratio * (float)originalImage.getWidth());
        int height =(int) Math.round(ratio * (float)originalImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(originalImage, width, height, filter);
        return newBitmap;
    }

    public static Bitmap ScaleBitmap(Bitmap originalImage, int wantedWidth, int wantedHeight)
    {
        Bitmap output = Bitmap.createBitmap(wantedWidth, wantedHeight, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(output);
        Matrix m = new Matrix();
        m.setScale((float)wantedWidth / originalImage.getWidth(), (float)wantedHeight / originalImage.getHeight());
        canvas.drawBitmap(originalImage, m, new Paint());
        return output;
    }
}
