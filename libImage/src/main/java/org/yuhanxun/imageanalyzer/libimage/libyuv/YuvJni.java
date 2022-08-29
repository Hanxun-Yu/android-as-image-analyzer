package org.yuhanxun.imageanalyzer.libimage.libyuv;

public class YuvJni {

    static {
        System.loadLibrary("yuvjni");
    }
    public static native void bgra8888ToYuvNV21(byte[] src, byte[] target, int width, int height);
    public static native void bgra8888ToYuvNV12(byte[] src, byte[] target, int width, int height);
    public static native void yuvNV12ToRGBA8888(byte[] src, byte[] target, int width, int height);
    public static native void yuvNV12ToRGB24(byte[] src, byte[] target, int width, int height);
    public static native void yuvNV21ToRGBA8888(byte[] src, byte[] target, int width, int height);
    public static native void yuvNV12ToBGRA8888(byte[] src, byte[] target, int width, int height);
    public static native void yuvNV21ToBGRA8888(byte[] src, byte[] target, int width, int height);

}
