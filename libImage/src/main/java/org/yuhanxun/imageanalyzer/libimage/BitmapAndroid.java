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

    public static void argb8888ToBmpFile(byte[] argb, int width, int height, String filePath) {
        int w = width;
        int h = height;
        int bmpDateSize = w * h * 3;
        int size = 14 + 40 + bmpDateSize;
        byte buffer[] = new byte[size];

        // 1.BMP文件头 14
        buffer[0] = 0x42; //bfType 2bytes
        buffer[1] = 0x4D;
        buffer[2] = (byte) ((size >> 0) & 0xFF); //bfSize 4bytes
        buffer[3] = (byte) ((size >> 8) & 0xFF);
        buffer[4] = (byte) ((size >> 16) & 0xFF);
        buffer[5] = (byte) ((size >> 24) & 0xFF);
        buffer[6] = 0x00; //bfReserved1 2bytes
        buffer[7] = 0x00;
        buffer[8] = 0x00; //bfReserved2 2bytes
        buffer[9] = 0x00;
        buffer[10] = 0x36; //bfOffBits 14+40 4bytes
        buffer[11] = 0x00;
        buffer[12] = 0x00;
        buffer[13] = 0x00;
        // 2.BMP信息头 40
        buffer[14] = 0x28; //biSize 40 4bytes
        buffer[15] = 0x00;
        buffer[16] = 0x00;
        buffer[17] = 0x00;
        buffer[18] = (byte) ((w >> 0) & 0xFF); //biWidth 4bytes
        buffer[19] = (byte) ((w >> 8) & 0xFF);
        buffer[20] = (byte) ((w >> 16) & 0xFF);
        buffer[21] = (byte) ((w >> 24) & 0xFF);
        buffer[22] = (byte) ((h >> 0) & 0xFF); //biHeight 4bytes
        buffer[23] = (byte) ((h >> 8) & 0xFF);
        buffer[24] = (byte) ((h >> 16) & 0xFF);
        buffer[25] = (byte) ((h >> 24) & 0xFF);
        buffer[26] = 0x01; //biPlanes 2bytes
        buffer[27] = 0x00;
        buffer[28] = 0x18; //biBitCount 24位位图 2bytes
        buffer[29] = 0x00;
        buffer[30] = 0x00; //biCompression 4bytes
        buffer[31] = 0x00;
        buffer[32] = 0x00;
        buffer[33] = 0x00;
        buffer[34] = 0x00; //biSizeImage 4bytes
        buffer[35] = 0x00;
        buffer[36] = 0x00;
        buffer[37] = 0x00;
        buffer[38] = 0x00; //biXpelsPerMeter 4bytes
        buffer[39] = 0x00;
        buffer[40] = 0x00;
        buffer[41] = 0x00;
        buffer[42] = 0x00; //biYPelsPerMeter 4bytes
        buffer[43] = 0x00;
        buffer[44] = 0x00;
        buffer[45] = 0x00;
        buffer[46] = 0x00; //biClrUsed 4bytes
        buffer[47] = 0x00;
        buffer[48] = 0x00;
        buffer[49] = 0x00;
        buffer[50] = 0x00; //biClrImportant 4bytes
        buffer[51] = 0x00;
        buffer[52] = 0x00;
        buffer[53] = 0x00;
        byte bmpData[] = new byte[bmpDateSize];
        for (int row = 0; row < height; row++) {
            int bmpRow = height - row - 1;
            for (int col = 0; col < width; col++) {
                int argbIndex = row * width + col;
                int bmpIndex = bmpRow * width + col;
                bmpData[3 * bmpIndex] = argb[4 * argbIndex + 1];
                bmpData[3 * bmpIndex + 1] = argb[4 * argbIndex + 2];
                bmpData[3 * bmpIndex + 2] = argb[4 * argbIndex + 3];
                //argb[4*index] 是A通道，丢弃
            }
        }
        System.arraycopy(bmpData, 0, buffer, 54, bmpDateSize);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.write(buffer);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
