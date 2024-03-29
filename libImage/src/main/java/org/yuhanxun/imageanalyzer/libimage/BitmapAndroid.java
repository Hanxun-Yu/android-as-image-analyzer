package org.yuhanxun.imageanalyzer.libimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileOutputStream;
import java.io.IOException;
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

    public static byte[] bitmapToRGB24(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] intValues = new int[width * height];
        bitmap.getPixels(intValues, 0, width, 0, 0, width,
                height);
        byte[] rgb = new byte[width * height * 3];
        for (int i = 0; i < intValues.length; ++i) {
            final int val = intValues[i];
            rgb[i * 3] = (byte) ((val >> 16) & 0xFF);//R
            rgb[i * 3 + 1] = (byte) ((val >> 8) & 0xFF);//G
            rgb[i * 3 + 2] = (byte) (val & 0xFF);//B
        }
//        Log.d(TAG, "r:" + (int) (rgb[0] & 0xff) + " g:" + (int) (rgb[1] & 0xff) + " b:" + (int) (rgb[2] & 0xff));
        return rgb;
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
     * bitmap copyPixelsFromBuffer 接收 RGBA
     * <p>
     * 一次性写入
     *
     * @param rawData
     * @param width
     * @param height
     * @return
     */
    public static Bitmap fromRGBA8888ByCopyPixelsFromBuffer(byte[] rawData, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(rawData));
        return bitmap;
    }

    /**
     * bitmap setPixel 接收 ARGB
     * <p>
     * 手工组装color
     *
     * @param rawData
     * @param width
     * @param height
     * @return
     */
    public static Bitmap fromARGB8888BySetPixel(byte[] rawData, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int index = 4 * (row * width + col);
                byte a = rawData[index];//A
                byte r = rawData[index + 1];//R
                byte g = rawData[index + 2];//G
                byte b = rawData[index + 3];//B
                int color = (a << 24 & 0xff000000)
                        | (r << 16 & 0x00ff0000)
                        | (g << 8 & 0x0000ff00)
                        | (b & 0x000000ff);

                //注意这里接受x，y 等价于 col,row
                bitmap.setPixel(col, row, color);
            }
        }
        return bitmap;
    }


}
