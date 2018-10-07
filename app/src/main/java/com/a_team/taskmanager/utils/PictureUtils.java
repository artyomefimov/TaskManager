package com.a_team.taskmanager.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * Created by ывв on 06.08.2018.
 */

public class PictureUtils {
    public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);

        return getScaledBitmap(path, size.x, size.y);
    }

    private static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        // reading image size
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        // counting degree of scaling

        int inSampleSize = 1;
        if (srcWidth > destWidth || srcHeight > destHeight) {
            float scaleWidth = srcWidth/destWidth;
            float scaleHeight = srcHeight/destHeight;

            inSampleSize = Math.round(scaleHeight > scaleWidth ? scaleHeight : scaleWidth);
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(path, options);
    }
}