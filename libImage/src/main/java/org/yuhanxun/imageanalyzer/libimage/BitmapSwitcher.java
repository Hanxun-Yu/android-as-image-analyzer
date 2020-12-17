package org.yuhanxun.imageanalyzer.libimage;

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
        YUV420SP_NV12,
        YUV420SP_NV21,
        YUV420P_I420,
        YUV_YV12,
        RGB_888,
        ARGB_8888,
        ABGR_8888,
        RGBA_8888
    }
}
