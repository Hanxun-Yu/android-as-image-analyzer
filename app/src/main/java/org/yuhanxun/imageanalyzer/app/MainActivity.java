package org.yuhanxun.imageanalyzer.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


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
//        testNV21ToBGRA();
        testNV21ToRGBA_RGB();
//        testLibyuvNV21ToARGB();
    }

    private void testNV21ToRGBA_RGB() {
        byte[] nv21 = AndroidResRW.getByteArrFromAssets(this, "nv21_150x150.yuv");
        byte[] rgba8888 = BitmapSwitcher.doSwitch(nv21, 150, 150,
                BitmapSwitcher.Format.YUV420SP_NV21, BitmapSwitcher.Format.RGBA_8888);

        byte[] rgb888 = BitmapSwitcher.doSwitch(rgba8888, 150, 150,
                BitmapSwitcher.Format.RGBA_8888, BitmapSwitcher.Format.RGB_888);

        /**
         * 使用雷神yuvplayer观察
         * (yuvplayer RGB32 接收内存序列 RGBA)
         * (yuvplayer RGB24 接收内存序列 RGB)
         */
        writeOverFileToEmmc(rgba8888, "rgba8888.rgb");
        writeOverFileToEmmc(rgb888, "rgb888.rgb");
    }

    private void testNV21ToBGRA() {
        //1.nv21文件 转 argb保存文件，使用yuvplayer观察裸数据
        byte[] nv21 = AndroidResRW.getByteArrFromAssets(this, "nv21_150x150.yuv");
        byte[] bgra8888 = BitmapSwitcher.doSwitch(nv21, 150, 150,
                BitmapSwitcher.Format.YUV420SP_NV21, BitmapSwitcher.Format.BGRA_8888);

        //存储位图
        writeOverFileToEmmc(bgra8888, "bgra8888.rgb");
        //存储bmp文件观察
        ImageFileWrapper.bgra8888ToBmpFile(bgra8888, 150, 150,
                emmcPath + File.separator + "argb8888.bmp");

        logD("OK!");
    }


    private void testARGBToBitmapRender() {
        //2.把argb塞入 AndroidBitmap，使用ImageView 观察显示

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