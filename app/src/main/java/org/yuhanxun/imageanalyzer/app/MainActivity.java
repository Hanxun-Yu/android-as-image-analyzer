package org.yuhanxun.imageanalyzer.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import org.yuhanxun.imageanalyzer.libimage.BitmapAndroid;
import org.yuhanxun.imageanalyzer.libimage.BitmapSwitcher;
import org.yuhanxun.imageanalyzer.libimage.ImageFileWrapper;
import org.yuhanxun.libcommonutil.file.AndroidResRW;

import java.io.File;


public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    ImageView mImageView;
    String emmcPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        if (checkPermission()) {
            logD("has Permission");
            doTest();
        } else {
            logD("no Permission");
        }
    }

    private void doTest() {
        logD("doTest()");

//        test_nv21_to_BGRA_bmp();
//        test_yuvplayer_show_RGB32_RGB24();
//        test_BGRA_to_nv12();
//        test_BGRA_to_nv21();

        //png jpg 有损 无损
//        testPNGCodecIsLossless();
//        testJPGCodecIsLossless();


        //bitmap ARGB 排列
//        testSort_BitmapGetPixelsARGB8888();
//        testSort_BitmapSetPixelsARGB8888();
    }


    private void test_yuvplayer_show_RGB32_RGB24() {
        int w = 150;
        int h = 150;
        String assetFileName = "nv21_150x150.yuv";
        byte[] nv21 = AndroidResRW.getByteArrFromAssets(this, assetFileName);
        byte[] rgba8888 = BitmapSwitcher.doSwitch(nv21, w, h,
                BitmapSwitcher.Format.YUV420SP_NV21, BitmapSwitcher.Format.RGBA_8888);

        byte[] rgb888 = BitmapSwitcher.doSwitch(rgba8888, w, h,
                BitmapSwitcher.Format.RGBA_8888, BitmapSwitcher.Format.RGB_888);

        /**
         * 使用雷神yuvplayer观察
         * (yuvplayer RGB32 接收内存序列 RGBA)
         * (yuvplayer RGB24 接收内存序列 RGB)
         */
        writeOverFileToEmmc(rgba8888, "rgba8888.rgb");
        writeOverFileToEmmc(rgb888, "rgb888.rgb");
    }

    private void test_BGRA_to_nv12() {
        int w = 150;
        int h = 150;
        String assetFileName = "nv21_150x150.yuv";
        byte[] nv21 = AndroidResRW.getByteArrFromAssets(this, assetFileName);
        byte[] bgra8888 = BitmapSwitcher.doSwitch(nv21, w, h,
                BitmapSwitcher.Format.YUV420SP_NV21, BitmapSwitcher.Format.BGRA_8888);
        byte[] nv12 = BitmapSwitcher.doSwitch(bgra8888, w, h,
                BitmapSwitcher.Format.BGRA_8888, BitmapSwitcher.Format.YUV420SP_NV12);
        writeOverFileToEmmc(nv12, "nv12.yuv");
    }

    private void test_BGRA_to_nv21() {
        int w = 150;
        int h = 150;
        String assetFileName = "nv21_150x150.yuv";
        byte[] nv21_src = AndroidResRW.getByteArrFromAssets(this, assetFileName);
        byte[] bgra8888 = BitmapSwitcher.doSwitch(nv21_src, w, h,
                BitmapSwitcher.Format.YUV420SP_NV21, BitmapSwitcher.Format.BGRA_8888);
        byte[] nv21 = BitmapSwitcher.doSwitch(bgra8888, w, h,
                BitmapSwitcher.Format.BGRA_8888, BitmapSwitcher.Format.YUV420SP_NV21);
        writeOverFileToEmmc(nv21, "nv21.yuv");
    }


    /**
     * 验证nv21 转出 bgra没有问题
     * 验证bmp文件由bgr从下到上排列
     * 验证yuvplayer 解析RGB32顺序是RGBA (yuvplayer读取这个bgra8888.rgb，颜色偏紫，由于br顺序交换导致)
     */
    private void test_nv21_to_BGRA_bmp() {
        //1.nv21文件 转 argb保存文件，使用yuvplayer观察裸数据
        int w = 150;
        int h = 150;
        String assetFileName = "nv21_150x150.yuv";
        byte[] nv21 = AndroidResRW.getByteArrFromAssets(this, assetFileName);
        byte[] bgra8888 = BitmapSwitcher.doSwitch(nv21, w, h,
                BitmapSwitcher.Format.YUV420SP_NV21, BitmapSwitcher.Format.BGRA_8888);

        //存储位图
        writeOverFileToEmmc(bgra8888, "bgra8888.rgb");
        //存储bmp文件观察
        ImageFileWrapper.bgra8888ToBmpFile(bgra8888, w, h,
                emmcPath + File.separator + "bgra8888.bmp");

        logD("OK!");
    }


    /**
     * 结论
     * libyuv输出的ARGB在内存中是倒序 BGRA
     */
    public void testLibyuvNV21ToARGB() {
        /**
         * 手动生成1个有色像素点，其余白色 观察返回ARGB排列， 验证大小端问题
         */
        byte[] nv21 = new byte[(int) (10 * 10 * 1.5)];
        nv21[0] = -28; //y  有符号0 ->无符号数0 => -28 -> 228

        /**
         * UV分量使用无符号数，当等于128时，表示为0
         * 所以java层给有符号数-128
         * 这里让uv为128,使图片黑白， 这样rgb会等于y分量
         */
        nv21[100] = -128; //v 有符号-128 ->无符号数128
        nv21[101] = -128; //u

        byte[] bgra = BitmapSwitcher.doSwitch(nv21, 10, 10, BitmapSwitcher.Format.YUV420SP_NV21,
                BitmapSwitcher.Format.BGRA_8888);

        for (int i = 0; i < bgra.length; i++) {
            logD("i:" + i + " byte:" + bgra[i]);
        }
    }

    /**
     * 验证Bitmap getPixels 读取出ARGB的顺序
     * 结果：ARGB
     */
    public void testSort_BitmapGetPixelsARGB8888() {
        int w = 1280;
        int h = 720;
        String assetFileName = "1280x720.jpg";
        byte[] jpg = AndroidResRW.getByteArrFromAssets(this, assetFileName);
        Bitmap jpgBitmap = BitmapAndroid.fromByteArr(jpg);
        byte[] rgba = BitmapAndroid.argb8888BitmapToRGBA8888(jpgBitmap);
        ImageFileWrapper.rgba8888ToBmpFile(rgba, w, h, emmcPath + File.separator + "rgba.bmp");
    }

    /**
     * 验证Bitmap setPixels 写入ARGB的顺序需要是？
     * 结果:RGBA
     */
    public void testSort_BitmapSetPixelsARGB8888() {
        int w = 150;
        int h = 150;
        String assetFileName = "nv21_150x150.yuv";
        byte[] nv21 = AndroidResRW.getByteArrFromAssets(this, assetFileName);
        byte[] rgba = BitmapSwitcher.doSwitch(nv21, w, h, BitmapSwitcher.Format.YUV420SP_NV21, BitmapSwitcher.Format.RGBA_8888);
        Bitmap bitmap = BitmapAndroid.fromRGBA8888_2(rgba, w, h);

        mImageView.setImageBitmap(bitmap);
    }

    /**
     * 验证Android的png编码是否无损
     * 结果：无损
     */
    public void testPNGCodecIsLossless() {
        int w = 150;
        int h = 150;
        String assetFileName = "nv21_150x150.yuv";

        byte[] nv21 = AndroidResRW.getByteArrFromAssets(this, assetFileName);
        //原始rgba
        byte[] rgbaSrc = BitmapSwitcher.doSwitch(nv21, w, h,
                BitmapSwitcher.Format.YUV420SP_NV21, BitmapSwitcher.Format.RGBA_8888);
        writeOverFileToEmmc(rgbaSrc, "rgbaSrc.rgb");
        //用这个rgba，构造一个bitmap
        Bitmap bitmapSrc = BitmapAndroid.fromRGBA8888(rgbaSrc, w, h);


        //bitmap编码png
        String pathPng = emmcPath + File.separator + "test.png";
        BitmapAndroid.bitmapToPNGFile(bitmapSrc, pathPng);

        Bitmap bitmapPng = BitmapAndroid.fromPNG(pathPng);
        //解码png得到rgba
        byte[] rgbaPng = BitmapAndroid.argb8888BitmapToRGBA8888(bitmapPng);
        writeOverFileToEmmc(rgbaPng, "rgbaPng.rgb");

        //比较rgbaSrc 与 rgbaPng

        //使用md5校验 与 文件二进制比较， 2串字节数组完全一致
    }

    /**
     * 验证Android的jpg最高质量100编码是否无损
     * 结果：有损 rgba 值前后不一致
     */
    public void testJPGCodecIsLossless() {
        int w = 150;
        int h = 150;
        String assetFileName = "nv21_150x150.yuv";

        byte[] nv21 = AndroidResRW.getByteArrFromAssets(this, assetFileName);
        //原始rgba
        byte[] rgbaSrc = BitmapSwitcher.doSwitch(nv21, w, h,
                BitmapSwitcher.Format.YUV420SP_NV21, BitmapSwitcher.Format.RGBA_8888);
        writeOverFileToEmmc(rgbaSrc, "rgbaSrc.rgb");
        //用这个rgba，构造一个bitmap
        Bitmap bitmapSrc = BitmapAndroid.fromRGBA8888(rgbaSrc, w, h);


        //bitmap编码png
        String pathPng = emmcPath + File.separator + "test.jpg";
        BitmapAndroid.bitmapToJPGFile(bitmapSrc, pathPng);

        Bitmap bitmapPng = BitmapAndroid.fromJPG(pathPng);
        //解码png得到rgba
        byte[] rgbaPng = BitmapAndroid.argb8888BitmapToRGBA8888(bitmapPng);
        writeOverFileToEmmc(rgbaPng, "rgbaJPG.rgb");

        //比较rgbaSrc 与 rgbaJPG

        //使用md5校验 与 文件二进制比较  内容不同
    }


    private void logD(String msg) {
        Log.d(TAG, msg);
    }

    /**
     * 覆盖写入文件到emmc根目录（存在将删除）
     *
     * @param fileName
     */
    private void writeOverFileToEmmc(byte[] data, String fileName) {
        String targetPath = emmcPath + File.separator + fileName;
        if (new File(targetPath).exists())
            new File(targetPath).delete();
        ImageFileWrapper.rawFile(data, targetPath);
    }

    private void init() {
        emmcPath = Environment.getExternalStorageDirectory().getPath();
        mImageView = findViewById(R.id.image);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    final int REQUEST_CODE = 99;

    private boolean checkPermission() {
        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };
        //如果返回true表示已经授权了
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            logD("checkPermission() ok!");
            return true;
        } else {
            // 类似 startActivityForResult()中的REQUEST_CODE
            // 权限列表,将要申请的权限以数组的形式提交。
            // 系统会依次进行弹窗提示。
            // 注意：如果AndroidManifest.xml中没有进行权限声明，这里配置了也是无效的，不会有弹窗提示。
            logD("requestPermissions()");

            ActivityCompat.requestPermissions(this,
                    permissions,
                    REQUEST_CODE);
            return false;
        }

    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        logD("onRequestPermissionsResult() requestCode:" + requestCode);

        switch (requestCode) {
            case REQUEST_CODE: {
                logD("onRequestPermissionsResult grantResults.length:" + grantResults.length);

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    logD("onRequestPermissionsResult ok");

                    if (checkPermission()) {
                        doTest();
                    }
                    // 权限同意了，做相应处理
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // 用户拒绝过这个权限了，应该提示用户，为什么需要这个权限。
                        logD("onRequestPermissionsResult !ok");
                    }
                }
            }
            return;
        }
    }
}