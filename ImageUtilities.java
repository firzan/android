package com.witel.firzan.mss.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Firzan on 09/06/2016.
 */
public class ImageUtilities {

    /***
     * returns bitmap of specific size
     *
     * @param imagePath path of image (absolute)
     * @param reqWidth  width of bitmap to be return
     * @param reqHeight height of bitmap to be return
     * @return bitmap
     */
    public static Bitmap decodeSampledBitmapFromResource(String imagePath, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(imagePath, options);
    }
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }



    public static String copyCompressImage(String sourceLocation,
                                           String targetLocation, Context context)
            throws IOException {

        File file = new File(targetLocation);
        if (!file.exists()) {
            file.createNewFile();
        }
        InputStream in = new FileInputStream(sourceLocation);
        OutputStream out = new FileOutputStream(file);

        in.close();

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);

        int reqWidth = dm.widthPixels;
        int reqHeight = dm.heightPixels;

        //System.out.println("ReqWidth : " + reqWidth + " reqHeight : " + reqHeight);

        Bitmap resizedBitmap = decodeSampledBitmapFromResource(sourceLocation, reqWidth, reqHeight);

        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        out.flush();
        out.close();

        return file.getAbsolutePath();
    }

    public static String getStringFromImage(String path){
        Bitmap bitmap1=BitmapFactory.decodeFile(path);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap1.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        return Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);
    }

}
