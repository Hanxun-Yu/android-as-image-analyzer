package org.yuhanxun.imageanalyzer.libimage;

import android.graphics.Bitmap;

import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Some operation on Android Bitmap
 * Android Bitmap 相关操作
 */
public class BitmapAndroid {
    private final static String TAG = "BitmapUtil";
    public static byte[] argb8888BitmapToByteArr(Bitmap bitmap) {
        if (bitmap.getConfig() != Bitmap.Config.ARGB_8888)
            throw new IllegalArgumentException();


        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int length = width * height;
        int[] pixels = new int[length];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        byte[] argb = new byte[length * 4];

        //bitmap的ARGB8888排列为 ABGR 从上往下
        //所以需要调整BR的位置
        for (int i = 0; i < length; i++) {
            int temp = pixels[i];
            int index = i * 4;

            argb[index] = (byte) (temp >> 24 & 0xff);//A = 0
            argb[index + 1] = (byte) (temp & 0xff); //R = 3
            argb[index + 2] = (byte) (temp >> 8 & 0xff);  //G = 1
            argb[index + 3] = (byte) (temp >> 16 & 0xff);//B = 2
        }
        return argb;
    }


}
