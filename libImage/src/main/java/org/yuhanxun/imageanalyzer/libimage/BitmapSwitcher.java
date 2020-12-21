package org.yuhanxun.imageanalyzer.libimage;

import org.yuhanxun.imageanalyzer.libimage.libyuv.YuvJni;

/**
 * Bitmap transformation implementation
 * 位图转换实现
 */
public class BitmapSwitcher {
    /**
     * add the format you need to learn here
     * 在这里添加你需要学习的格式
     */
    public enum Format {
        /**
         * https://blog.csdn.net/byhook/article/details/84262330
         * <p>
         * android平台下的RGB格式
         * 我们平时在android平台下处理Bitmap的时候，下面的几个参数应该接触的比较多：
         * <p>
         * Bitmap.Config.ALPHA_8：
         * 每个像素用8比特位表示，占1个字节，只有透明度，没有颜色。
         * <p>
         * Bitmap.Config.RGB_565：
         * 每个像素用16比特位表示，占2个字节，RGB分量分别使用5位、6位、5位，上面的图已经有作说明。
         * <p>
         * Bitmap.Config.ARGB_4444：
         * 每个像素用16比特位表示，占2个字节，由4个4位组成，ARGB分量都是4位。
         * <p>
         * Bitmap.Config.ARGB_8888：
         * 每个像素用32比特位表示，占4个字节，由4个8位组成，ARGB分量都是8位。
         * <p>
         * 注意：java默认使用大端字节序，c/c++默认使用小端字节序，
         * android平台下Bitmap.config.ARGB_8888的Bitmap默认是大端字节序ABGR8888，当需要把这个图片内存数据给小端语言使用的时候，就需要把大端字节序转换为小端字节序。
         * 例如：java层的ARGB_8888传递给jni层使用时，需要把java层的ARGB_8888的内存数据转换为BGRA8888。
         */
        YUV420SP_NV12,
        YUV420SP_NV21,
        YUV420P_I420,
        YUV_YV12,
        RGB_888,
        //        ARGB_8888,
        BGRA_8888,
        RGBA_8888
    }

    public static byte[] doSwitch(byte[] bmpRawData, int width, int height, Format src, Format target) {
        if (src == Format.YUV420SP_NV21 && target == Format.RGBA_8888) {
            return nv21ToRGBA8888(bmpRawData, width, height);
        }

        if (src == Format.YUV420SP_NV12 && target == Format.RGBA_8888) {
            return nv12ToRGBA8888(bmpRawData, width, height);
        }

        if (src == Format.YUV420SP_NV12 && target == Format.BGRA_8888) {
            return nv12ToBGRA8888(bmpRawData, width, height);
        }

        if (src == Format.YUV420SP_NV21 && target == Format.BGRA_8888) {
            return nv21ToBGRA8888(bmpRawData, width, height);
        }

        if (src == Format.RGBA_8888 && target == Format.RGB_888) {
            return rgba8888ToRGB888(bmpRawData, width, height);
        }


        return null;
    }

    private static byte[] nv21ToRGBA8888(byte[] bmpRawData, int width, int height) {
        byte[] ret = new byte[width * height * 4];
        YuvJni.yuvNV21ToRGBA8888(bmpRawData, ret, width, height);
        return ret;
    }

    private static byte[] nv12ToRGBA8888(byte[] bmpRawData, int width, int height) {
        byte[] ret = new byte[width * height * 4];
        YuvJni.yuvNV12ToRGBA8888(bmpRawData, ret, width, height);
        return ret;
    }

    private static byte[] nv21ToBGRA8888(byte[] bmpRawData, int width, int height) {
        byte[] ret = new byte[width * height * 4];
        YuvJni.yuvNV21ToBGRA8888(bmpRawData, ret, width, height);
        return ret;
    }

    private static byte[] nv12ToBGRA8888(byte[] bmpRawData, int width, int height) {
        byte[] ret = new byte[width * height * 4];
        YuvJni.yuvNV12ToBGRA8888(bmpRawData, ret, width, height);
        return ret;
    }

    private static byte[] rgba8888ToRGB888(byte[] bmpRawData, int width, int height) {
        byte[] ret = new byte[width * height * 3];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int rgbIndex = 3 * (row * width + col);
                int rgbaIndex = 4 * (row * width + col);
                ret[rgbIndex] = bmpRawData[rgbaIndex];
                ret[rgbIndex + 1] = bmpRawData[rgbaIndex + 1];
                ret[rgbIndex + 2] = bmpRawData[rgbaIndex + 2];
            }
        }
        return ret;
    }


}
