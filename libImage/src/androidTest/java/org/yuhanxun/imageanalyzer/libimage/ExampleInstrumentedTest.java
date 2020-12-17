package org.yuhanxun.imageanalyzer.libimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    final String TAG = "ExampleInstrumentedTest";
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("org.yuhanxun.imageanalyzer.libimage.test", appContext.getPackageName());
    }

    @Test
    public void testBitmapUtil() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        byte[] imgFileByteArr = ResRW.getByteArrFromAssets(appContext, "1280x720.jpg");
        Bitmap bitmap = BitmapFactory.decodeByteArray(imgFileByteArr, 0, imgFileByteArr.length);
        log("img w:"+bitmap.getWidth()+" h:"+bitmap.getHeight()+ "format:"+bitmap.getConfig());

        byte[] argb = BitmapAndroid.argb8888BitmapToByteArr(bitmap);

        BitmapAndroid.argb8888ToBmpFile(argb,1280,720,"/sdcard/haha.bmp");
    }

    private void log(String msg) {
        Log.d(TAG,msg);
    }
}