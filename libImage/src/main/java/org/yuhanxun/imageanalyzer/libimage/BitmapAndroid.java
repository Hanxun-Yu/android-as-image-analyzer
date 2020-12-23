package org.yuhanxun.imageanalyzer.libimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;


/**
 * Some operation on Android Bitmap
 * Android Bitmap 相关操作
 */
public class BitmapAndroid {
    private final static String TAG = "BitmapUtil";

    public static void bitmapToPNGFile(Bitmap bitmap, String pathPNGFile) {
        try {
            FileOutputStream outputStream = new FileOutputStream(pathPNGFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void bitmapToJPGFile(Bitmap bitmap, String pathJPGFile) {
        try {
            FileOutputStream outputStream = new FileOutputStream(pathJPGFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap fromPNG(String pathPNGFile) {
        Bitmap bitmap = BitmapFactory.decodeFile(pathPNGFile);
        return bitmap;
    }

    public static Bitmap fromJPG(String pathJPGFile) {
        Bitmap bitmap = BitmapFactory.decodeFile(pathJPGFile);
        return bitmap;
    }

    public static Bitmap fromByteArr(byte[] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

    public static byte[] argb8888BitmapToARGB8888(Bitmap bitmap) {
        if (bitmap.getConfig() != Bitmap.Config.ARGB_8888)
            throw new IllegalArgumentException();

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int length = width * height;
        int[] pixels = new int[length];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        byte[] argb = new byte[length * 4];

        //bitmap的ARGB8888 取出时排列为 ARGB 从上往下
        for (int i = 0; i < length; i++) {
            int temp = pixels[i];
            int index = i * 4;

            //打开下面的注释可以看到，取出时已ARGB顺序
//            Color.alpha(temp);
//            Color.red(temp);
//            Color.green(temp);
//            Color.blue(temp);
            //A
            argb[index] = (byte) ((temp >> 24) & 0xff);//A = 0
            //R
            argb[index + 1] = (byte) ((temp >> 16) & 0xff);//R = 1
            //G
            argb[index + 2] = (byte) ((temp >> 8) & 0xff);  //G = 2
            //B
            argb[index + 3] = (byte) (temp & 0xff); //B = 3
        }
        return argb;
    }

    public static byte[] argb8888BitmapToRGBA8888(Bitmap bitmap) {
        byte[] argb = argb8888BitmapToARGB8888(bitmap);
        byte[] rgba = new byte[argb.length];

        int len = argb.length / 4;
        //bitmap的ARGB8888排列为 ARGB 从上往下
        //所以需要调整BR的位置
        for (int i = 0; i < len; i++) {
            int index = i * 4;
            //R
            rgba[index] = argb[index + 1];
            //G
            rgba[index + 1] = argb[index + 2];
            //B
            rgba[index + 2] = argb[index + 3];
            //A
            rgba[index + 3] = argb[index];
        }
        return rgba;
    }


    /**
     * bitmap 接收 RGBA
     * 注：从bitmap取出将是ABGR
     *
     * @param rawData
     * @param width
     * @param height
     * @return
     */
    public static Bitmap rgba8888ToBitmap(byte[] rawData, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(rawData));
        return bitmap;
    }


}
