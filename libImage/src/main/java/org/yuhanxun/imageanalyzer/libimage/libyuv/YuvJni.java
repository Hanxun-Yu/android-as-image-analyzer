package org.yuhanxun.imageanalyzer.libimage.libyuv;

public class YuvJni {

    static {
        System.loadLibrary("yuvjni");
    }
    public static native void argb8888ToNV21(byte[] src, byte[] target, int width, int height);
    public static native void argb8888ToNV12(byte[] src, byte[] target, int width, int height);
}
